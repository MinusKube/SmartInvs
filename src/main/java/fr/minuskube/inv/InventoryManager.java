/*
 * Copyright 2018-2020 Isaac Montagne
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.minuskube.inv;

import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import fr.minuskube.inv.opener.ChestInventoryOpener;
import fr.minuskube.inv.opener.InventoryOpener;
import fr.minuskube.inv.opener.SpecialInventoryOpener;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class InventoryManager {

    private final JavaPlugin plugin;
    private final PluginManager pluginManager;

    private final Map<Inventory, SmartInventory> inventories;
    private final Map<Inventory, InventoryContents> contents;
    private final Map<Inventory, BukkitRunnable> updateTasks;

    private final List<InventoryOpener> defaultOpeners;
    private final List<InventoryOpener> openers;

    public InventoryManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.pluginManager = Bukkit.getPluginManager();

        this.inventories = new HashMap<>();
        this.contents = new HashMap<>();
        this.updateTasks = new HashMap<>();

        this.defaultOpeners = Arrays.asList(
                new ChestInventoryOpener(),
                new SpecialInventoryOpener()
        );

        this.openers = new ArrayList<>();
    }

    public void init() {
        pluginManager.registerEvents(new InvListener(), plugin);

//        new InvTask().runTaskTimer(plugin, 1, 1);
    }

    public Optional<InventoryOpener> findOpener(InventoryType type) {
        Optional<InventoryOpener> opInv = this.openers.stream()
                .filter(opener -> opener.supports(type))
                .findAny();

        if(!opInv.isPresent()) {
            opInv = this.defaultOpeners.stream()
                    .filter(opener -> opener.supports(type))
                    .findAny();
        }

        return opInv;
    }

    public void registerOpeners(InventoryOpener... openers) {
        this.openers.addAll(Arrays.asList(openers));
    }

    public List<Player> getOpenedPlayers(SmartInventory inv) {
        List<Player> list = new ArrayList<>();

        // TODO: this can be optimized?
        // if we use regular for-loop and break once find the inventory, average execution time can be dropped.
        // Worst case would be O(n) anyway, so could someone come up with better idea?
        this.inventories.forEach((Inventory, playerInv) -> {
            if(inv.equals(playerInv))
                Inventory.getViewers().stream()
                        .filter(Player.class::isInstance)
                        .map(Player.class::cast)
                        .forEach(list::add);
        });

        return list;
    }

    public Optional<SmartInventory> getInventory(Inventory inventory) {
        return Optional.ofNullable(this.inventories.get(inventory));
    }

    protected void setInventory(Inventory inventory, SmartInventory inv) {
        if(inv == null)
            this.inventories.remove(inventory);
        else
            this.inventories.put(inventory, inv);
    }

    public Optional<InventoryContents> getContents(Inventory inventory) {
        return Optional.ofNullable(this.contents.get(inventory));
    }

    protected void setContents(Inventory inventory, InventoryContents contents) {
        if(contents == null)
            this.contents.remove(inventory);
        else
            this.contents.put(inventory, contents);
    }
    
    protected void scheduleUpdateTask(Inventory inventory, SmartInventory inv) {
    	PlayerInvTask task = new PlayerInvTask(inventory, inv.getProvider(), contents.get(inventory));
    	task.runTaskTimer(plugin, 1, inv.getUpdateFrequency());
    	this.updateTasks.put(inventory, task);
    }
    
    protected void cancelUpdateTask(Inventory inventory) {
    	if(updateTasks.containsKey(inventory)) {
          int bukkitTaskId = this.updateTasks.get(inventory).getTaskId();
          Bukkit.getScheduler().cancelTask(bukkitTaskId);
          this.updateTasks.remove(inventory);
    	}
    }

    @SuppressWarnings("unchecked")
    class InvListener implements Listener {

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryClick(InventoryClickEvent e) {
            Player player = (Player) e.getWhoClicked();
            Inventory inventory = e.getInventory();
            SmartInventory inv = inventories.get(inventory);
            
            if(inv == null)
                return;

            if( e.getAction() == InventoryAction.COLLECT_TO_CURSOR ||
                e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY ||
                e.getAction() == InventoryAction.NOTHING) {

                e.setCancelled(true);
                return;
            }

            if(inventory == player.getOpenInventory().getTopInventory()) {
                int row = e.getSlot() / 9;
                int column = e.getSlot() % 9;
                
                if(!inv.checkBounds(row, column))
                    return;

                InventoryContents invContents = contents.get(inventory);
                SlotPos slot = SlotPos.of(row, column);
                
                if(!invContents.isEditable(slot))
                    e.setCancelled(true);

                inv.getListeners().stream()
                        .filter(listener -> listener.getType() == InventoryClickEvent.class)
                        .forEach(listener -> ((InventoryListener<InventoryClickEvent>) listener).accept(e));

                invContents.get(slot).ifPresent(item -> item.run(new ItemClickData(e, player, e.getCurrentItem(), slot)));

                // Don't update if the clicked slot is editable - prevent item glitching
                if(!invContents.isEditable(slot)) {
                    player.updateInventory();
                }
            }
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryDrag(InventoryDragEvent e) {
            Player player = (Player) e.getWhoClicked();
            Inventory inventory = e.getInventory();

            if(!inventories.containsKey(inventory))
                return;

            SmartInventory inv = inventories.get(inventory);

            for(int slot : e.getRawSlots()) {
                if(slot >= player.getOpenInventory().getTopInventory().getSize())
                    continue;

                e.setCancelled(true);
                break;
            }

            inv.getListeners().stream()
                    .filter(listener -> listener.getType() == InventoryDragEvent.class)
                    .forEach(listener -> ((InventoryListener<InventoryDragEvent>) listener).accept(e));
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryOpen(InventoryOpenEvent e) {
            Inventory inventory = e.getInventory();

            if(!inventories.containsKey(inventory))
                return;

            SmartInventory inv = inventories.get(inventory);

            inv.getListeners().stream()
                    .filter(listener -> listener.getType() == InventoryOpenEvent.class)
                    .forEach(listener -> ((InventoryListener<InventoryOpenEvent>) listener).accept(e));
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryClose(InventoryCloseEvent e) {
            Player player = (Player) e.getPlayer();
            Inventory inventory = e.getInventory();

            if(!inventories.containsKey(inventory))
                return;

            SmartInventory inv = inventories.get(inventory);

            try{
                inv.getListeners().stream()
                        .filter(listener -> listener.getType() == InventoryCloseEvent.class)
                        .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener).accept(e));
            } finally {
                if(inv.isCloseable()) {
                    e.getInventory().clear();
                    InventoryManager.this.cancelUpdateTask(inventory);

                    inventories.remove(inventory);
                    contents.remove(inventory);
                }
                else
                    Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(e.getInventory()));
            }
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onPlayerQuit(PlayerQuitEvent e) {
            //This is no longer needed if we keep track of Inventory instead of Player
            //InventoryCloseEvent will be called when a player quit
            //Downside: Some way to pass PlayerQuitEvent to the listener is needed.
            //          Or, is PlayerQuitEvent really useful in GUI context?
//            Inventory inventory = e.getPlayer();
//
//            if(!inventories.containsKey(p))
//                return;
//
//            SmartInventory inv = inventories.get(p);
//
//            try{
//                inv.getListeners().stream()
//                        .filter(listener -> listener.getType() == PlayerQuitEvent.class)
//                        .forEach(listener -> ((InventoryListener<PlayerQuitEvent>) listener).accept(e));
//            } finally {
//                inventories.remove(p);
//                contents.remove(p);
//            }
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onPluginDisable(PluginDisableEvent e) {
            new HashMap<>(inventories).forEach((inventory, smartInv) -> {
                try {
                    smartInv.getListeners().stream()
                            .filter(listener -> listener.getType() == PluginDisableEvent.class)
                            .forEach(listener -> ((InventoryListener<PluginDisableEvent>) listener).accept(e));
                } finally {
                    inventory.getViewers().stream()
                            .filter(Player.class::isInstance)
                            .map(Player.class::cast)
                            .forEach(HumanEntity::closeInventory);
                    //smartInv.close(player);
                }
            });

            inventories.clear();
            contents.clear();
        }

    }

    class InvTask extends BukkitRunnable {

        @Override
        public void run() {
            new HashMap<>(inventories).forEach((player, inv) -> inv.getProvider().update(player, contents.get(player)));
        }

    }
    
    class PlayerInvTask extends BukkitRunnable {

        private Inventory inventorylayer;
        private InventoryProvider provider;
        private InventoryContents contents;

        public PlayerInvTask(Inventory inventorylayer, InventoryProvider provider, InventoryContents contents) {
          this.inventorylayer = Objects.requireNonNull(inventorylayer);
          this.provider = Objects.requireNonNull(provider);
          this.contents = Objects.requireNonNull(contents);
        }

        @Override
        public void run() {
            provider.update(this.inventorylayer, this.contents);
        }
    	
    }

}

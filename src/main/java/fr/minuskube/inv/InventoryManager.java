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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class InventoryManager {

    private final JavaPlugin plugin;
    private final PluginManager pluginManager;

    private final Map<Player, SmartInventory> inventories;
    private final Map<Player, InventoryContents> contents;
    private final Map<Player, BukkitRunnable> updateTasks;

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

        this.inventories.forEach((player, playerInv) -> {
            if(inv.equals(playerInv))
                list.add(player);
        });

        return list;
    }

    public Optional<SmartInventory> getInventory(Player p) {
        return Optional.ofNullable(this.inventories.get(p));
    }

    protected void setInventory(Player p, SmartInventory inv) {
        if(inv == null)
            this.inventories.remove(p);
        else
            this.inventories.put(p, inv);
    }

    public Optional<InventoryContents> getContents(Player p) {
        return Optional.ofNullable(this.contents.get(p));
    }

    protected void setContents(Player p, InventoryContents contents) {
        if(contents == null)
            this.contents.remove(p);
        else
            this.contents.put(p, contents);
    }
    
    protected void scheduleUpdateTask(Player p, SmartInventory inv) {
    	PlayerInvTask task = new PlayerInvTask(p, inv.getProvider(), contents.get(p));
    	task.runTaskTimer(plugin, 1, inv.getUpdateFrequency());
    	this.updateTasks.put(p, task);
    }
    
    protected void cancelUpdateTask(Player p) {
    	if(updateTasks.containsKey(p)) {
          int bukkitTaskId = this.updateTasks.get(p).getTaskId();
          Bukkit.getScheduler().cancelTask(bukkitTaskId);
          this.updateTasks.remove(p);
    	}
    }

    @SuppressWarnings("unchecked")
    class InvListener implements Listener {

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryClick(InventoryClickEvent e) {
            Player p = (Player) e.getWhoClicked();
            SmartInventory inv = inventories.get(p);
            
            if(inv == null)
                return;

            if( e.getAction() == InventoryAction.COLLECT_TO_CURSOR ||
                e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY ||
                e.getAction() == InventoryAction.NOTHING) {

                e.setCancelled(true);
                return;
            }

            if(e.getClickedInventory() == p.getOpenInventory().getTopInventory()) {
                int row = e.getSlot() / 9;
                int column = e.getSlot() % 9;
                
                if(!inv.checkBounds(row, column))
                    return;

                InventoryContents invContents = contents.get(p);
                SlotPos slot = SlotPos.of(row, column);
                
                if(!invContents.isEditable(slot))
                    e.setCancelled(true);

                inv.getListeners().stream()
                        .filter(listener -> listener.getType() == InventoryClickEvent.class)
                        .forEach(listener -> ((InventoryListener<InventoryClickEvent>) listener).accept(e));

                invContents.get(slot).ifPresent(item -> item.run(new ItemClickData(e, p, e.getCurrentItem(), slot)));

                // Don't update if the clicked slot is editable - prevent item glitching
                if(!invContents.isEditable(slot)) {
                    p.updateInventory();
                }
            }
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryDrag(InventoryDragEvent e) {
            Player p = (Player) e.getWhoClicked();

            if(!inventories.containsKey(p))
                return;

            SmartInventory inv = inventories.get(p);

            for(int slot : e.getRawSlots()) {
                if(slot >= p.getOpenInventory().getTopInventory().getSize())
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
            Player p = (Player) e.getPlayer();

            if(!inventories.containsKey(p))
                return;

            SmartInventory inv = inventories.get(p);

            inv.getListeners().stream()
                    .filter(listener -> listener.getType() == InventoryOpenEvent.class)
                    .forEach(listener -> ((InventoryListener<InventoryOpenEvent>) listener).accept(e));
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryClose(InventoryCloseEvent e) {
            Player p = (Player) e.getPlayer();

            if(!inventories.containsKey(p))
                return;

            SmartInventory inv = inventories.get(p);

            try{
                inv.getListeners().stream()
                        .filter(listener -> listener.getType() == InventoryCloseEvent.class)
                        .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener).accept(e));
            } finally {
                if(inv.isCloseable()) {
                    e.getInventory().clear();
                    InventoryManager.this.cancelUpdateTask(p);

                    inventories.remove(p);
                    contents.remove(p);
                }
                else
                    Bukkit.getScheduler().runTask(plugin, () -> p.openInventory(e.getInventory()));
            }
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onPlayerQuit(PlayerQuitEvent e) {
            Player p = e.getPlayer();

            if(!inventories.containsKey(p))
                return;

            SmartInventory inv = inventories.get(p);

            try{
                inv.getListeners().stream()
                        .filter(listener -> listener.getType() == PlayerQuitEvent.class)
                        .forEach(listener -> ((InventoryListener<PlayerQuitEvent>) listener).accept(e));
            } finally {
                inventories.remove(p);
                contents.remove(p);
            }
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onPluginDisable(PluginDisableEvent e) {
            new HashMap<>(inventories).forEach((player, inv) -> {
                try{
                    inv.getListeners().stream()
                            .filter(listener -> listener.getType() == PluginDisableEvent.class)
                            .forEach(listener -> ((InventoryListener<PluginDisableEvent>) listener).accept(e));
                } finally {
                    inv.close(player);
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

        private Player player;
        private InventoryProvider provider;
        private InventoryContents contents;

        public PlayerInvTask(Player player, InventoryProvider provider, InventoryContents contents) {
          this.player = Objects.requireNonNull(player);
          this.provider = Objects.requireNonNull(provider);
          this.contents = Objects.requireNonNull(contents);
        }

        @Override
        public void run() {
            provider.update(this.player, this.contents);
        }
    	
    }

}

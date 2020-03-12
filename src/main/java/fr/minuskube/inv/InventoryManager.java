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
import fr.minuskube.inv.internal.PlayerInvTask;
import fr.minuskube.inv.opener.ChestInventoryOpener;
import fr.minuskube.inv.opener.InventoryOpener;
import fr.minuskube.inv.opener.SpecialInventoryOpener;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

public final class InventoryManager {

    private final Plugin plugin;

    private final PluginManager pluginmanager = Bukkit.getPluginManager();

    private final Map<Player, SmartInventory> inventories = new HashMap<>();

    private final Map<Player, InventoryContents> contents = new HashMap<>();

    private final Map<Player, BukkitRunnable> tasks = new HashMap<>();

    private final List<InventoryOpener> defaulters = Arrays.asList(
        new ChestInventoryOpener(), new SpecialInventoryOpener()
    );

    private final Collection<InventoryOpener> openers = new ArrayList<>();

    public InventoryManager(final Plugin plgn) {
        this.plugin = plgn;
    }

    public void init() {
        this.pluginmanager.registerEvents(new InvListener(this), this.plugin);
        // FIXME: 23.02.2020 What's this for and are we need that? If we don't need remove.
        //  new InvTask().runTaskTimer(plugin, 1, 1);
    }
    // FIXME: 23.02.2020 What's this for and do we need that? If we don't need remove.
    //    class InvTask extends BukkitRunnable {
    //        @Override
    //        public void run() {
    //            new HashMap<>(InventoryManager.this.inventories).forEach((player, inv) ->
    //                inv.getProvider().update(player, InventoryManager.this.contents.get(player))
    //            );
    //        }
    //    }

    public Optional<InventoryOpener> findOpener(final InventoryType type) {
        final Optional<InventoryOpener> optional = this.openers.stream()
            .filter(opener -> opener.supports(type))
            .findAny();
        if (optional.isPresent()) {
            return optional;
        }
        return this.defaulters.stream().filter(opener -> opener.supports(type)).findAny();
    }

    public void registerOpeners(final InventoryOpener... opnrs) {
        this.openers.addAll(Arrays.asList(opnrs));
    }

    public List<Player> getOpenedPlayers(final SmartInventory inv) {
        final List<Player> list = new ArrayList<>();
        this.inventories.forEach((player, playerInv) -> {
            if (inv.equals(playerInv)) {
                list.add(player);
            }
        });
        return list;
    }

    public Optional<SmartInventory> getInventory(final Player player) {
        return Optional.ofNullable(this.inventories.get(player));
    }

    public Optional<InventoryContents> getContents(final Player player) {
        return Optional.ofNullable(this.contents.get(player));
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public Map<Player, SmartInventory> getInventories() {
        return Collections.unmodifiableMap(this.inventories);
    }

    public void removeInventory(final Player player) {
        this.inventories.remove(player);
    }

    public void removeContent(final Player player) {
        this.contents.remove(player);
    }

    public void clearInventories() {
        this.inventories.clear();
    }

    public void clearContents() {
        this.contents.clear();
    }

    void setInventory(final Player player, final SmartInventory inv) {
        if (inv == null) {
            this.inventories.remove(player);
        } else {
            this.inventories.put(player, inv);
        }
    }

    void setContents(final Player player, final InventoryContents contest) {
        if (contest == null) {
            this.contents.remove(player);
        } else {
            this.contents.put(player, contest);
        }
    }

    void scheduleUpdateTask(final Player player, final SmartInventory inv) {
        final PlayerInvTask task =
            new PlayerInvTask(player, inv.getProvider(), this.contents.get(player));
        task.runTaskTimer(this.plugin, 1L, inv.getUpdateFrequency());
        this.tasks.put(player, task);
    }

    void cancelUpdateTask(final Player player) {
        if (this.tasks.containsKey(player)) {
            final int id = this.tasks.get(player).getTaskId();
            Bukkit.getScheduler().cancelTask(id);
            this.tasks.remove(player);
        }
    }

}

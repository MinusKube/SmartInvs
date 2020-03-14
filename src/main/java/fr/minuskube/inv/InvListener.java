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

import fr.minuskube.inv.content.SlotPos;
import fr.minuskube.inv.internal.InventoryListener;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

/**
 * TODO: Write well javadoc.
 *
 * @since 1.3.0
 */
@SuppressWarnings("unchecked")
final class InvListener implements Listener {

    private final InventoryManager manager;

    InvListener(final InventoryManager mngr) {
        this.manager = mngr;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }
        final Player player = (Player) event.getWhoClicked();
        this.manager.getInventory(player).ifPresent(inv -> {
            if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR ||
                event.getAction() == InventoryAction.NOTHING) {
                event.setCancelled(true);
                return;
            }
            if (!event.getClickedInventory().equals(player.getOpenInventory().getTopInventory())) {
                return;
            }
            final int row = event.getSlot() / 9;
            final int column = event.getSlot() % 9;
            if (!inv.checkBounds(row, column)) {
                return;
            }
            this.manager.getContents(player).ifPresent(contents -> {
                final SlotPos slot = SlotPos.of(row, column);
                if (!contents.isEditable(slot)) {
                    event.setCancelled(true);
                }
                inv.getListeners().stream()
                    .filter(listener -> listener.getType().equals(InventoryClickEvent.class))
                    // FIXME: 23.02.2020 We should not use casting.
                    .forEach(listener -> ((InventoryListener<InventoryClickEvent>) listener).accept(event));
                contents.get(slot).ifPresent(item ->
                    item.run(new ItemClickData(event, player, event.getCurrentItem(), slot))
                );
                // Don't update if the clicked slot is editable - prevent item glitching
                if (!contents.isEditable(slot)) {
                    player.updateInventory();
                }
            });
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryDrag(final InventoryDragEvent event) {
        final Player player = (Player) event.getWhoClicked();
        this.manager.getInventory(player).ifPresent(inv -> {
            for (final int slot : event.getRawSlots()) {
                if (slot >= player.getOpenInventory().getTopInventory().getSize()) {
                    continue;
                }
                event.setCancelled(true);
                break;
            }
            inv.getListeners().stream()
                .filter(listener -> listener.getType().equals(InventoryDragEvent.class))
                // FIXME: 23.02.2020 We should not use casting.
                .forEach(listener -> ((InventoryListener<InventoryDragEvent>) listener).accept(event));
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryOpen(final InventoryOpenEvent event) {
        final Player player = (Player) event.getPlayer();
        this.manager.getInventory(player).ifPresent(inv ->
            inv.getListeners().stream()
                .filter(listener -> listener.getType().equals(InventoryOpenEvent.class))
                // FIXME: 23.02.2020 We should not use casting.
                .forEach(listener -> ((InventoryListener<InventoryOpenEvent>) listener).accept(event))
        );
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClose(final InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        this.manager.getInventory(player).ifPresent(inv -> {
            inv.getListeners().stream()
                .filter(listener -> listener.getType().equals(InventoryCloseEvent.class))
                // FIXME: 23.02.2020 We should not use casting.
                .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener).accept(event));
            if (!inv.isCloseable()) {
                Bukkit.getScheduler().runTask(this.manager.getPlugin(), () ->
                    player.openInventory(event.getInventory())
                );
                return;
            }
            event.getInventory().clear();
            this.manager.cancelUpdateTask(player);
            this.manager.removeInventory(player);
            this.manager.removeContent(player);
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        this.manager.getInventory(player).ifPresent(inv -> {
            inv.getListeners().stream()
                .filter(listener -> listener.getType().equals(PlayerQuitEvent.class))
                // FIXME: 23.02.2020 We should not use casting.
                .forEach(listener -> ((InventoryListener<PlayerQuitEvent>) listener).accept(event));
            this.manager.removeInventory(player);
            this.manager.removeContent(player);
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPluginDisable(final PluginDisableEvent event) {
        new HashMap<>(this.manager.getInventories()).forEach((player, inv) -> {
            inv.getListeners().stream()
                .filter(listener -> listener.getType().equals(PluginDisableEvent.class))
                // FIXME: 23.02.2020 We should not use casting.
                .forEach(listener -> ((InventoryListener<PluginDisableEvent>) listener).accept(event));
            inv.close(player);
        });
        this.manager.clearInventories();
        this.manager.clearContents();
    }

}

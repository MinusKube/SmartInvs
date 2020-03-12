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

package fr.minuskube.inv.opener;

import com.google.common.collect.ImmutableList;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public final class SpecialInventoryOpener implements InventoryOpener {

    private static final List<InventoryType> SUPPORTED = ImmutableList.of(
        InventoryType.FURNACE,
        InventoryType.WORKBENCH,
        InventoryType.DISPENSER,
        InventoryType.DROPPER,
        InventoryType.ENCHANTING,
        InventoryType.BREWING,
        InventoryType.ANVIL,
        InventoryType.BEACON,
        InventoryType.HOPPER
    );

    @Override
    public Inventory open(final SmartInventory inv, final Player player) {
        final InventoryManager manager = inv.getManager();
        final Inventory handle = Bukkit.createInventory(player, inv.getType(), inv.getTitle());
        this.fill(handle, manager.getContents(player).get(), player);
        player.openInventory(handle);
        return handle;
    }

    @Override
    public boolean supports(final InventoryType type) {
        return SpecialInventoryOpener.SUPPORTED.contains(type);
    }

}

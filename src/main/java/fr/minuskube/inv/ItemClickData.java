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
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class ItemClickData {

    @NotNull
    private final Event event;

    @NotNull
    private final Player player;

    @NotNull
    private final ItemStack item;

    @NotNull
    private final SlotPos slot;

    public ItemClickData(@NotNull final Event event, @NotNull final Player player, @NotNull final ItemStack item,
                         @NotNull final SlotPos slot) {
        this.event = event;
        this.player = player;
        this.item = item;
        this.slot = slot;
    }

    @NotNull
    public Event getEvent() {
        return this.event;
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @NotNull
    public ItemStack getItem() {
        return this.item;
    }

    @NotNull
    public SlotPos getSlot() {
        return this.slot;
    }

}

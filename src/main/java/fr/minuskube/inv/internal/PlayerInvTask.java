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

package fr.minuskube.inv.internal;

import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: Write well javadoc.
 *
 * @since 1.0
 */
public final class PlayerInvTask extends BukkitRunnable {

    @NotNull
    private final Player player;

    @NotNull
    private final InventoryProvider provider;

    @NotNull
    private final InventoryContents contents;

    public PlayerInvTask(@NotNull final Player plyr, @NotNull final InventoryProvider prvdr,
                  @NotNull final InventoryContents cntnts) {
        super();
        this.player = plyr;
        this.provider = prvdr;
        this.contents = cntnts;
    }

    @Override
    public void run() {
        this.provider.update(this.player, this.contents);
    }

}

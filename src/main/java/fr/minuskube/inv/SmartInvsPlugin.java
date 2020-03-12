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

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Main plugin class of SmartInventory plugin/library.
 *
 * @since 1.0
 */
public final class SmartInvsPlugin extends JavaPlugin {

    @Nullable
    private static Plugin instance;

    @Nullable
    private static InventoryManager manager;

    @NotNull
    public static InventoryManager getManager() {
        if (SmartInvsPlugin.manager == null) {
            throw new IllegalStateException("You can't use SmartInvsPlugin#getInstance() method before initialization!");
        }
        return SmartInvsPlugin.manager;
    }

    @NotNull
    public static Plugin getInstance() {
        if (SmartInvsPlugin.instance == null) {
            throw new IllegalStateException("You can't use SmartInvsPlugin#getInstance() method before initialization!");
        }
        return SmartInvsPlugin.instance;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        SmartInvsPlugin.deleteStaticReferences();
    }

    private static void deleteStaticReferences() {
        SmartInvsPlugin.instance = null;
        SmartInvsPlugin.manager = null;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        SmartInvsPlugin.setPlugin(this);
    }

    private static void setPlugin(@NotNull final Plugin plugin) {
        SmartInvsPlugin.instance = plugin;
        SmartInvsPlugin.manager = new InventoryManager(plugin);
        SmartInvsPlugin.manager.init();
    }

}

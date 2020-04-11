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

import org.bukkit.plugin.java.JavaPlugin;

public class SmartInvsPlugin extends JavaPlugin {

    private static JavaPlugin instance;
    private static InventoryManager invManager;

    @Override
    public void onEnable() {
        setPlugin(this);
    }

    @Override
    public void onDisable() {
        deleteStaticReferences();
    }

    public static InventoryManager manager() { return invManager; }
    public static JavaPlugin instance() { return instance; }

    public static void setPlugin(JavaPlugin javaPlugin) {
        instance = javaPlugin;
        invManager = new InventoryManager(javaPlugin);
        invManager.init();
    }

    public static void deleteStaticReferences() {
        instance = null;
        invManager = null;
    }
}

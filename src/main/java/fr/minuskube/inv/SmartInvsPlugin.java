package fr.minuskube.inv;

import org.bukkit.plugin.java.JavaPlugin;

public class SmartInvsPlugin extends JavaPlugin {

    private static SmartInvsPlugin instance;
    private static InventoryManager invManager;

    @Override
    public void onEnable() {
        instance = this;
        initInventoryManager(this);
    }

    public static void initInventoryManager(JavaPlugin plugin) {
        invManager = new InventoryManager(plugin);
        invManager.init();
    }

    public static InventoryManager manager() { return invManager; }
    public static SmartInvsPlugin instance() { return instance; }

}

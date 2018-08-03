package fr.minuskube.inv;

import org.bukkit.plugin.java.JavaPlugin;

public class SmartInvsPlugin extends JavaPlugin {

    private static SmartInvsPlugin instance;
    private static InventoryManager invManager;

    @Override
    public void onEnable() {
        instance = this;

        invManager = new InventoryManager(this);
        invManager.init();
    }

    @Override
    public void onDisable() {
        instance = null;
        invManager = null;
    }

    public static InventoryManager manager() { return invManager; }
    public static SmartInvsPlugin instance() { return instance; }

}

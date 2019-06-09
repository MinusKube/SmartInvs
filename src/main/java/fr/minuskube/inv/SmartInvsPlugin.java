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

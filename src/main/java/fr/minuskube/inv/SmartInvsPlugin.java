package fr.minuskube.inv;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * SmartInvs main plugin class.
 * <p>
 * @author MinusKube
 * @version 1.2.7
 */
public class SmartInvsPlugin extends JavaPlugin {

    private static SmartInvsPlugin instance;
    private static InventoryManager invManager;

    /**
     * This method is called when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        instance = this;

        invManager = new InventoryManager(this);
        invManager.init();
    }

    /**
     * Creates an instance of the Inventory Manager class.
     *
     * @return inventory manager instance
     */
    public static InventoryManager manager() { return invManager; }

    /**
     * Creates an instance of the main SmartInvs plugin class.
     *
     * @return SmartInvs instance
     */
    public static SmartInvsPlugin instance() { return instance; }

}

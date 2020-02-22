package fr.minuskube.inv;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SmartInvsPlugin extends JavaPlugin {

    @Nullable
    private static Plugin instance = null;

    @Nullable
    private static InventoryManager manager = null;

    @Override
    public void onEnable() {
        super.onEnable();
        SmartInvsPlugin.setPlugin(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        SmartInvsPlugin.deleteStaticReferences();
    }

    public static InventoryManager getManager() {
        if (SmartInvsPlugin.manager == null) {
            throw new IllegalStateException("You can't use SmartInvsPlugin#getManager before it start!");
        }
        return SmartInvsPlugin.manager;
    }

    public static Plugin getInstance() {
        if (SmartInvsPlugin.instance == null) {
            throw new IllegalStateException("You can't use SmartInvsPlugin#getInstance before it start!");
        }
        return SmartInvsPlugin.instance;
    }

    private static void setPlugin(@NotNull final Plugin plugin) {
        SmartInvsPlugin.instance = plugin;
        SmartInvsPlugin.manager = new InventoryManager(plugin);
        SmartInvsPlugin.manager.init();
    }

    private static void deleteStaticReferences() {
        SmartInvsPlugin.instance = null;
        SmartInvsPlugin.manager = null;
    }
}

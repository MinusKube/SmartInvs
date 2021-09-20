package fr.minuskube.inv.content;

import org.bukkit.entity.Player;

public interface InventoryProvider {

    /**
     * Called when a player open a SmartInventory.
     * @param player the player
     * @param contents the inventory content
     */
    void init(Player player, InventoryContents contents);

    /**
     * Called every tick. This method updates all opened SmartInventories.
     * The delay time can be customized via {@link fr.minuskube.inv.SmartInventory.Builder#delay(long)}.
     * @param player the player who's using the inventory
     * @param contents the inventory content
     */
    default void update(Player player, InventoryContents contents) {}

    /**
     * Called when an error occurs while trying to open / update a SmartInventory.
     * The error message can be found via {@link Throwable#getMessage()}.
     * @param exception the exception which occurred
     */
    default void error(RuntimeException exception) {}
}

package fr.minuskube.inv.content;

import org.bukkit.entity.Player;

/**
 * Inventory Provider Interface
 */
public interface InventoryProvider {
    /**
     * Will check for all the contents inside the SmartInvs and get the given player.
     *
     * @param player Get the given player
     * @param contents Get all the contents inside the SmartInvs
     */
    void init(Player player, InventoryContents contents);

    /**
     * Runs a hashmap to update the player with the given contents inside the SmartInvs.
     *
     * @param player Get given player
     * @param contents Get all the contents inside the SmartInvs
     */
    default void update(Player player, InventoryContents contents) {}

}

package fr.minuskube.inv.content;

import org.bukkit.entity.Player;

public interface InventoryProvider {

    default init(Player player, InventoryContents contents) {update(player, contents)}
    default void update(Player player, InventoryContents contents) {};

}

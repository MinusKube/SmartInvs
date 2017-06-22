package fr.minuskube.inv.opener;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.SmartInvsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.List;

public class ChestInventoryOpener implements InventoryOpener {

    private final List<InventoryType> supported = Arrays.asList(
            InventoryType.CHEST,
            InventoryType.ENDER_CHEST
    );

    @Override
    public Inventory open(SmartInventory inv, Player player) {
        InventoryManager manager = SmartInvsPlugin.manager();
        Inventory handle = Bukkit.createInventory(player, inv.getRows() * inv.getColumns(), inv.getTitle());

        ClickableItem[][] contents = manager.getContents(player).get().all();

        for(int row = 0; row < contents.length; row++) {
            for(int column = 0; column < contents[row].length; column++) {
                if(contents[row][column] != null)
                    handle.setItem(9 * row + column, contents[row][column].getItem());
            }
        }

        player.openInventory(handle);
        return handle;
    }

    @Override
    public boolean supports(InventoryType type) {
        return supported.contains(type);
    }

}

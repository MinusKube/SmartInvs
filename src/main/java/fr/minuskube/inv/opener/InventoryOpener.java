package fr.minuskube.inv.opener;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public interface InventoryOpener {

    Inventory open(SmartInventory inv, Player player);
    boolean supports(InventoryType type);

    default void fill(Inventory handle, InventoryContents contents) {
        ClickableItem[][] items = contents.all();

        for(int row = 0; row < items.length; row++) {
            for(int column = 0; column < items[row].length; column++) {
                if(items[row][column] != null)
                    handle.setItem(9 * row + column, items[row][column].getItem());
            }
        }
    }
    
    /**
     * This method is used to configure the default inventory size(s)
     * for inventories supported by this opener. These values will only
     * be applied if the size is not set explicitly. (See {@link SmartInventory.Builder#size(int, int)}).
     * <p>
     * This method must return a non-null value for all supported inventory types.
     * @param type inventory type
     * @return The desired default dimensions, this default implementation returns
     *         (3x9) for type (ender)chest, (3x3) for dispenser & dropper and
     *         (1x_sizeOfInventoryType_) for everything else.
     */
    default SlotPos defaultSize(InventoryType type) {
        switch(type) {
            case CHEST:
            case ENDER_CHEST:
                return SlotPos.of(3, 9);
            case DISPENSER:
            case DROPPER:
                return SlotPos.of(3, 3);
            default:
                return SlotPos.of(1, type.getDefaultSize());
        }
    }

}

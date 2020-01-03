package fr.minuskube.inv.opener;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.event.inventory.InventoryType;
import org.junit.Test;
import fr.minuskube.inv.content.SlotPos;

public class InventoryOpenerTest {
    
    @Test
    public void testDefaultSize() {
        final Map<InventoryType, SlotPos> expectedSizes = new HashMap<>();
        
        expectedSizes.put(InventoryType.CHEST, SlotPos.of(3, 9));
        expectedSizes.put(InventoryType.ENDER_CHEST, SlotPos.of(3, 9));
        expectedSizes.put(InventoryType.DISPENSER, SlotPos.of(3, 3));
        expectedSizes.put(InventoryType.DROPPER, SlotPos.of(3, 3));
        expectedSizes.put(InventoryType.ENCHANTING, SlotPos.of(1, 2));
        expectedSizes.put(InventoryType.BREWING, SlotPos.of(1, 4));       // this line needs to be updated to SlotPos(1, 5) in the future (new brewing stand slot)
        expectedSizes.put(InventoryType.ANVIL, SlotPos.of(1, 3));
        expectedSizes.put(InventoryType.BEACON, SlotPos.of(1, 1));
        expectedSizes.put(InventoryType.HOPPER, SlotPos.of(1, 5));
        expectedSizes.put(InventoryType.FURNACE, SlotPos.of(1, 3));
        expectedSizes.put(InventoryType.WORKBENCH, SlotPos.of(1, 10));
        
        SpecialInventoryOpener opener = new SpecialInventoryOpener();
        
        expectedSizes.forEach((type, expectedSize) -> {
            assertEquals(expectedSize, opener.defaultSize(type));
        });
    }
}

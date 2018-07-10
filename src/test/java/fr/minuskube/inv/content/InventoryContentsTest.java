package fr.minuskube.inv.content;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InventoryContentsTest {

    private static final ItemStack TEST_ITEM = new ItemStack(Material.DIRT);

    @Test
    public void test() {
        InventoryManager manager = mock(InventoryManager.class);

        SmartInventory inv = mock(SmartInventory.class);
        when(inv.getRows()).thenReturn(6);
        when(inv.getColumns()).thenReturn(9);
        when(inv.getManager()).thenReturn(manager);

        InventoryContents contents = new InventoryContents.Impl(inv);

        for(int row = 0; row < inv.getRows(); row++) {
            for(int column = 0; column < inv.getColumns(); column++) {
                assertFalse(contents.get(row, column).isPresent());
                assertFalse(contents.get(SlotPos.of(row, column)).isPresent());
            }
        }

        contents.setProperty("test", "Test String");

        assertEquals(contents.property("test"), "Test String");
        assertNull(contents.property("unknownProperty"));

        contents.add(ClickableItem.empty(TEST_ITEM));

        assertTrue(contents.get(0, 0).isPresent());
        assertTrue(contents.get(SlotPos.of(0, 0)).isPresent());

        assertSame(contents.get(0, 0).get().getItem(), TEST_ITEM);
        assertSame(contents.get(SlotPos.of(0, 0)).get().getItem(), TEST_ITEM);

        assertTrue(contents.firstEmpty().isPresent());
        assertEquals(contents.firstEmpty().get(), SlotPos.of(0, 1));
    }

}

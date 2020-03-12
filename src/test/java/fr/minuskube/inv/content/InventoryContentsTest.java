package fr.minuskube.inv.content;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

public class InventoryContentsTest {

    private static final ItemStack TEST_ITEM = new ItemStack(Material.DIRT);

    private static final ClickableItem TEST_CLICKABLE = ClickableItem.empty(InventoryContentsTest.TEST_ITEM);

    @Test
    public void testAddItem() {
        final SmartInventory inv = this.mockInventory(6, 9);
        final InventoryContents contents = new InventoryContents.Impl(inv, null);

        contents.add(InventoryContentsTest.TEST_CLICKABLE);

        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 9; column++) {
                final Optional<ClickableItem> result = contents.get(SlotPos.of(row, column));

                if (row == 0 && column == 0) {
                    assertTrue(result.isPresent());
                    assertEquals(result.get(), InventoryContentsTest.TEST_CLICKABLE);
                } else {
                    assertFalse(result.isPresent());
                }
            }
        }
    }

    private SmartInventory mockInventory(final int rows, final int columns) {
        final InventoryManager manager = mock(InventoryManager.class);

        final SmartInventory inv = mock(SmartInventory.class);
        when(inv.getRows()).thenReturn(rows);
        when(inv.getColumns()).thenReturn(columns);
        when(inv.getManager()).thenReturn(manager);

        return inv;
    }

    @Test
    public void testAddNull() {
        final SmartInventory inv = this.mockInventory(6, 9);
        final InventoryContents contents = new InventoryContents.Impl(inv, null);

        contents.add(null);

        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 9; column++) {
                final Optional<ClickableItem> result = contents.get(SlotPos.of(row, column));

                assertFalse(result.isPresent());
            }
        }
    }

    @Test
    public void test() {
        final SmartInventory inv = this.mockInventory(6, 9);
        final InventoryContents contents = new InventoryContents.Impl(inv, null);

        for (int row = 0; row < inv.getRows(); row++) {
            for (int column = 0; column < inv.getColumns(); column++) {
                assertFalse(contents.get(row, column).isPresent());
                assertFalse(contents.get(SlotPos.of(row, column)).isPresent());
            }
        }

        contents.setProperty("test", "Test String");

        assertEquals(contents.property("test"), "Test String");
        assertNull(contents.property("unknownProperty"));

        contents.add(ClickableItem.empty(InventoryContentsTest.TEST_ITEM));

        assertTrue(contents.get(0, 0).isPresent());
        assertTrue(contents.get(SlotPos.of(0, 0)).isPresent());

        assertSame(contents.get(0, 0).get().getItem(), InventoryContentsTest.TEST_ITEM);
        assertSame(contents.get(SlotPos.of(0, 0)).get().getItem(), InventoryContentsTest.TEST_ITEM);

        assertTrue(contents.firstEmpty().isPresent());
        assertEquals(contents.firstEmpty().get(), SlotPos.of(0, 1));
    }

}

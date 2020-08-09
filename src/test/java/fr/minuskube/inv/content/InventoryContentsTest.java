package fr.minuskube.inv.content;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InventoryContentsTest {
    private static final ItemStack TEST_ITEM = new ItemStack(Material.DIRT);
    private static final ClickableItem TEST_CLICKABLE = ClickableItem.empty(TEST_ITEM);

    private SmartInventory mockInventory(int rows, int columns) {
        InventoryManager manager = mock(InventoryManager.class);

        SmartInventory inv = mock(SmartInventory.class);
        when(inv.getRows()).thenReturn(rows);
        when(inv.getColumns()).thenReturn(columns);
        when(inv.getManager()).thenReturn(manager);

        return inv;
    }

    @Test
    public void testAddItem() {
        SmartInventory inv = mockInventory(6, 9);
        InventoryContents contents = new InventoryContents.Impl(inv, null);

        contents.add(TEST_CLICKABLE);

        for(int row = 0; row < 6; row++) {
            for(int column = 0; column < 9; column++) {
                Optional<ClickableItem> result = contents.get(SlotPos.of(row, column));

                if(row == 0 && column == 0) {
                    assertTrue(result.isPresent());
                    assertEquals(result.get(), TEST_CLICKABLE);
                }
                else {
                    assertFalse(result.isPresent());
                }
            }
        }
    }

    @Test
    public void testAddNull() {
        SmartInventory inv = mockInventory(6, 9);
        InventoryContents contents = new InventoryContents.Impl(inv, null);

        contents.add(null);

        for(int row = 0; row < 6; row++) {
            for(int column = 0; column < 9; column++) {
                Optional<ClickableItem> result = contents.get(SlotPos.of(row, column));

                assertFalse(result.isPresent());
            }
        }
    }

    @Test
    public void test() {
        SmartInventory inv = mockInventory(6, 9);
        InventoryContents contents = new InventoryContents.Impl(inv, null);

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

    @Test
    public void testApplyRectRaw() {
        SmartInventory inv = mockInventory(6, 9);
        InventoryContents contents = new InventoryContents.Impl(inv, null);

        BiConsumer<Integer, Integer> mockConsumer = mock(BiConsumer.class);
        contents.applyRect(3,4,5,6, mockConsumer);

        verify(mockConsumer, times(1)).accept(eq(3), eq(4));
        verify(mockConsumer, times(1)).accept(eq(3), eq(5));
        verify(mockConsumer, times(1)).accept(eq(3), eq(6));

        verify(mockConsumer, times(1)).accept(eq(4), eq(4));
        verify(mockConsumer, times(1)).accept(eq(4), eq(5));
        verify(mockConsumer, times(1)).accept(eq(4), eq(6));

        verify(mockConsumer, times(1)).accept(eq(5), eq(4));
        verify(mockConsumer, times(1)).accept(eq(5), eq(5));
        verify(mockConsumer, times(1)).accept(eq(5), eq(6));
    }

    @Test
    public void testApplyRectSlot() {
        SmartInventory inv = mockInventory(6, 9);
        InventoryContents contents = new InventoryContents.Impl(inv, null);

        contents.set(0, 0, ClickableItem.empty(TEST_ITEM));
        contents.set(3, 4, ClickableItem.empty(TEST_ITEM));
        contents.set(4, 5, ClickableItem.empty(TEST_ITEM));
        contents.set(5, 6, ClickableItem.empty(TEST_ITEM));
        contents.set(6, 7, ClickableItem.empty(TEST_ITEM));

        Consumer<ClickableItem> mockConsumer = mock(Consumer.class);
        contents.applyRect(3,4,5,6, mockConsumer);

        verify(mockConsumer, times(3)).accept(any(ClickableItem.class));
    }
}

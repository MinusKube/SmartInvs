package fr.minuskube.inv.content;

import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SlotIteratorTest {

    private SlotIterator createIterator(int rows, int columns) {
        InventoryManager manager = mock(InventoryManager.class);

        SmartInventory inv = mock(SmartInventory.class);
        when(inv.getRows()).thenReturn(rows);
        when(inv.getColumns()).thenReturn(columns);
        when(inv.getManager()).thenReturn(manager);

        InventoryContents contents = new InventoryContents.Impl(inv);
        return contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0);
    }

    @Test
    public void testPreviousNext() {
        SlotIterator iterator = this.createIterator(3, 9);
    }

    @Test
    public void testStartedEnded() {
        SlotIterator iterator = this.createIterator(3, 9);

        assertFalse("The started() method returns true before the start", iterator.started());
        assertFalse("The ended() method returns true before the end", iterator.ended());

        iterator.next();

        assertTrue("The started() method returns false after next()", iterator.started());
        assertFalse("The ended() method returns true before the end", iterator.ended());

        for(int i = 0; i < 3 * 9 - 1; i++)
            iterator.next();

        assertTrue("The started() method returns false after multiple next()", iterator.started());
        assertTrue("The ended() method returns false at the end of the inventory", iterator.ended());

        iterator.next();

        assertTrue("The started() method returns false after the end of the inventory", iterator.started());
        assertTrue("The ended() method returns false after the end of the inventory", iterator.ended());

        iterator.previous();

        assertTrue("The started() method returns false after previous()", iterator.started());
        assertFalse("The ended() method returns true after previous()", iterator.ended());
    }

}

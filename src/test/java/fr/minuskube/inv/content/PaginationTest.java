package fr.minuskube.inv.content;

import static org.junit.Assert.*;
import fr.minuskube.inv.ClickableItem;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

public class PaginationTest {

    @Test
    public void test() {
        final Pagination pagination = new Pagination.Impl();

        final ClickableItem[] items = new ClickableItem[64];

        for (int i = 0; i < items.length; i++) {
            items[i] = ClickableItem.empty(new ItemStack(Material.STONE, i));
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(30);

        assertEquals(pagination.getPage(), 0);
        assertArrayEquals(Arrays.copyOfRange(items, 0, 30), pagination.getPageItems());

        pagination.previous();

        assertEquals(pagination.getPage(), 0);

        pagination.next();

        assertEquals(pagination.getPage(), 1);
        assertArrayEquals(Arrays.copyOfRange(items, 30, 60), pagination.getPageItems());

        pagination.next();

        assertEquals(pagination.getPage(), 2);
        assertArrayEquals(Arrays.copyOfRange(items, 60, 90), pagination.getPageItems());

        pagination.next();

        assertEquals(pagination.getPage(), 2);

        pagination.first();
        assertTrue(pagination.isFirst());
        assertFalse(pagination.isLast());
        assertEquals(pagination.getPage(), 0);

        pagination.last();

        assertFalse(pagination.isFirst());
        assertTrue(pagination.isLast());
        assertEquals(pagination.getPage(), 2);
    }

}

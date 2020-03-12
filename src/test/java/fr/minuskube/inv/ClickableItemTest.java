package fr.minuskube.inv;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

public class ClickableItemTest {

    @Test
    public void testNone() {
        final ClickableItem item = ClickableItem.NONE;

        assertNull("The item from ClickableItem.NONE is not null", item.getItem());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testLegacyInventoryConsumer() {
        final AtomicBoolean bool = new AtomicBoolean(false);

        final ClickableItem item = ClickableItem.of(null, event -> bool.set(true));

        final ItemClickData clickData = mock(ItemClickData.class);
        when(clickData.getEvent()).thenReturn(mock(InventoryClickEvent.class));

        item.run(clickData);

        assertTrue("The ClickableItem's consumer has not been called", bool.get());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testLegacyInteractConsumer() {
        final AtomicBoolean bool = new AtomicBoolean(false);

        final ClickableItem item = ClickableItem.of(null, event -> bool.set(true));

        final ItemClickData clickData = mock(ItemClickData.class);
        when(clickData.getEvent()).thenReturn(mock(PlayerInteractEvent.class));

        item.run(clickData);

        assertFalse("The ClickableItem's consumer has been called", bool.get());
    }

    @Test
    public void testInventoryConsumer() {
        final AtomicBoolean bool = new AtomicBoolean(false);

        final ClickableItem item = ClickableItem.from(null, event -> bool.set(true));

        final ItemClickData clickData = mock(ItemClickData.class);
        when(clickData.getEvent()).thenReturn(mock(InventoryClickEvent.class));

        item.run(clickData);

        assertTrue("The ClickableItem's consumer has not been called", bool.get());
    }

    @Test
    public void testInteractConsumer() {
        final AtomicBoolean bool = new AtomicBoolean(false);

        final ClickableItem item = ClickableItem.from(null, event -> bool.set(true));

        final ItemClickData clickData = mock(ItemClickData.class);
        when(clickData.getEvent()).thenReturn(mock(PlayerInteractEvent.class));

        item.run(clickData);

        assertTrue("The ClickableItem's consumer has not been called", bool.get());
    }

    @Test
    public void testClone() {
        final ItemStack itemStack = new ItemStack(Material.APPLE);

        final AtomicBoolean bool = new AtomicBoolean(false);

        final ClickableItem item = ClickableItem.from(null, event -> bool.set(true));
        final ClickableItem clone = item.clone(itemStack);
        clone.run(mock(ItemClickData.class));

        assertEquals("The cloned ClickableItem's item is wrong", clone.getItem(), itemStack);
        assertTrue("The cloned ClickableItem's consumer has not been called", bool.get());
    }

}

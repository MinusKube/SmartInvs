package fr.minuskube.inv;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClickableItemTest {

    @Test
    public void testNone() {
        ClickableItem item = ClickableItem.NONE;

        assertNull("The item from ClickableItem.NONE is not null", item.getItem());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testLegacyInventoryConsumer() {
        AtomicBoolean bool = new AtomicBoolean(false);

        ClickableItem item = ClickableItem.of(null, event -> bool.set(true));

        ItemClickData clickData = mock(ItemClickData.class);
        when(clickData.getEvent()).thenReturn(mock(InventoryClickEvent.class));

        item.run(clickData);

        assertTrue("The ClickableItem's consumer has not been called", bool.get());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testLegacyInteractConsumer() {
        AtomicBoolean bool = new AtomicBoolean(false);

        ClickableItem item = ClickableItem.of(null, event -> bool.set(true));

        ItemClickData clickData = mock(ItemClickData.class);
        when(clickData.getEvent()).thenReturn(mock(PlayerInteractEvent.class));

        item.run(clickData);

        assertFalse("The ClickableItem's consumer has been called", bool.get());
    }

    @Test
    public void testInventoryConsumer() {
        AtomicBoolean bool = new AtomicBoolean(false);

        ClickableItem item = ClickableItem.from(null, event -> bool.set(true));

        ItemClickData clickData = mock(ItemClickData.class);
        when(clickData.getEvent()).thenReturn(mock(InventoryClickEvent.class));

        item.run(clickData);

        assertTrue("The ClickableItem's consumer has not been called", bool.get());
    }

    @Test
    public void testInteractConsumer() {
        AtomicBoolean bool = new AtomicBoolean(false);

        ClickableItem item = ClickableItem.from(null, event -> bool.set(true));

        ItemClickData clickData = mock(ItemClickData.class);
        when(clickData.getEvent()).thenReturn(mock(PlayerInteractEvent.class));

        item.run(clickData);

        assertTrue("The ClickableItem's consumer has not been called", bool.get());
    }

    @Test
    public void testClone() {
        ItemStack itemStack = new ItemStack(Material.APPLE);

        AtomicBoolean bool = new AtomicBoolean(false);

        ClickableItem item = ClickableItem.from(null, event -> bool.set(true));
        ClickableItem clone = item.clone(itemStack);
        clone.run(mock(ItemClickData.class));

        assertEquals("The cloned ClickableItem's item is wrong", clone.getItem(), itemStack);
        assertTrue("The cloned ClickableItem's consumer has not been called", bool.get());
    }

}

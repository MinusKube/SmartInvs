package fr.minuskube.inv;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ClickableItem {

    private ItemStack item;
    private Consumer<InventoryClickEvent> consumer;

    private ClickableItem(boolean movable, ItemStack item, Consumer<InventoryClickEvent> consumer) {
        this.item = item;
        this.consumer = consumer;
    }

    public static ClickableItem fixedEmpty(ItemStack item) {
        return fixed(item, e -> {});
    }

    public static ClickableItem fixed(ItemStack item, Consumer<InventoryClickEvent> consumer) {
        return new ClickableItem(false, item, consumer);
    }

    public void run(InventoryClickEvent e) { consumer.accept(e); }

    public ItemStack getItem() { return item; }

}

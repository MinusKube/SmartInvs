package fr.minuskube.inv;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ClickableItem {

    private ItemStack item;
    private Consumer<InventoryClickEvent> consumer;

    private ClickableItem(ItemStack item, Consumer<InventoryClickEvent> consumer) {
        this.item = item;
        this.consumer = consumer;
    }

    public static ClickableItem empty(ItemStack item) {
        return of(item, e -> {});
    }

    public static ClickableItem of(ItemStack item, Consumer<InventoryClickEvent> consumer) {
        return new ClickableItem(item, consumer);
    }

    public void run(InventoryClickEvent e) { consumer.accept(e); }

    public ItemStack getItem() { return item; }

}

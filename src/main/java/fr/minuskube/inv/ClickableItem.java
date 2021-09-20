package fr.minuskube.inv;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ClickableItem {

    private final ItemStack item;
    private final Consumer<InventoryClickEvent> consumer;

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

    /**
     * Updates the {@code ItemStack} of a {@code ClickableItem} without changing its listener.
     * @param clickableItem the old {@code ClickableItem}
     * @param itemStack the new {@code ItemStack}
     * @return a new {@code ClickableItem} with its related {@code ItemStack} updated
     */
    public static ClickableItem updateItem(ClickableItem clickableItem, ItemStack itemStack) {
        return new ClickableItem(itemStack, clickableItem.consumer);
    }

    public void run(InventoryClickEvent e) { consumer.accept(e); }

    public ItemStack getItem() { return item; }

}

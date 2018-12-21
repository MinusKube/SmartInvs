package fr.minuskube.inv;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@SuppressWarnings({ "unchecked", "deprecation" })
public class ClickableItem {

    private ItemStack item;
    private Consumer<?> consumer;
    private boolean legacy;

    private ClickableItem(ItemStack item, Consumer<?> consumer, boolean legacy) {
        this.item = item;
        this.consumer = consumer;
        this.legacy = legacy;
    }

    public static ClickableItem empty(ItemStack item) {
        return from(item, data -> {});
    }

    /**
     * @deprecated Replaced by {@link ClickableItem#from(ItemStack, Consumer)}
     *
     * Creates a ClickableItem made of a given item and a given InventoryClickEvent's consumer.
     *
     * @param item the item
     * @param consumer the consumer which will be called when the item is clicked
     * @return the created ClickableItem
     */
    @Deprecated
    public static ClickableItem of(ItemStack item, Consumer<InventoryClickEvent> consumer) {
        return new ClickableItem(item, consumer, true);
    }

    /**
     * Creates a ClickableItem made of a given item and a given ItemClickData's consumer.
     *
     * @param item the item
     * @param consumer the consumer which will be called when the item is clicked
     * @return the created ClickableItem
     */
    public static ClickableItem from(ItemStack item, Consumer<ItemClickData> consumer) {
        return new ClickableItem(item, consumer, false);
    }

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public void run(InventoryClickEvent e) {
        if(!this.legacy)
            return;

        Consumer<InventoryClickEvent> legacyConsumer = (Consumer<InventoryClickEvent>) this.consumer;
        legacyConsumer.accept(e);
    }

    public void run(ItemClickData data) {
        if(this.legacy) {
            if(data.getEvent() instanceof InventoryClickEvent) {
                InventoryClickEvent event = (InventoryClickEvent) data.getEvent();

                this.run(event);
            }

            return;
        }

        Consumer<ItemClickData> newConsumer = (Consumer<ItemClickData>) this.consumer;
        newConsumer.accept(data);
    }

    public ItemStack getItem() { return this.item; }

}

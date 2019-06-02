package fr.minuskube.inv;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@SuppressWarnings({ "unchecked", "deprecation" })
public class ClickableItem {

    /**
     * ClickableItem constant with no item and empty consumer.
     */
    public static final ClickableItem NONE = empty(null);

    private final ItemStack item;
    private final Consumer<?> consumer;
    private final boolean legacy;

    private ClickableItem(ItemStack item, Consumer<?> consumer, boolean legacy) {
        this.item = item;
        this.consumer = consumer;
        this.legacy = legacy;
    }

    /**
     * Creates a ClickableItem made of a given item and an empty consumer, thus
     * doing nothing when we click on the item.
     *
     * @param item the item
     * @return the created ClickableItem
     */
    public static ClickableItem empty(ItemStack item) {
        return from(item, data -> {});
    }

    /**
     * Creates a ClickableItem made of a given item and a given InventoryClickEvent's consumer.
     *
     * @deprecated Replaced by {@link ClickableItem#from(ItemStack, Consumer)}
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

    /**
     * Executes this ClickableItem's consumer using the given click event.
     *
     * @deprecated This has been replaced by {@link ClickableItem#run(ItemClickData)}.
     * @param e the click event
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public void run(InventoryClickEvent e) {
        if(!this.legacy)
            return;

        Consumer<InventoryClickEvent> legacyConsumer = (Consumer<InventoryClickEvent>) this.consumer;
        legacyConsumer.accept(e);
    }

    /**
     * Clones this ClickableItem using a different item.
     *
     * @param newItem the new item
     * @return the created ClickableItem
     */
    public ClickableItem clone(ItemStack newItem) {
        return new ClickableItem(newItem, this.consumer, this.legacy);
    }

    /**
     * Executes this ClickableItem's consumer using the given click data.
     *
     * @param data the data of the click
     */
    public void run(ItemClickData data) {
        if(this.legacy) {
            if(data.getEvent() instanceof InventoryClickEvent) {
                InventoryClickEvent event = (InventoryClickEvent) data.getEvent();

                this.run(event);
            }
        } else {
            Consumer<ItemClickData> newConsumer = (Consumer<ItemClickData>) this.consumer;
            newConsumer.accept(data);
        }
    }

    /**
     * Returns the item contained in this ClickableItem.
     * <br />
     * <b>Warning:</b> The item can be <code>null</code>.
     *
     * @return the item, or <code>null</code> if there is no item
     */
    public ItemStack getItem() { return this.item; }

}

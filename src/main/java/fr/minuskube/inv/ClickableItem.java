package fr.minuskube.inv;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings({ "unchecked", "deprecation" })
public class ClickableItem {

    /**
     * ClickableItem constant with no item and empty consumer.
     */
    public static final ClickableItem NONE = empty(null);

    private static final Predicate<Player> ALWAYS_TRUE = p -> true;

    private final ItemStack item;
    private final Consumer<?> consumer;
    private final boolean legacy;
    private Predicate<Player> canSee = ALWAYS_TRUE, canClick = ALWAYS_TRUE;
    private ItemStack notVisibleFallBackItem;

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
        if (canSee.test((Player) e.getWhoClicked()) && canClick.test((Player) e.getWhoClicked())) {
            if(!this.legacy)
                return;

            Consumer<InventoryClickEvent> legacyConsumer = (Consumer<InventoryClickEvent>) this.consumer;
            legacyConsumer.accept(e);
        }
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
        if (canSee.test(data.getPlayer()) && canClick.test(data.getPlayer())) {
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
    }

    /**
     * Returns the item contained in this ClickableItem disregarding the visibility test set via {@link #canSee(Predicate, ItemStack)}.
     * <br>
     * <b>Warning:</b> The item can be <code>null</code>.
     *
     * @return the item, or <code>null</code> if there is no item
     */
    public ItemStack getItem() {
        return this.item;
    }

    /**
     * Returns the item contained in this ClickableItem or the fallback item, if the player is not allowed to see the item.
     * <br>
     * <b>Warning:</b> The item can be <code>null</code>.
     *
     * @param player The player to test against if he can see this item
     * @return the item, the fallback item when not visible to the player, or <code>null</code> if there is no item
     */
    public ItemStack getItem(Player player) {
        if (canSee.test(player)) {
            return this.item;
        } else {
            return this.notVisibleFallBackItem;
        }
    }

    /**
     * Sets a test to check if a player is allowed to see this item.
     * <br>
     * Note: If the player is not allowed to see the item, in the inventory this item will be empty.
     * <br>
     * Examples:
     * <ul>
     *     <li><code>.canSee(player -> player.hasPermission("my.permission"))</code></li>
     *     <li><code>.canSee(player -> player.getHealth() >= 10)</code></li>
     * </ul>
     *
     * @param canSee the test, if a player should be allowed to see this item
     *
     * @return <code>this</code> for a builder-like usage
     *
     * @see #canSee(Predicate, ItemStack) If you want to set a specific fallback item
     */
    public ClickableItem canSee(Predicate<Player> canSee) {
        return canSee(canSee, null);
    }

    /**
     * Sets a test to check if a player is allowed to see this item.
     * <br>
     * If the player is <b>not</b> allowed to see the item, the fallback item will be used instead.
     * <br>
     * Note: If the player is not allowed to see the item, the on click handler will not be run
     * <br>
     * Examples:
     * <ul>
     *     <li><code>.canSee(player -> player.hasPermission("my.permission"), backgroundItem)</code></li>
     *     <li><code>.canSee(player -> player.getHealth() >= 10, backgroundItem)</code></li>
     * </ul>
     *
     * @param canSee       the test, if a player should be allowed to see this item
     * @param fallBackItem the item that should be used, if the player is <b>not</b> allowed to see the item
     *
     * @return <code>this</code> for a builder-like usage
     *
     * @see #canSee(Predicate) If you want the slot to be empty
     */
    public ClickableItem canSee(Predicate<Player> canSee, ItemStack fallBackItem) {
        this.canSee = canSee;
        this.notVisibleFallBackItem = fallBackItem;
        return this;
    }

    /**
     * Sets a test to check if a player is allowed to click the item.
     * <br>
     * If a player is not allowed to click this item, the on click handler provided at creation will not be run
     *
     * @param canClick the test, if a player should be allowed to see this item
     *
     * @return <code>this</code> for a builder-like usage
     */
    public ClickableItem canClick(Predicate<Player> canClick) {
        this.canClick = canClick;
        return this;
    }
}

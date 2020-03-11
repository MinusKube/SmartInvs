/*
 * Copyright 2018-2020 Isaac Montagne
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.minuskube.inv;

import java.util.function.Consumer;
import java.util.function.Predicate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public final class ClickableItem {

    /**
     * ClickableItem constant with no item and empty consumer.
     */
    public static final ClickableItem NONE = ClickableItem.empty(new ItemStack(Material.AIR));

    @NotNull
    private final ItemStack item;

    @NotNull
    private final Consumer<?> consumer;

    private final boolean legacy;

    @Nullable
    private Predicate<Player> canSee;

    @Nullable
    private Predicate<Player> canClick;

    @NotNull
    private ItemStack notVisibleFallBackItem = new ItemStack(Material.AIR);

    private ClickableItem(@NotNull final ItemStack item, @NotNull final Consumer<?> consumer, final boolean legacy) {
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
    @NotNull
    public static ClickableItem empty(@NotNull final ItemStack item) {
        return ClickableItem.from(item, data -> {
        });
    }

    /**
     * Creates a ClickableItem made of a given item and a given InventoryClickEvent's consumer.
     *
     * @param item the item
     * @param consumer the consumer which will be called when the item is clicked
     * @return the created ClickableItem
     * @deprecated Replaced by {@link ClickableItem#from(ItemStack, Consumer)}
     */
    @NotNull
    @Deprecated
    public static ClickableItem of(@NotNull final ItemStack item,
                                   @NotNull final Consumer<InventoryClickEvent> consumer) {
        return new ClickableItem(item, consumer, true);
    }

    /**
     * Creates a ClickableItem made of a given item and a given ItemClickData's consumer.
     *
     * @param item the item
     * @param consumer the consumer which will be called when the item is clicked
     * @return the created ClickableItem
     */
    @NotNull
    public static ClickableItem from(@NotNull final ItemStack item, @NotNull final Consumer<ItemClickData> consumer) {
        return new ClickableItem(item, consumer, false);
    }

    /**
     * Executes this ClickableItem's consumer using the given click event.
     *
     * @param e the click event
     * @deprecated This has been replaced by {@link ClickableItem#run(ItemClickData)}.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public void run(@NotNull final InventoryClickEvent e) {
        if ((this.canSee == null || this.canSee.test((Player) e.getWhoClicked())) &&
            (this.canClick == null || this.canClick.test((Player) e.getWhoClicked()))) {
            if (!this.legacy) {
                return;
            }
            final Consumer<InventoryClickEvent> legacyConsumer = (Consumer<InventoryClickEvent>) this.consumer;
            legacyConsumer.accept(e);
        }
    }

    /**
     * Clones this ClickableItem using a different item.
     *
     * @param newItem the new item
     * @return the created ClickableItem
     */
    @NotNull
    public ClickableItem clone(@NotNull final ItemStack newItem) {
        return new ClickableItem(newItem, this.consumer, this.legacy);
    }

    /**
     * Executes this ClickableItem's consumer using the given click data.
     *
     * @param data the data of the click
     */
    public void run(@NotNull final ItemClickData data) {
        if ((this.canSee == null || this.canSee.test(data.getPlayer())) &&
            (this.canClick == null || this.canClick.test(data.getPlayer()))) {
            if (this.legacy) {
                if (data.getEvent() instanceof InventoryClickEvent) {
                    final InventoryClickEvent event = (InventoryClickEvent) data.getEvent();

                    this.run(event);
                }
            } else {
                final Consumer<ItemClickData> newConsumer = (Consumer<ItemClickData>) this.consumer;
                newConsumer.accept(data);
            }
        }
    }

    /**
     * Returns the item contained in this ClickableItem disregarding the visibility test set
     * via {@link #canSee(Predicate, ItemStack)}.
     * <br>
     * <b>Warning:</b> The item can be {@code null}.
     *
     * @return the item, or {@code null} if there is no item
     */
    @NotNull
    public ItemStack getItem() {
        return this.item;
    }

    /**
     * Returns the item contained in this ClickableItem or the fallback item, if the player is not allowed to see
     * the item.
     * <br>
     * <b>Warning:</b> The item can be {@code null}.
     *
     * @param player The player to test against if he can see this item
     * @return the item, the fallback item when not visible to the player, or {@code null} if there is no item
     */
    @NotNull
    public ItemStack getItem(@NotNull final Player player) {
        if (this.canSee == null || this.canSee.test(player)) {
            return this.item;
        }
        return this.notVisibleFallBackItem;
    }

    /**
     * Sets a test to check if a player is allowed to see this item.
     * <br>
     * Note: If the player is not allowed to see the item, in the inventory this item will be empty.
     * <br>
     * Examples:
     * <ul>
     *     <li>{@code .canSee(player -> player.hasPermission("my.permission"))}</li>
     *     <li>{@code .canSee(player -> player.getHealth() >= 10)}</li>
     * </ul>
     *
     * @param canSee the test, if a player should be allowed to see this item
     * @return {@code this} for a builder-like usage
     * @see #canSee(Predicate, ItemStack) If you want to set a specific fallback item
     */
    @NotNull
    public ClickableItem canSee(@NotNull final Predicate<Player> canSee) {
        return this.canSee(canSee, new ItemStack(Material.AIR));
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
     *     <li>{@code .canSee(player -> player.hasPermission("my.permission"), backgroundItem)}</li>
     *     <li>{@code .canSee(player -> player.getHealth() >= 10, backgroundItem)}</li>
     * </ul>
     *
     * @param canSee the test, if a player should be allowed to see this item
     * @param fallBackItem the item that should be used, if the player is <b>not</b> allowed to see the item
     * @return {@code this} for a builder-like usage
     * @see #canSee(Predicate) If you want the slot to be empty
     */
    @NotNull
    public ClickableItem canSee(@NotNull final Predicate<Player> canSee, @NotNull final ItemStack fallBackItem) {
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
     * @return {@code this} for a builder-like usage
     */
    @NotNull
    public ClickableItem canClick(@NotNull final Predicate<Player> canClick) {
        this.canClick = canClick;
        return this;
    }
}

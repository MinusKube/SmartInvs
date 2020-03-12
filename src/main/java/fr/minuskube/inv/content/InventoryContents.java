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

package fr.minuskube.inv.content;

import com.google.common.base.Preconditions;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.util.Pattern;
import java.util.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * Represents the content of an inventory.
 * </p>
 *
 * <p>
 * This contains several methods which let you get and modify
 * the content of the inventory.
 * </p>
 *
 * <p>
 * For example, you can get the item at a given slot by
 * using {@link InventoryContents#get(SlotPos)}. You can
 * also fill an entire column with the use of the method
 * {@link InventoryContents#fillColumn(int, ClickableItem)}.
 * </p>
 */
public interface InventoryContents {

    /**
     * Gets the inventory linked to this.
     * <br>
     * Cannot be {@code null}.
     *
     * @return the inventory
     */
    @NotNull
    SmartInventory inventory();

    /**
     * Gets the pagination system linked to this.
     * <br>
     * Cannot be {@code null}.
     *
     * @return the pagination
     */
    @NotNull
    Pagination pagination();

    /**
     * Gets a previously registered iterator named with the given id.
     * <br>
     * If no iterator is found, this will return {@link Optional#empty()}.
     *
     * @param id the id of the iterator
     * @return the found iterator, if there is one
     */
    @NotNull
    Optional<SlotIterator> iterator(@NotNull String id);

    /**
     * Creates and registers an iterator using a given id.
     *
     * <p>
     * You can retrieve the iterator at any time using
     * the {@link InventoryContents#iterator(String)} method.
     * </p>
     *
     * @param id the id of the iterator
     * @param type the type of the iterator
     * @param startRow the starting row of the iterator
     * @param startColumn the starting column of the iterator
     * @return the newly created iterator
     */
    @NotNull
    SlotIterator newIterator(@NotNull String id, @NotNull SlotIterator.Type type, int startRow, int startColumn);

    /**
     * Creates and returns an iterator.
     *
     * <p>
     * This does <b>NOT</b> registers the iterator,
     * thus {@link InventoryContents#iterator(String)} will not be
     * able to return the iterators created with this method.
     * </p>
     *
     * @param type the type of the iterator
     * @param startRow the starting row of the iterator
     * @param startColumn the starting column of the iterator
     * @return the newly created iterator
     */
    @NotNull
    SlotIterator newIterator(@NotNull SlotIterator.Type type, int startRow, int startColumn);

    /**
     * Same as {@link InventoryContents#newIterator(String, SlotIterator.Type, int, int)},
     * but using a {@link SlotPos} instead.
     *
     * @see InventoryContents#newIterator(String, SlotIterator.Type, int, int)
     */
    @NotNull
    SlotIterator newIterator(@NotNull String id, @NotNull SlotIterator.Type type, @NotNull SlotPos startPos);

    /**
     * Same as {@link InventoryContents#newIterator(SlotIterator.Type, int, int)},
     * but using a {@link SlotPos} instead.
     *
     * @see InventoryContents#newIterator(SlotIterator.Type, int, int)
     */
    @NotNull
    SlotIterator newIterator(@NotNull SlotIterator.Type type, @NotNull SlotPos startPos);

    /**
     * Returns a 2D array of ClickableItems containing
     * all the items of the inventory.
     * The ClickableItems can be null when there is no
     * item in the corresponding slot.
     *
     * @return the items of the inventory
     */
    @NotNull
    ClickableItem[][] all();

    /**
     * Returns a list of all the slots in the inventory.
     *
     * @return the inventory slots
     */
    @NotNull
    List<SlotPos> slots();

    /**
     * Returns the position of the first empty slot
     * in the inventory, or {@link Optional#empty()} if
     * there is no free slot.
     *
     * @return the first empty slot, if there is one
     */
    @NotNull
    Optional<SlotPos> firstEmpty();

    /**
     * Returns the item in the inventory at the given
     * slot index, or {@link Optional#empty()} if
     * the slot is empty or if the index is out of bounds.
     *
     * @param index the slot index
     * @return the found item, if there is one
     */
    @NotNull
    Optional<ClickableItem> get(int index);

    /**
     * Same as {@link InventoryContents#get(int)},
     * but with a row and a column instead of the index.
     *
     * @see InventoryContents#get(int)
     */
    @NotNull
    Optional<ClickableItem> get(int row, int column);

    /**
     * Same as {@link InventoryContents#get(int)},
     * but with a {@link SlotPos} instead of the index.
     *
     * @see InventoryContents#get(int)
     */
    @NotNull
    Optional<ClickableItem> get(@NotNull SlotPos slotPos);

    /**
     * Sets the item in the inventory at the given
     * slot index.
     *
     * @param index the slot index
     * @param item the item to set, or {@code null} to clear the slot
     * @return {@code this}, for chained calls
     */
    @NotNull
    InventoryContents set(int index, @NotNull ClickableItem item);

    /**
     * Same as {@link InventoryContents#set(int, ClickableItem)},
     * but with a row and a column instead of the index.
     *
     * @see InventoryContents#set(int, ClickableItem)
     */
    @NotNull
    InventoryContents set(int row, int column, @NotNull ClickableItem item);

    /**
     * Same as {@link InventoryContents#set(int, ClickableItem)},
     * but with a {@link SlotPos} instead of the index.
     *
     * @see InventoryContents#set(int, ClickableItem)
     */
    @NotNull
    InventoryContents set(@NotNull SlotPos slotPos, @NotNull ClickableItem item);

    /**
     * Adds an item to the <b>first empty slot</b> of the inventory.
     * <br>
     * <b>Warning:</b> If there is already a stack of the same item,
     * this will not add the item to the stack, this will always
     * add the item into an empty slot.
     *
     * @param item the item to add
     * @return {@code this}, for chained calls
     */
    @NotNull
    InventoryContents add(@NotNull ClickableItem item);

    /**
     * Looks for the given item and compares them using {@link ItemStack#isSimilar(ItemStack)},
     * ignoring the amount.
     * <br>
     * This method searches row for row from left to right.
     *
     * @param item the item to look for
     * @return an optional containing the position where the item first occurred, or an empty optional
     */
    @NotNull
    Optional<SlotPos> findItem(@NotNull ItemStack item);

    /**
     * Looks for the given item and compares them using {@link ItemStack#isSimilar(ItemStack)},
     * ignoring the amount.
     * <br>
     * This method searches row for row from left to right.
     *
     * @param item the clickable item with the item stack to look for
     * @return an optional containing the position where the item first occurred, or an empty optional
     */
    @NotNull
    Optional<SlotPos> findItem(@NotNull ClickableItem item);

    /**
     * Fills the inventory with the given item.
     *
     * @param item the item
     * @return {@code this}, for chained calls
     */
    @NotNull
    InventoryContents fill(@NotNull ClickableItem item);

    /**
     * Fills the given inventory row with the given item.
     *
     * @param row the row to fill
     * @param item the item
     * @return {@code this}, for chained calls
     */
    @NotNull
    InventoryContents fillRow(int row, @NotNull ClickableItem item);

    /**
     * Fills the given inventory column with the given item.
     *
     * @param column the column to fill
     * @param item the item
     * @return {@code this}, for chained calls
     */
    @NotNull
    InventoryContents fillColumn(int column, @NotNull ClickableItem item);

    /**
     * Fills the inventory borders with the given item.
     *
     * @param item the item
     * @return {@code this}, for chained calls
     */
    @NotNull
    InventoryContents fillBorders(@NotNull ClickableItem item);

    /**
     * Fills a rectangle inside the inventory using the given
     * positions.
     * <br>
     * The created rectangle will have its top-left position at
     * the given <b>from slot index</b> and its bottom-right position at
     * the given <b>to slot index</b>.
     *
     * @param fromIndex the slot index at the top-left position
     * @param toIndex the slot index at the bottom-right position
     * @param item the item
     * @return {@code this}, for chained calls
     */
    @NotNull
    InventoryContents fillRect(int fromIndex, int toIndex, @NotNull ClickableItem item);

    /**
     * Same as {@link InventoryContents#fillRect(int, int, ClickableItem)},
     * but with {@link SlotPos} instead of the indexes.
     *
     * @see InventoryContents#fillRect(int, int, ClickableItem)
     */
    @NotNull
    InventoryContents fillRect(int fromRow, int fromColumn, int toRow, int toColumn, @NotNull ClickableItem item);

    /**
     * Same as {@link InventoryContents#fillRect(int, int, ClickableItem)},
     * but with rows and columns instead of the indexes.
     *
     * @see InventoryContents#fillRect(int, int, ClickableItem)
     */
    @NotNull
    InventoryContents fillRect(@NotNull SlotPos fromPos, @NotNull SlotPos toPos, @NotNull ClickableItem item);

    /**
     * Completely fills the provided square with the given {@link ClickableItem}.
     *
     * @param fromIndex the slot index of the upper left corner
     * @param toIndex the slot index of the lower right corner
     * @param item the item
     * @return {@code this}, for chained calls
     */
    @NotNull
    InventoryContents fillSquare(int fromIndex, int toIndex, @NotNull ClickableItem item);

    /**
     * Completely fills the provided square with the given {@link ClickableItem}.
     *
     * @param fromRow the row of the upper left corner
     * @param fromColumn the column of the upper-left corner
     * @param toRow the row of the lower right corner
     * @param toColumn the column of the lower right corner
     * @param item the item
     * @return {@code this}, for chained calls
     */
    @NotNull
    InventoryContents fillSquare(int fromRow, int fromColumn, int toRow, int toColumn, @NotNull ClickableItem item);

    /**
     * Completely fills the provided square with the given {@link ClickableItem}.
     *
     * @param fromPos the slot position of the upper left corner
     * @param toPos the slot position of the lower right corner
     * @param item the item
     * @return {@code this}, for chained calls
     */
    @NotNull
    InventoryContents fillSquare(@NotNull SlotPos fromPos, @NotNull SlotPos toPos, @NotNull ClickableItem item);

    /**
     * Fills the inventory with the given {@link Pattern}.
     * <br>
     * The pattern will start at the first slot.
     *
     * @param pattern the filling pattern
     * @return {@code this}, for chained calls
     * @see #fillPattern(Pattern, int) to fill the pattern from the provided slot index
     * @see #fillPattern(Pattern, int, int) to fill the pattern from the provided row and column
     * @see #fillPattern(Pattern, SlotPos) to fill the pattern from the provided slot pos
     */
    @NotNull
    InventoryContents fillPattern(@NotNull Pattern<ClickableItem> pattern);

    /**
     * Fills the inventory with the given {@link Pattern}.
     * <br>
     * The pattern will start at the given slot index.
     *
     * @param pattern the filling pattern
     * @param startIndex the start slot index for the filling
     * @return {@code this}, for chained calls
     * @see #fillPattern(Pattern) to fill the pattern from the first slot
     * @see #fillPattern(Pattern, int, int) to fill the pattern from the provided row and column
     * @see #fillPattern(Pattern, SlotPos) to fill the pattern from the provided slot pos
     */
    @NotNull
    InventoryContents fillPattern(@NotNull Pattern<ClickableItem> pattern, int startIndex);

    /**
     * Fills the inventory with the given {@link Pattern}.
     * <br>
     * The pattern will start at the given slot position based on the provided row and column.
     *
     * @param pattern the filling pattern
     * @param startRow the start row of the slot for filling
     * @param startColumn the start column of the slot for filling
     * @return {@code this}, for chained calls
     * @see #fillPattern(Pattern) to fill the pattern from the first slot
     * @see #fillPattern(Pattern, int) to fill the pattern from the provided slot index
     * @see #fillPattern(Pattern, SlotPos) to fill the pattern from the provided slot pos
     */
    @NotNull
    InventoryContents fillPattern(@NotNull Pattern<ClickableItem> pattern, int startRow, int startColumn);

    /**
     * Fills the inventory with the given {@link Pattern}.
     * <br>
     * The pattern will start at the given slot position.
     *
     * @param pattern the filling pattern
     * @param startPos the start position of the slot for filling
     * @return {@code this}, for chained calls
     * @see #fillPattern(Pattern) to fill the pattern from the first slot
     * @see #fillPattern(Pattern, int) to fill the pattern from the provided slot index
     * @see #fillPattern(Pattern, int, int) to fill the pattern from the provided row and column
     */
    @NotNull
    InventoryContents fillPattern(@NotNull Pattern<ClickableItem> pattern, @NotNull SlotPos startPos);

    /**
     * Fills the inventory with the given {@link Pattern}.
     * <br>
     * The pattern will start at the first slot and end at the last slot.
     * If the pattern is not big enough, it will wrap around to the other side and repeat the pattern.
     * <br>
     * The top-left corner of the specified inventory area is also the top-left corner of the specified pattern.
     * <br>
     * <b>For this to work the pattern needs to be created with {@code wrapAround} enabled.</b>
     *
     * @param pattern the filling pattern
     * @return {@code this}, for chained calls
     * @see #fillPatternRepeating(Pattern, int, int) to fill a repeating pattern using slot indexes
     * @see #fillPatternRepeating(Pattern, int, int, int, int) to fill a repeating pattern
     * using slot positions contructed from their rows and columns
     * @see #fillPatternRepeating(Pattern, SlotPos, SlotPos) to fill a repeating pattern using slot positions
     */
    @NotNull
    InventoryContents fillPatternRepeating(@NotNull Pattern<ClickableItem> pattern);

    /**
     * Fills the inventory with the given {@link Pattern}.
     * <br>
     * The pattern will start at the first slot index and end at the second slot index.
     * If the pattern is not big enough, it will wrap around to the other side and repeat the pattern.
     * <br>
     * The top-left corner of the specified inventory area is also the top-left corner of the specified pattern.
     * <br>
     * If {@code endIndex} is a negative value it is set to the bottom-right corner.
     * <br>
     * <b>For this to work the pattern needs to be created with {@code wrapAround} enabled.</b>
     *
     * @param pattern the filling pattern
     * @param startIndex the start slot index where the pattern should begin
     * @param endIndex the end slot index where the pattern should end
     * @return {@code this}, for chained calls
     * @see #fillPatternRepeating(Pattern) to fill a repeating pattern into the whole inventory
     * @see #fillPatternRepeating(Pattern, int, int, int, int) to fill a repeating pattern
     * using slot positions contructed from their rows and columns
     * @see #fillPatternRepeating(Pattern, SlotPos, SlotPos) to fill a repeating pattern using slot positions
     */
    @NotNull
    InventoryContents fillPatternRepeating(@NotNull Pattern<ClickableItem> pattern, int startIndex, int endIndex);

    /**
     * Fills the inventory with the given {@link Pattern}.
     * <br>
     * The pattern will start at the given slot position and end at the second slot position.
     * If the pattern is not big enough, it will wrap around to the other side and repeat the pattern.
     * <br>
     * The top-left corner of the specified inventory area is also the top-left corner of the specified pattern.
     * <br>
     * If {@code endRow} is a negative value, endRow is automatically set to the max row size,
     * if {@code endColumn} is a negative value, endColumn is automatically set to the max column size.
     * <br>
     * <b>For this to work the pattern needs to be created with {@code wrapAround} enabled.</b>
     *
     * @param pattern the filling pattern
     * @param startRow the start row of the slot for filling
     * @param startColumn the start column of the slot for filling
     * @param endRow the end row of the slot for filling
     * @param endColumn the end column of the slot for filling
     * @return {@code this}, for chained calls
     * @see #fillPatternRepeating(Pattern) to fill a repeating pattern into the whole inventory
     * @see #fillPatternRepeating(Pattern, int, int) to fill a repeating pattern using slot indexes
     * @see #fillPatternRepeating(Pattern, SlotPos, SlotPos) to fill a repeating pattern using slot positions
     */
    @NotNull
    InventoryContents fillPatternRepeating(@NotNull Pattern<ClickableItem> pattern, int startRow, int startColumn,
                                           int endRow, int endColumn);

    /**
     * Fills the inventory with the given {@link Pattern}.
     * <br>
     * The pattern will start at the given slot position and end at the second slot position.
     * If the pattern is not big enough, it will wrap around to the other side and repeat the pattern.
     * <br>
     * The top-left corner of the specified inventory area is also the top-left corner of the specified pattern.
     * <br>
     * If the row of {@code endPos} is a negative value, endRow is automatically set to the max row size,
     * if the column of {@code endPos} is a negative value, endColumn is automatically set to the max column size.
     * <br>
     * <b>For this to work the pattern needs to be created with {@code wrapAround} enabled.</b>
     *
     * @param pattern the filling pattern
     * @param startPos the position where the pattern should start
     * @param endPos the position where the pattern should end
     * @return {@code this}, for chained calls
     * @see #fillPatternRepeating(Pattern) to fill a repeating pattern into the whole inventory
     * @see #fillPatternRepeating(Pattern, int, int) to fill a repeating pattern using slot indexes
     * @see #fillPatternRepeating(Pattern, int, int, int, int) to fill a repeating pattern
     * using slot positions contructed from their rows and columns
     */
    @NotNull
    InventoryContents fillPatternRepeating(@NotNull Pattern<ClickableItem> pattern, @NotNull SlotPos startPos,
                                           @NotNull SlotPos endPos);

    /**
     * Gets the value of the property with the given name.
     *
     * @param name the property's name
     * @param <T> the type of the value
     * @return the property's value
     */
    @Nullable <T> T property(@NotNull String name);

    /**
     * Gets the value of the property with the given name,
     * or a default value if the property isn't set.
     *
     * @param name the property's name
     * @param def the default value
     * @param <T> the type of the value
     * @return the property's value, or the given default value
     */
    @NotNull <T> T property(@NotNull String name, T def);

    /**
     * Sets the value of the property with the given name.
     * <br>
     * This will replace the existing value for the property,
     * if there is one.
     *
     * @param name the property's name
     * @param value the new property's value
     * @return {@code this}, for chained calls
     */
    @NotNull
    InventoryContents setProperty(@NotNull String name, @NotNull Object value);

    /**
     * Makes a slot editable, which enables the player to
     * put items in and take items out of the inventory in the
     * specified slot.
     *
     * @param slot The slot to set editable
     * @param editable {@code true} to make a slot editable, {@code false}
     * to make it 'static' again.
     */
    @NotNull
    void setEditable(@NotNull SlotPos slot, boolean editable);

    /**
     * Returns if a given slot is editable or not.
     *
     * @param slot The slot to check
     * @return {@code true} if the editable.
     * @see #setEditable(SlotPos, boolean)
     */
    @NotNull
    boolean isEditable(@NotNull SlotPos slot);

    final class Impl implements InventoryContents {

        @NotNull
        private final SmartInventory inventory;

        @NotNull
        private final Player player;

        @Nullable
        private final ClickableItem[][] contents;

        private final Pagination pagination = new Pagination.Impl();

        private final Map<String, SlotIterator> iterators = new HashMap<>();

        private final Map<String, Object> properties = new HashMap<>();

        private final Set<SlotPos> editableSlots = new HashSet<>();

        public Impl(@NotNull final SmartInventory inventory, @NotNull final Player plyr) {
            this.inventory = inventory;
            this.player = plyr;
            this.contents = new ClickableItem[inventory.getRows()][inventory.getColumns()];
        }

        @NotNull
        @Override
        public SmartInventory inventory() {
            return this.inventory;
        }

        @NotNull
        @Override
        public Pagination pagination() {
            return this.pagination;
        }

        @NotNull
        @Override
        public Optional<SlotIterator> iterator(@NotNull final String id) {
            return Optional.ofNullable(this.iterators.get(id));
        }

        @NotNull
        @Override
        public SlotIterator newIterator(@NotNull final String id, @NotNull final SlotIterator.Type type,
                                        final int startRow, final int startColumn) {
            final SlotIterator iterator = new SlotIterator.Impl(this, this.inventory, type, startRow, startColumn);
            this.iterators.put(id, iterator);
            return iterator;
        }

        @NotNull
        @Override
        public SlotIterator newIterator(@NotNull final SlotIterator.Type type, final int startRow,
                                        final int startColumn) {
            return new SlotIterator.Impl(this, this.inventory, type, startRow, startColumn);
        }

        @NotNull
        @Override
        public SlotIterator newIterator(@NotNull final String id, @NotNull final SlotIterator.Type type,
                                        @NotNull final SlotPos startPos) {
            return this.newIterator(id, type, startPos.getRow(), startPos.getColumn());
        }

        @NotNull
        @Override
        public SlotIterator newIterator(@NotNull final SlotIterator.Type type, @NotNull final SlotPos startPos) {
            return this.newIterator(type, startPos.getRow(), startPos.getColumn());
        }

        @NotNull
        @Override
        public ClickableItem[][] all() {
            return this.contents;
        }

        @NotNull
        @Override
        public List<SlotPos> slots() {
            final List<SlotPos> position = new ArrayList<>();
            for (int row = 0; row < this.contents.length; row++) {
                for (int column = 0; column < this.contents[0].length; column++) {
                    position.add(SlotPos.of(row, column));
                }
            }
            return position;
        }

        @NotNull
        @Override
        public Optional<SlotPos> firstEmpty() {
            for (int row = 0; row < this.contents.length; row++) {
                for (int column = 0; column < this.contents[0].length; column++) {
                    if (!this.get(row, column).isPresent()) {
                        return Optional.of(new SlotPos(row, column));
                    }
                }
            }
            return Optional.empty();
        }

        @NotNull
        @Override
        public Optional<ClickableItem> get(final int index) {
            final int count = this.inventory.getColumns();
            return this.get(index / count, index % count);
        }

        @NotNull
        @Override
        public Optional<ClickableItem> get(final int row, final int column) {
            if (row < 0 || row >= this.contents.length) {
                return Optional.empty();
            }
            if (column < 0 || column >= this.contents[row].length) {
                return Optional.empty();
            }
            return Optional.ofNullable(this.contents[row][column]);
        }

        @NotNull
        @Override
        public Optional<ClickableItem> get(@NotNull final SlotPos pos) {
            return this.get(pos.getRow(), pos.getColumn());
        }

        @NotNull
        @Override
        public InventoryContents set(final int index, @NotNull final ClickableItem item) {
            final int columnCount = this.inventory.getColumns();
            return this.set(index / columnCount, index % columnCount, item);
        }

        @NotNull
        @Override
        public InventoryContents set(final int row, final int column, @NotNull final ClickableItem item) {
            if (row < 0 || row >= this.contents.length) {
                return this;
            }
            if (column < 0 || column >= this.contents[row].length) {
                return this;
            }
            this.contents[row][column] = item;
            if (item == null) {
                this.update(row, column, null);
            } else {
                this.update(row, column, item.getItem(this.player));
            }
            return this;
        }

        @NotNull
        @Override
        public InventoryContents set(@NotNull final SlotPos slotPos, @NotNull final ClickableItem item) {
            return this.set(slotPos.getRow(), slotPos.getColumn(), item);
        }

        @NotNull
        @Override
        public InventoryContents add(@NotNull final ClickableItem item) {
            for (int row = 0; row < this.contents.length; row++) {
                for (int column = 0; column < this.contents[0].length; column++) {
                    if (this.contents[row][column] == null) {
                        this.set(row, column, item);
                        return this;
                    }
                }
            }
            return this;
        }

        @NotNull
        @Override
        public Optional<SlotPos> findItem(@NotNull final ItemStack itemStack) {
            for (int row = 0; row < this.contents.length; row++) {
                for (int column = 0; column < this.contents[0].length; column++) {
                    final ClickableItem item = this.contents[row][column];
                    if (item != null && itemStack.isSimilar(item.getItem(this.player))) {
                        return Optional.of(SlotPos.of(row, column));
                    }
                }
            }
            return Optional.empty();
        }

        @NotNull
        @Override
        public Optional<SlotPos> findItem(@NotNull final ClickableItem clickableItem) {
            return this.findItem(clickableItem.getItem(this.player));
        }

        @NotNull
        @Override
        public InventoryContents fill(@NotNull final ClickableItem item) {
            for (int row = 0; row < this.contents.length; row++) {
                for (int column = 0; column < this.contents[row].length; column++) {
                    this.set(row, column, item);
                }
            }
            return this;
        }

        @NotNull
        @Override
        public InventoryContents fillRow(final int row, @NotNull final ClickableItem item) {
            if (row < 0 || row >= this.contents.length) {
                return this;
            }
            for (int column = 0; column < this.contents[row].length; column++) {
                this.set(row, column, item);
            }
            return this;
        }

        @NotNull
        @Override
        public InventoryContents fillColumn(final int column, @NotNull final ClickableItem item) {
            if (column < 0 || column >= this.contents[0].length) {
                return this;
            }
            for (int row = 0; row < this.contents.length; row++) {
                this.set(row, column, item);
            }
            return this;
        }

        @NotNull
        @Override
        public InventoryContents fillBorders(@NotNull final ClickableItem item) {
            this.fillRect(0, 0, this.inventory.getRows() - 1, this.inventory.getColumns() - 1, item);
            return this;
        }

        @NotNull
        @Override
        public InventoryContents fillRect(final int fromIndex, final int toIndex, @NotNull final ClickableItem item) {
            final int count = this.inventory.getColumns();
            return this.fillRect(
                fromIndex / count, fromIndex % count,
                toIndex / count, toIndex % count,
                item
            );
        }

        @NotNull
        @Override
        public InventoryContents fillRect(final int fromRow, final int fromColumn, final int toRow, final int toColumn,
                                          @NotNull final ClickableItem item) {
            for (int row = fromRow; row <= toRow; row++) {
                for (int column = fromColumn; column <= toColumn; column++) {
                    if (row != fromRow &&
                        row != toRow &&
                        column != fromColumn &&
                        column != toColumn) {
                        continue;
                    }
                    this.set(row, column, item);
                }
            }
            return this;
        }

        @NotNull
        @Override
        public InventoryContents fillRect(@NotNull final SlotPos fromPos, @NotNull final SlotPos toPos,
                                          @NotNull final ClickableItem item) {
            return this.fillRect(fromPos.getRow(), fromPos.getColumn(), toPos.getRow(), toPos.getColumn(), item);
        }

        @NotNull
        @Override
        public InventoryContents fillSquare(final int fromIndex, final int toIndex, @NotNull final ClickableItem item) {
            final int count = this.inventory.getColumns();
            return this.fillSquare(
                fromIndex / count, fromIndex % count,
                toIndex / count, toIndex % count,
                item
            );
        }

        @NotNull
        @Override
        public InventoryContents fillSquare(final int fromRow, final int fromColumn, final int toRow,
                                            final int toColumn, @NotNull final ClickableItem item) {
            Preconditions.checkArgument(fromRow < toRow, "The start row needs to be lower than the end row");
            Preconditions.checkArgument(fromColumn < toColumn,
                "The start column needs to be lower than the end column");
            for (int row = fromRow; row <= toRow; row++) {
                for (int column = fromColumn; column <= toColumn; column++) {
                    this.set(row, column, item);
                }
            }
            return this;
        }

        @NotNull
        @Override
        public InventoryContents fillSquare(@NotNull final SlotPos fromPos, @NotNull final SlotPos toPos,
                                            @NotNull final ClickableItem item) {
            return this.fillSquare(fromPos.getRow(), fromPos.getColumn(), toPos.getRow(), toPos.getColumn(), item);
        }

        @NotNull
        @Override
        public InventoryContents fillPattern(@NotNull final Pattern<ClickableItem> pattern) {
            return this.fillPattern(pattern, 0, 0);
        }

        @NotNull
        @Override
        public InventoryContents fillPattern(@NotNull final Pattern<ClickableItem> pattern, final int startIndex) {
            final int count = this.inventory.getColumns();
            return this.fillPattern(pattern, startIndex / count, startIndex % count);
        }

        @NotNull
        @Override
        public InventoryContents fillPattern(@NotNull final Pattern<ClickableItem> pattern, final int startRow,
                                             final int startColumn) {
            for (int row = 0; row < pattern.getRowCount(); row++) {
                for (int column = 0; column < pattern.getColumnCount(); column++) {
                    final ClickableItem item = pattern.getObject(row, column);
                    if (item != null) {
                        this.set(startRow + row, startColumn + column, item);
                    }
                }
            }
            return this;
        }

        @NotNull
        @Override
        public InventoryContents fillPattern(@NotNull final Pattern<ClickableItem> pattern,
                                             @NotNull final SlotPos startPos) {
            return this.fillPattern(pattern, startPos.getRow(), startPos.getColumn());
        }

        @NotNull
        @Override
        public InventoryContents fillPatternRepeating(@NotNull final Pattern<ClickableItem> pattern) {
            return this.fillPatternRepeating(pattern, 0, 0, -1, -1);
        }

        @NotNull
        @Override
        public InventoryContents fillPatternRepeating(@NotNull final Pattern<ClickableItem> pattern,
                                                      final int startIndex, final int endIndex) {
            final int columnCount = this.inventory.getColumns();
            final boolean maxSize = endIndex < 0;

            if (maxSize) {
                return this.fillPatternRepeating(pattern, startIndex / columnCount, startIndex % columnCount, -1, -1);
            }
            return this.fillPatternRepeating(pattern, startIndex / columnCount, startIndex % columnCount,
                endIndex / columnCount, endIndex % columnCount);
        }

        @NotNull
        @Override
        public InventoryContents fillPatternRepeating(@NotNull final Pattern<ClickableItem> pattern, final int startRow,
                                                      final int startColumn, int endRow, int endColumn) {
            Preconditions.checkArgument(pattern.isWrapAround(),
                "To fill in a repeating pattern wrapAround needs to be enabled for the pattern to work!");
            if (endRow < 0) {
                endRow = this.inventory.getRows();
            }
            if (endColumn < 0) {
                endColumn = this.inventory.getColumns();
            }
            Preconditions.checkArgument(startRow < endRow, "The start row needs to be lower than the end row");
            Preconditions.checkArgument(startColumn < endColumn,
                "The start column needs to be lower than the end column");
            final int rowDelta = endRow - startRow;
            final int columnDelta = endColumn - startColumn;
            for (int row = 0; row <= rowDelta; row++) {
                for (int column = 0; column <= columnDelta; column++) {
                    final ClickableItem item = pattern.getObject(row, column);
                    if (item != null) {
                        this.set(startRow + row, startColumn + column, item);
                    }
                }
            }
            return this;
        }

        @NotNull
        @Override
        public InventoryContents fillPatternRepeating(@NotNull final Pattern<ClickableItem> pattern,
                                                      @NotNull final SlotPos startPos, @NotNull final SlotPos endPos) {
            return this.fillPatternRepeating(pattern, startPos.getRow(), startPos.getColumn(), endPos.getRow(),
                endPos.getColumn());
        }

        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> T property(@NotNull final String name) {
            return (T) this.properties.get(name);
        }

        @SuppressWarnings("unchecked")
        @NotNull
        @Override
        public <T> T property(@NotNull final String name, @NotNull final T def) {
            if (this.properties.containsKey(name)) {
                return (T) this.properties.get(name);
            }
            return def;
        }

        @NotNull
        @Override
        public InventoryContents setProperty(@NotNull final String name, @NotNull final Object value) {
            this.properties.put(name, value);
            return this;
        }

        @NotNull
        @Override
        public void setEditable(@NotNull final SlotPos slot, final boolean editable) {
            if (editable) {
                this.editableSlots.add(slot);
            } else {
                this.editableSlots.remove(slot);
            }
        }

        @Override
        public boolean isEditable(@NotNull final SlotPos slot) {
            return this.editableSlots.contains(slot);
        }

        private void update(final int row, final int column, @NotNull final ItemStack item) {
            if (!this.inventory.getManager().getOpenedPlayers(this.inventory).contains(this.player)) {
                return;
            }
            final Inventory inv = this.player.getOpenInventory().getTopInventory();
            inv.setItem(this.inventory.getColumns() * row + column, item);
        }

    }

}

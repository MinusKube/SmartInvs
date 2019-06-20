package fr.minuskube.inv.content;

import com.google.common.base.Preconditions;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.util.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
     * Gets the inventory linked to this {@link InventoryContents}.
     * <br>
     * Cannot be <code>null</code>.
     *
     * @return the inventory
     */
    SmartInventory inventory();

    /**
     * Gets the pagination system linked to this {@link InventoryContents}.
     * <br>
     * Cannot be <code>null</code>.
     *
     * @return the pagination
     */
    Pagination pagination();

    /**
     * Gets a previously registered iterator named with the given id.
     * <br>
     * If no iterator is found, this will return <code>Optional.empty()</code>.
     *
     * @param id the id of the iterator
     * @return the found iterator, if there is one
     */
    Optional<SlotIterator> iterator(String id);

    /**
     * Creates and registers an iterator using a given id.
     *
     * <p>
     * You can retrieve the iterator at any time using
     * the {@link InventoryContents#iterator(String)} method.
     * </p>
     *
     * @param id          the id of the iterator
     * @param type        the type of the iterator
     * @param startRow    the starting row of the iterator
     * @param startColumn the starting column of the iterator
     * @return the newly created iterator
     */
    SlotIterator newIterator(String id, SlotIterator.Type type, int startRow, int startColumn);

    /**
     * Creates and returns an iterator.
     *
     * <p>
     * This does <b>NOT</b> registers the iterator,
     * thus {@link InventoryContents#iterator(String)} will not be
     * able to return the iterators created with this method.
     * </p>
     *
     * @param type        the type of the iterator
     * @param startRow    the starting row of the iterator
     * @param startColumn the starting column of the iterator
     * @return the newly created iterator
     */
    SlotIterator newIterator(SlotIterator.Type type, int startRow, int startColumn);

    /**
     * Same as {@link InventoryContents#newIterator(String, SlotIterator.Type, int, int)},
     * but using a {@link SlotPos} instead.
     *
     * @see InventoryContents#newIterator(String, SlotIterator.Type, int, int)
     */
    SlotIterator newIterator(String id, SlotIterator.Type type, SlotPos startPos);

    /**
     * Same as {@link InventoryContents#newIterator(SlotIterator.Type, int, int)},
     * but using a {@link SlotPos} instead.
     *
     * @see InventoryContents#newIterator(SlotIterator.Type, int, int)
     */
    SlotIterator newIterator(SlotIterator.Type type, SlotPos startPos);

    /**
     * Returns a 2D array of ClickableItems containing
     * all the items of the inventory.
     * The ClickableItems can be null when there is no
     * item in the corresponding slot.
     *
     * @return the items of the inventory
     */
    ClickableItem[][] all();

    /**
     * Returns the position of the first empty slot
     * in the inventory, or <code>Optional.empty()</code> if
     * there is no free slot.
     *
     * @return the first empty slot, if there is one
     */
    Optional<SlotPos> firstEmpty();

    /**
     * Returns the item in the inventory at the given
     * slot index, or <code>Optional.empty()</code> if
     * the slot is empty or if the index is out of bounds.
     *
     * @param index the slot index
     * @return the found item, if there is one
     */
    Optional<ClickableItem> get(int index);

    /**
     * Same as {@link InventoryContents#get(int)},
     * but with a row and a column instead of the index.
     *
     * @see InventoryContents#get(int)
     */
    Optional<ClickableItem> get(int row, int column);

    /**
     * Same as {@link InventoryContents#get(int)},
     * but with a {@link SlotPos} instead of the index.
     *
     * @see InventoryContents#get(int)
     */
    Optional<ClickableItem> get(SlotPos slotPos);

    /**
     * Sets the item in the inventory at the given
     * slot index.
     *
     * @param index the slot index
     * @param item  the item to set, or <code>null</code> to clear the slot
     * @return <code>this</code>, for chained calls
     */
    InventoryContents set(int index, ClickableItem item);

    /**
     * Same as {@link InventoryContents#set(int, ClickableItem)},
     * but with a row and a column instead of the index.
     *
     * @see InventoryContents#set(int, ClickableItem)
     */
    InventoryContents set(int row, int column, ClickableItem item);

    /**
     * Same as {@link InventoryContents#set(int, ClickableItem)},
     * but with a {@link SlotPos} instead of the index.
     *
     * @see InventoryContents#set(int, ClickableItem)
     */
    InventoryContents set(SlotPos slotPos, ClickableItem item);

    /**
     * Adds an item to the <b>first empty slot</b> of the inventory.
     * <br>
     * <b>Warning:</b> If there is already a stack of the same item,
     * this will not add the item to the stack, this will always
     * add the item into an empty slot.
     *
     * @param item the item to add
     * @return <code>this</code>, for chained calls
     */
    InventoryContents add(ClickableItem item);

    /**
     * Looks for the given item stack and compares them with {@link ItemStack#isSimilar(ItemStack)}, ignoring the amount.
     * <br>
     * This method searches row for row from left to right
     *
     * @param itemStack The item stack to look for
     * @return An optional containing the position where the item stack first occurred, or an empty optional
     */
    Optional<SlotPos> findItemStack(ItemStack itemStack);

    /**
     * Looks for the given item stack and compares them with {@link ItemStack#isSimilar(ItemStack)}, ignoring the amount.
     * <br>
     * This method searches row for row from left to right
     *
     * @param clickableItem The clickable item with it's item stack to look for
     * @return An optional containing the position where the item stack first occurred, or an empty optional
     */
    Optional<SlotPos> findItemStack(ClickableItem clickableItem);

    /**
     * Fills the inventory with the given item.
     *
     * @param item the item
     * @return <code>this</code>, for chained calls
     */
    InventoryContents fill(ClickableItem item);

    /**
     * Fills the given inventory row with the given item.
     *
     * @param row  the row to fill
     * @param item the item
     * @return <code>this</code>, for chained calls
     */
    InventoryContents fillRow(int row, ClickableItem item);

    /**
     * Fills the given inventory column with the given item.
     *
     * @param column the column to fill
     * @param item   the item
     * @return <code>this</code>, for chained calls
     */
    InventoryContents fillColumn(int column, ClickableItem item);

    /**
     * Fills the inventory borders with the given item.
     *
     * @param item the item
     * @return <code>this</code>, for chained calls
     */
    InventoryContents fillBorders(ClickableItem item);

    /**
     * Fills a rectangle inside the inventory using the given
     * positions.
     * <br>
     * The created rectangle will have its top-left position at
     * the given <b>from slot index</b> and its bottom-right position at
     * the given <b>to slot index</b>.
     *
     * @param fromIndex the slot index at the top-left position
     * @param toIndex   the slot index at the bottom-right position
     * @param item      the item
     * @return <code>this</code>, for chained calls
     */
    InventoryContents fillRect(int fromIndex, int toIndex, ClickableItem item);

    /**
     * Same as {@link InventoryContents#fillRect(int, int, ClickableItem)},
     * but with {@link SlotPos} instead of the indexes.
     *
     * @see InventoryContents#fillRect(int, int, ClickableItem)
     */
    InventoryContents fillRect(int fromRow, int fromColumn,
                               int toRow, int toColumn, ClickableItem item);

    /**
     * Same as {@link InventoryContents#fillRect(int, int, ClickableItem)},
     * but with rows and columns instead of the indexes.
     *
     * @see InventoryContents#fillRect(int, int, ClickableItem)
     */
    InventoryContents fillRect(SlotPos fromPos, SlotPos toPos, ClickableItem item);

    /**
     * Completely fills the provided square with the {@link ClickableItem}
     *
     * @param fromIndex The slot index of the upper left corner
     * @param toIndex   The slot index of the lower right corner
     * @param item      the item
     * @return <code>this</code>, for chained calls
     */
    InventoryContents fillSquare(int fromIndex, int toIndex, ClickableItem item);

    /**
     * Completely fills the provided square with the {@link ClickableItem}
     *
     * @param fromRow    The row of the upper left corner
     * @param fromColumn The column of the upper-left corner
     * @param toRow      The row of the lower right corner
     * @param toColumn   The column of the lower right corner
     * @param item       the item
     * @return <code>this</code>, for chained calls
     */
    InventoryContents fillSquare(int fromRow, int fromColumn, int toRow, int toColumn, ClickableItem item);

    /**
     * Completely fills the provided square with the {@link ClickableItem}
     *
     * @param fromPos The slot position of the upper left corner
     * @param toPos   The slot position of the lower right corner
     * @param item    the item
     * @return <code>this</code>, for chained calls
     */
    InventoryContents fillSquare(SlotPos fromPos, SlotPos toPos, ClickableItem item);

    /**
     * Fills the inventory with the given {@link Pattern}.
     * <br>
     * The pattern will start at the first slot.
     *
     * @param pattern the filling pattern
     * @return <code>this</code>, for chained calls
     * @see #fillPattern(Pattern, int) To fill the pattern from the provided slot index
     * @see #fillPattern(Pattern, int, int) To fill the pattern from the provided row and column
     * @see #fillPattern(Pattern, SlotPos) To fill the pattern from the provided slot pos
     */
    InventoryContents fillPattern(Pattern<ClickableItem> pattern);

    /**
     * Fills the inventory with the given {@link Pattern}.
     * <br>
     * The pattern will start at the given slot index.
     *
     * @param pattern    the filling pattern
     * @param startIndex the start slot index for the filling
     * @return <code>this</code>, for chained calls
     * @see #fillPattern(Pattern) To fill the pattern from the first slot
     * @see #fillPattern(Pattern, int, int) To fill the pattern from the provided row and column
     * @see #fillPattern(Pattern, SlotPos) To fill the pattern from the provided slot pos
     */
    InventoryContents fillPattern(Pattern<ClickableItem> pattern, int startIndex);

    /**
     * Fills the inventory with the given {@link Pattern}.
     * <br>
     * The pattern will start at the given slot position based on the provided row and column
     *
     * @param pattern     the filling pattern
     * @param startRow    the start row of the slot for filling
     * @param startColumn the start column of the slot for filling
     * @return <code>this</code>, for chained calls
     * @see #fillPattern(Pattern) To fill the pattern from the first slot
     * @see #fillPattern(Pattern, int) To fill the pattern from the provided slot index
     * @see #fillPattern(Pattern, SlotPos) To fill the pattern from the provided slot pos
     */
    InventoryContents fillPattern(Pattern<ClickableItem> pattern, int startRow, int startColumn);

    /**
     * Fills the inventory with the given {@link Pattern}.
     * <br>
     * The pattern will start at the given slot position
     *
     * @param pattern  the filling pattern
     * @param startPos the start position of the slot for filling
     * @return <code>this</code>, for chained calls
     * @see #fillPattern(Pattern) To fill the pattern from the first slot
     * @see #fillPattern(Pattern, int) To fill the pattern from the provided slot index
     * @see #fillPattern(Pattern, int, int) To fill the pattern from the provided row and column
     */
    InventoryContents fillPattern(Pattern<ClickableItem> pattern, SlotPos startPos);

    /**
     * Fills the inventory with the given {@link Pattern}.
     * <br>
     * The pattern will start at the first slot and end at the last slot.
     * If the pattern is not big enough, it will wrap around to the other side and repeat the pattern
     * <br>
     * The top-left corner of the specified inventory area is also the top-left corner of the specified pattern
     * <br>
     * <b>For this to work the pattern needs to be created with <code>wrapAround</code> enabled</b>
     *
     * @param pattern the filling pattern
     * @return <code>this</code>, for chained calls
     * @see #fillPatternRepeating(Pattern, int, int) To fill a repeating pattern using slot indexes
     * @see #fillPatternRepeating(Pattern, int, int, int, int) To fill a repeating pattern using slot positions contructed from their rows and columns
     * @see #fillPatternRepeating(Pattern, SlotPos, SlotPos) To filla a repeating pattern using slot positions
     */
    InventoryContents fillPatternRepeating(Pattern<ClickableItem> pattern);

    /**
     * Fills the inventory with the given {@link Pattern}.
     * <br>
     * The pattern will start at the first slot index and end at the second slot index.
     * If the pattern is not big enough, it will wrap around to the other side and repeat the pattern
     * <br>
     * The top-left corner of the specified inventory area is also the top-left corner of the specified pattern
     * <br>
     * If <code>endIndex</code> is a negative value it is set to the bottom-right corner
     * <br>
     * <b>For this to work the pattern needs to be created with <code>wrapAround</code> enabled</b>
     *
     * @param pattern    the filling pattern
     * @param startIndex the start slot index where the pattern should begin
     * @param endIndex   the end slot index where the pattern should end
     * @return <code>this</code>, for chained calls
     * @see #fillPatternRepeating(Pattern) To fill a repeating pattern into the whole inventory
     * @see #fillPatternRepeating(Pattern, int, int, int, int) To fill a repeating pattern using slot positions contructed from their rows and columns
     * @see #fillPatternRepeating(Pattern, SlotPos, SlotPos) To filla a repeating pattern using slot positions
     */
    InventoryContents fillPatternRepeating(Pattern<ClickableItem> pattern, int startIndex, int endIndex);

    /**
     * Fills the inventory with the given {@link Pattern}.
     * <br>
     * The pattern will start at the given slot position and end at the second slot position.
     * If the pattern is not big enough, it will wrap around to the other side and repeat the pattern
     * <br>
     * The top-left corner of the specified inventory area is also the top-left corner of the specified pattern
     * <br>
     * If <code>endRow</code> is a negative value, endRow is automatically set to the max row size,
     * if <code>endColumn</code> is a negative value, endColumn is automatically set to the max column size.
     * <br>
     * <b>For this to work the pattern needs to be created with <code>wrapAround</code> enabled</b>
     *
     * @param pattern     the filling pattern
     * @param startRow    the start row of the slot for filling
     * @param startColumn the start column of the slot for filling
     * @param endRow      the end row of the slot for filling
     * @param endColumn   the end column of the slot for filling
     * @return <code>this</code>, for chained calls
     * @see #fillPatternRepeating(Pattern) To fill a repeating pattern into the whole inventory
     * @see #fillPatternRepeating(Pattern, int, int) To fill a repeating pattern using slot indexes
     * @see #fillPatternRepeating(Pattern, SlotPos, SlotPos) To filla a repeating pattern using slot positions
     */
    InventoryContents fillPatternRepeating(Pattern<ClickableItem> pattern, int startRow, int startColumn, int endRow, int endColumn);

    /**
     * Fills the inventory with the given {@link Pattern}.
     * <br>
     * The pattern will start at the given slot position and end at the second slot position.
     * If the pattern is not big enough, it will wrap around to the other side and repeat the pattern
     * <br>
     * The top-left corner of the specified inventory area is also the top-left corner of the specified pattern
     * <br>
     * If the row of <code>endPos</code> is a negative value, endRow is automatically set to the max row size,
     * if the column of <code>endPos</code> is a negative value, endColumn is automatically set to the max column size.
     * <br>
     * <b>For this to work the pattern needs to be created with <code>wrapAround</code> enabled</b>
     *
     * @param pattern  the filling pattern
     * @param startPos the position where the pattern should start
     * @param endPos   the position where the pattern should end
     * @return <code>this</code>, for chained calls
     * @see #fillPatternRepeating(Pattern) To fill a repeating pattern into the whole inventory
     * @see #fillPatternRepeating(Pattern, int, int) To fill a repeating pattern using slot indexes
     * @see #fillPatternRepeating(Pattern, int, int, int, int) To fill a repeating pattern using slot positions contructed from their rows and columns
     */
    InventoryContents fillPatternRepeating(Pattern<ClickableItem> pattern, SlotPos startPos, SlotPos endPos);

    /**
     * Gets the value of the property with the given name.
     *
     * @param name the property's name
     * @param <T>  the type of the value
     * @return the property's value
     */
    <T> T property(String name);

    /**
     * Gets the value of the property with the given name,
     * or a default value if the property isn't set.
     *
     * @param name the property's name
     * @param def  the default value
     * @param <T>  the type of the value
     * @return the property's value, or the given default value
     */
    <T> T property(String name, T def);

    /**
     * Sets the value of the property with the given name.
     * <br>
     * This will replace the existing value for the property,
     * if there is one.
     *
     * @param name  the property's name
     * @param value the new property's value
     * @return <code>this</code>, for chained calls
     */
    InventoryContents setProperty(String name, Object value);

    class Impl implements InventoryContents {

        private final SmartInventory inv;
        private final Player player;

        private final ClickableItem[][] contents;

        private Pagination pagination = new Pagination.Impl();
        private Map<String, SlotIterator> iterators = new HashMap<>();
        private Map<String, Object> properties = new HashMap<>();

        public Impl(SmartInventory inv, Player player) {
            this.inv = inv;
            this.player = player;
            this.contents = new ClickableItem[inv.getRows()][inv.getColumns()];
        }

        @Override
        public SmartInventory inventory() {
            return inv;
        }

        @Override
        public Pagination pagination() {
            return pagination;
        }

        @Override
        public Optional<SlotIterator> iterator(String id) {
            return Optional.ofNullable(this.iterators.get(id));
        }

        @Override
        public SlotIterator newIterator(String id, SlotIterator.Type type, int startRow, int startColumn) {
            SlotIterator iterator = new SlotIterator.Impl(this, inv,
                    type, startRow, startColumn);

            this.iterators.put(id, iterator);
            return iterator;
        }

        @Override
        public SlotIterator newIterator(String id, SlotIterator.Type type, SlotPos startPos) {
            return newIterator(id, type, startPos.getRow(), startPos.getColumn());
        }

        @Override
        public SlotIterator newIterator(SlotIterator.Type type, int startRow, int startColumn) {
            return new SlotIterator.Impl(this, inv, type, startRow, startColumn);
        }

        @Override
        public SlotIterator newIterator(SlotIterator.Type type, SlotPos startPos) {
            return newIterator(type, startPos.getRow(), startPos.getColumn());
        }

        @Override
        public ClickableItem[][] all() {
            return contents;
        }

        @Override
        public Optional<SlotPos> firstEmpty() {
            for (int row = 0; row < contents.length; row++) {
                for (int column = 0; column < contents[0].length; column++) {
                    if (!this.get(row, column).isPresent())
                        return Optional.of(new SlotPos(row, column));
                }
            }

            return Optional.empty();
        }

        @Override
        public Optional<ClickableItem> get(int index) {
            int columnCount = this.inv.getColumns();

            return get(index / columnCount, index % columnCount);
        }

        @Override
        public Optional<ClickableItem> get(int row, int column) {
            if (row < 0 || row >= contents.length)
                return Optional.empty();
            if (column < 0 || column >= contents[row].length)
                return Optional.empty();

            return Optional.ofNullable(contents[row][column]);
        }

        @Override
        public Optional<ClickableItem> get(SlotPos slotPos) {
            return get(slotPos.getRow(), slotPos.getColumn());
        }

        @Override
        public InventoryContents set(int index, ClickableItem item) {
            int columnCount = this.inv.getColumns();

            return set(index / columnCount, index % columnCount, item);
        }

        @Override
        public InventoryContents set(int row, int column, ClickableItem item) {
            if (row < 0 || row >= contents.length)
                return this;
            if (column < 0 || column >= contents[row].length)
                return this;

            contents[row][column] = item;
            update(row, column, item != null ? item.getItem() : null);
            return this;
        }

        @Override
        public InventoryContents set(SlotPos slotPos, ClickableItem item) {
            return set(slotPos.getRow(), slotPos.getColumn(), item);
        }

        @Override
        public InventoryContents add(ClickableItem item) {
            for (int row = 0; row < contents.length; row++) {
                for (int column = 0; column < contents[0].length; column++) {
                    if (contents[row][column] == null) {
                        set(row, column, item);
                        return this;
                    }
                }
            }

            return this;
        }

        @Override
        public Optional<SlotPos> findItemStack(ItemStack itemStack) {
            Preconditions.checkNotNull(itemStack, "The itemstack to look for cannot be null!");
            for (int row = 0; row < contents.length; row++) {
                for (int column = 0; column < contents[0].length; column++) {
                    if (contents[row][column] != null &&
                            itemStack.isSimilar(contents[row][column].getItem())) {
                        return Optional.of(SlotPos.of(row, column));
                    }
                }
            }
            return Optional.empty();
        }

        @Override
        public Optional<SlotPos> findItemStack(ClickableItem clickableItem) {
            Preconditions.checkNotNull(clickableItem, "The clickable item to look for cannot be null!");
            return findItemStack(clickableItem.getItem());
        }

        @Override
        public InventoryContents fill(ClickableItem item) {
            for (int row = 0; row < contents.length; row++)
                for (int column = 0; column < contents[row].length; column++)
                    set(row, column, item);

            return this;
        }

        @Override
        public InventoryContents fillRow(int row, ClickableItem item) {
            if (row < 0 || row >= contents.length)
                return this;

            for (int column = 0; column < contents[row].length; column++)
                set(row, column, item);

            return this;
        }

        @Override
        public InventoryContents fillColumn(int column, ClickableItem item) {
            if (column < 0 || column >= contents[0].length)
                return this;

            for (int row = 0; row < contents.length; row++)
                set(row, column, item);

            return this;
        }

        @Override
        public InventoryContents fillBorders(ClickableItem item) {
            fillRect(0, 0, inv.getRows() - 1, inv.getColumns() - 1, item);
            return this;
        }

        @Override
        public InventoryContents fillRect(int fromIndex, int toIndex, ClickableItem item) {
            int columnCount = this.inv.getColumns();

            return fillRect(
                    fromIndex / columnCount, fromIndex % columnCount,
                    toIndex / columnCount, toIndex % columnCount,
                    item
            );
        }

        @Override
        public InventoryContents fillRect(int fromRow, int fromColumn, int toRow, int toColumn, ClickableItem item) {
            for (int row = fromRow; row <= toRow; row++) {
                for (int column = fromColumn; column <= toColumn; column++) {
                    if (row != fromRow && row != toRow && column != fromColumn && column != toColumn)
                        continue;

                    set(row, column, item);
                }
            }

            return this;
        }

        @Override
        public InventoryContents fillRect(SlotPos fromPos, SlotPos toPos, ClickableItem item) {
            return fillRect(fromPos.getRow(), fromPos.getColumn(), toPos.getRow(), toPos.getColumn(), item);
        }

        @Override
        public InventoryContents fillSquare(int fromIndex, int toIndex, ClickableItem item) {
            int columnCount = this.inv.getColumns();

            return fillSquare(
                    fromIndex / columnCount, fromIndex % columnCount,
                    toIndex / columnCount, toIndex % columnCount,
                    item
            );
        }

        @Override
        public InventoryContents fillSquare(int fromRow, int fromColumn, int toRow, int toColumn, ClickableItem item) {
            Preconditions.checkArgument(fromRow < toRow, "The start row needs to be lower than the end row");
            Preconditions.checkArgument(fromColumn < toColumn, "The start column needs to be lower than the end column");


            int rowDelta = toRow - fromRow, columnDelta = toColumn - fromColumn;
            for (int row = 0; row <= rowDelta; row++) {
                for (int column = 0; column <= columnDelta; column++) {
                    set(fromRow + row, fromColumn + column, item);
                }
            }
            return this;
        }

        @Override
        public InventoryContents fillSquare(SlotPos fromPos, SlotPos toPos, ClickableItem item) {
            return fillSquare(fromPos.getRow(), fromPos.getColumn(), toPos.getRow(), toPos.getColumn(), item);
        }

        @Override
        public InventoryContents fillPattern(Pattern<ClickableItem> pattern) {
            return fillPattern(pattern, 0, 0);
        }

        @Override
        public InventoryContents fillPattern(Pattern<ClickableItem> pattern, int startIndex) {
            int columnCount = this.inv.getColumns();

            return fillPattern(pattern, startIndex / columnCount, startIndex % columnCount);
        }

        @Override
        public InventoryContents fillPattern(Pattern<ClickableItem> pattern, SlotPos startPos) {
            return fillPattern(pattern, startPos.getRow(), startPos.getColumn());
        }

        @Override
        public InventoryContents fillPatternRepeating(Pattern<ClickableItem> pattern) {
            return fillPatternRepeating(pattern, 0, 0, -1, -1);
        }

        @Override
        public InventoryContents fillPatternRepeating(Pattern<ClickableItem> pattern, int startIndex, int endIndex) {
            int columnCount = this.inv.getColumns();
            boolean maxSize = endIndex < 0;

            return fillPatternRepeating(pattern, startIndex / columnCount, startIndex % columnCount, (maxSize ? -1 : endIndex / columnCount), (maxSize ? -1 : endIndex % columnCount));
        }

        @Override
        public InventoryContents fillPatternRepeating(Pattern<ClickableItem> pattern, int startRow, int startColumn, int endRow, int endColumn) {
            Preconditions.checkArgument(pattern.isWrapAround(), "To fill in a repeating pattern wrapAround needs to be enabled for the pattern to work!");

            if (endRow < 0)
                endRow = this.inv.getRows();
            if (endColumn < 0)
                endColumn = this.inv.getColumns();

            Preconditions.checkArgument(startRow < endRow, "The start row needs to be lower than the end row");
            Preconditions.checkArgument(startColumn < endColumn, "The start column needs to be lower than the end column");

            int rowDelta = endRow - startRow, columnDelta = endColumn - startColumn;
            for (int row = 0; row <= rowDelta; row++) {
                for (int column = 0; column <= columnDelta; column++) {
                    ClickableItem item = pattern.getObject(row, column);

                    if (item != null)
                        set(startRow + row, startColumn + column, item);
                }
            }
            return this;
        }

        @Override
        public InventoryContents fillPatternRepeating(Pattern<ClickableItem> pattern, SlotPos startPos, SlotPos endPos) {
            return fillPatternRepeating(pattern, startPos.getRow(), startPos.getColumn(), endPos.getRow(), endPos.getColumn());
        }

        @Override
        public InventoryContents fillPattern(Pattern<ClickableItem> pattern, int startRow, int startColumn) {
            for (int row = 0; row < pattern.getRowCount(); row++) {
                for (int column = 0; column < pattern.getColumnCount(); column++) {
                    ClickableItem item = pattern.getObject(row, column);

                    if (item != null)
                        set(startRow + row, startColumn + column, item);
                }
            }

            return this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T property(String name) {
            return (T) properties.get(name);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T property(String name, T def) {
            return properties.containsKey(name) ? (T) properties.get(name) : def;
        }

        @Override
        public InventoryContents setProperty(String name, Object value) {
            properties.put(name, value);
            return this;
        }

        private void update(int row, int column, ItemStack item) {
            if (!inv.getManager().getOpenedPlayers(inv).contains(player))
                return;

            Inventory topInventory = player.getOpenInventory().getTopInventory();
            topInventory.setItem(inv.getColumns() * row + column, item);
        }

    }

}
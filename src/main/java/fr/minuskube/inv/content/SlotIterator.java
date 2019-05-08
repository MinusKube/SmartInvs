package fr.minuskube.inv.content;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * <p>
 *     The SlotIterator system allows you to iterate through the slots of
 *     an inventory either {@link SlotIterator.Type#HORIZONTAL horizontally}
 *     or {@link SlotIterator.Type#VERTICAL vertically}.
 * </p>
 *
 * TODO: Add SlotIterator usage example
 *
 */
public interface SlotIterator {

    /**
     * The iterate type of the inventory.
     */
    enum Type {
        /**
         * Iterates horizontally from the left to the right
         * of the inventory, and jump to the next line
         * when the last column is reached.
         */
        HORIZONTAL,

        /**
         * Iterates vertically from the up to the down
         * of the inventory, and jump to the next column
         * when the last line is reached.
         */
        VERTICAL
    }

    /**
     * Gets the item at the current position in the inventory.
     * @return the item at the current position
     */
    Optional<ClickableItem> get();

    /**
     * Replaces the item at the current position in the inventory
     * by the given item.
     *
     * @param item the new item
     * @return <code>this</code>, for chained calls
     */
    SlotIterator set(ClickableItem item);

    /**
     * Moves the cursor to the previous position inside
     * the inventory.
     * <br />
     * This has no effect if the cursor is already
     * at the first position of the inventory.
     *
     * @return <code>this</code>, for chained calls
     */
    SlotIterator previous();

    /**
     * Moves the cursor to the next position inside
     * the inventory.
     * <br />
     * This has no effect if the cursor is already
     * at the last position of the inventory.
     *
     * @return <code>this</code>, for chained calls
     */
    SlotIterator next();

    /**
     * Blacklists the given slot index.
     * <br />
     * Blacklisting a slot will make the iterator
     * skip the given slot and directly go to the next
     * unblacklisted slot.
     *
     * @param index the index to blacklist
     * @return <code>this</code>, for chained calls
     */
    SlotIterator blacklist(int index);

    /**
     * Blacklists the given slot position.
     * <br />
     * Blacklisting a slot will make the iterator
     * skip the given slot and directly go to the next
     * unblacklisted slot.
     *
     * @param row the row of the slot to blacklist
     * @param column the column of the slot to blacklist
     * @return <code>this</code>, for chained calls
     */
    SlotIterator blacklist(int row, int column);

    /**
     * Blacklists the given slot position.
     * <br />
     * Blacklisting a slot will make the iterator
     * skip the given slot and directly go to the next
     * unblacklisted slot.
     *
     * @param slotPos the slot to blacklist
     * @return <code>this</code>, for chained calls
     */
    SlotIterator blacklist(SlotPos slotPos);

    /**
     * Gets the current row of the iterator.
     *
     * @return the current row
     */
    int row();

    /**
     * Sets the current row of the iterator.
     *
     * @param row the new row
     * @return <code>this</code>, for chained calls
     */
    SlotIterator row(int row);

    /**
     * Gets the current column of the iterator.
     *
     * @return the current column
     */
    int column();

    /**
     * Sets the current column of the iterator.
     *
     * @param column the new column
     * @return <code>this</code>, for chained calls
     */
    SlotIterator column(int column);

    /**
     * Checks if this iterator has been started.
     * <br />
     * An iterator is not started until any
     * of the {@link SlotIterator#previous()}
     * or the {@link SlotIterator#next()} methods have been called.
     *
     * @return <code>true</code> if this iterator has been started
     */
    boolean started();

    /**
     * Checks if this iterator has been ended.
     * <br />
     * An iterator is not ended until it has reached the last
     * slot of the inventory.
     *
     * @return <code>true</code> if this iterator has been ended
     */
    boolean ended();

    /**
     * Gets the value of the allow override option.
     * <br />
     * - If this is <code>true</code>, the iterator will override any
     * existing item it founds on its way.
     * <br />
     * - If this is <code>false</code>, the iterator will skip
     * the slots which are not empty.
     *
     * @return <code>true</code> if this iterator allows to override
     */
    boolean doesAllowOverride();

    /**
     * Sets the value of the allow override option.
     * <br />
     * - If this is <code>true</code>, the iterator will override any
     * existing item it founds on its way.
     * <br />
     * - If this is <code>false</code>, the iterator will skip
     * the slots which are not empty.
     *
     * @param override the value of the allow override option
     * @return <code>this</code>, for chained calls
     */
    SlotIterator allowOverride(boolean override);


    class Impl implements SlotIterator {

        private InventoryContents contents;
        private final SmartInventory inv;

        private final Type type;
        private boolean started = false;
        private boolean allowOverride = true;
        private int row, column;

        private Set<SlotPos> blacklisted = new HashSet<>();

        public Impl(InventoryContents contents, SmartInventory inv,
                    Type type, int startRow, int startColumn) {

            this.contents = contents;
            this.inv = inv;

            this.type = type;

            this.row = startRow;
            this.column = startColumn;
        }

        public Impl(InventoryContents contents, SmartInventory inv,
                    Type type) {

            this(contents, inv, type, 0, 0);
        }

        @Override
        public Optional<ClickableItem> get() {
            return contents.get(row, column);
        }

        @Override
        public SlotIterator set(ClickableItem item) {
            if(canPlace())
                contents.set(row, column, item);

            return this;
        }

        @Override
        public SlotIterator previous() {
            if(row == 0 && column == 0) {
                this.started = true;
                return this;
            }

            do {
                if(!this.started) {
                    this.started = true;
                }
                else {
                    switch(type) {
                        case HORIZONTAL:
                            column--;

                            if(column == 0) {
                                column = inv.getColumns() - 1;
                                row--;
                            }
                            break;
                        case VERTICAL:
                            row--;

                            if(row == 0) {
                                row = inv.getRows() - 1;
                                column--;
                            }
                            break;
                    }
                }
            }
            while(!canPlace() && (row != 0 || column != 0));

            return this;
        }

        @Override
        public SlotIterator next() {
            if(ended()) {
                this.started = true;
                return this;
            }

            do {
                if(!this.started) {
                    this.started = true;
                }
                else {
                    switch(type) {
                        case HORIZONTAL:
                            column = ++column % inv.getColumns();

                            if(column == 0)
                                row++;
                            break;
                        case VERTICAL:
                            row = ++row % inv.getRows();

                            if(row == 0)
                                column++;
                            break;
                    }
                }
            }
            while(!canPlace() && !ended());

            return this;
        }

        @Override
        public SlotIterator blacklist(int index) {
            int columnCount = this.inv.getColumns();

            this.blacklisted.add(SlotPos.of(index / columnCount, index % columnCount));
            return this;
        }

        @Override
        public SlotIterator blacklist(int row, int column) {
            this.blacklisted.add(SlotPos.of(row, column));
            return this;
        }

        @Override
        public SlotIterator blacklist(SlotPos slotPos) {
            return blacklist(slotPos.getRow(), slotPos.getColumn());
        }

        @Override
        public int row() { return row; }

        @Override
        public SlotIterator row(int row) {
            this.row = row;
            return this;
        }

        @Override
        public int column() { return column; }

        @Override
        public SlotIterator column(int column) {
            this.column = column;
            return this;
        }

        @Override
        public boolean started() {
            return this.started;
        }

        @Override
        public boolean ended() {
            return row == inv.getRows() - 1
                    && column == inv.getColumns() - 1;
        }

        @Override
        public boolean doesAllowOverride() { return allowOverride; }

        @Override
        public SlotIterator allowOverride(boolean override) {
            this.allowOverride = override;
            return this;
        }

        private boolean canPlace() {
            return !blacklisted.contains(SlotPos.of(row, column)) && (allowOverride || !this.get().isPresent());
        }

    }

}
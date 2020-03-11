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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * <p>
 * The SlotIterator system allows you to iterate through the slots of
 * an inventory either {@link SlotIterator.Type#HORIZONTAL horizontally}
 * or {@link SlotIterator.Type#VERTICAL vertically}.
 * </p>
 *///TODO: Add SlotIterator usage example
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
     *
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
     * <br>
     * This has no effect if the cursor is already
     * at the first position of the inventory.
     *
     * @return <code>this</code>, for chained calls
     */
    SlotIterator previous();

    /**
     * Moves the cursor to the next position inside
     * the inventory.
     * <br>
     * This has no effect if the cursor is already
     * at the last position of the inventory.
     *
     * @return <code>this</code>, for chained calls
     */
    SlotIterator next();

    /**
     * Blacklists the given slot index.
     * <br>
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
     * <br>
     * Blacklisting a slot will make the iterator
     * skip the given slot and directly go to the next
     * unblacklisted slot.
     *
     * @param row    the row of the slot to blacklist
     * @param column the column of the slot to blacklist
     * @return <code>this</code>, for chained calls
     */
    SlotIterator blacklist(int row, int column);

    /**
     * Blacklists the given slot position.
     * <br>
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
     * Resets iterator to its original position specified while creation.
     * <br>
     * When the iterator gets reset to its original position, <code>started</code> gets set back to <code>false</code>
     *
     * @return <code>this</code>, for chained calls
     */
    SlotIterator reset();

    /**
     * Checks if this iterator has been started.
     * <br>
     * An iterator is not started until any
     * of the {@link SlotIterator#previous()}
     * or the {@link SlotIterator#next()} methods have been called.
     *
     * @return <code>true</code> if this iterator has been started
     */
    boolean started();

    /**
     * Checks if this iterator has been ended.
     * <br>
     * An iterator is not ended until it has reached the last
     * slot of the inventory.
     *
     * @return <code>true</code> if this iterator has been ended
     */
    boolean ended();

    /**
     * Sets the slot where the iterator should end.
     * <br><br>
     * If <code>row</code> is a negative value, it is set to the maximum row count.<br>
     * If <code>column</code> is a negative value, it is set to maximum column count.
     *
     * @param row    The row where the iterator should end
     * @param column The column where the iterator should end
     * @return <code>this</code>, for chained calls
     */
    SlotIterator endPosition(int row, int column);

    /**
     * Sets the slot where the iterator should end.
     * <br><br>
     * If the row of the SlotPos is a negative value, it is set to the maximum row count.<br>
     * If the column of the SlotPos is a negative value, it is set to maximum column count.
     *
     * @param endPosition The slot where the iterator should end
     * @return <code>this</code>, for chained calls
     */
    SlotIterator endPosition(SlotPos endPosition);

    /**
     * Gets the value of the allow override option.
     * <br>
     * - If this is <code>true</code>, the iterator will override any
     * existing item it founds on its way.
     * <br>
     * - If this is <code>false</code>, the iterator will skip
     * the slots which are not empty.
     *
     * @return <code>true</code> if this iterator allows to override
     */
    boolean doesAllowOverride();

    /**
     * Sets the value of the allow override option.
     * <br>
     * - If this is <code>true</code>, the iterator will override any
     * existing item it founds on its way.
     * <br>
     * - If this is <code>false</code>, the iterator will skip
     * the slots which are not empty.
     *
     * @param override the value of the allow override option
     * @return <code>this</code>, for chained calls
     */
    SlotIterator allowOverride(boolean override);

    /**
     * Setting a pattern using this method will use it as a guideline where the slot iterator can set items or not.
     * If the pattern doesn't fill the whole inventory, the slot iterator is limited to the space the pattern provides.
     * If the pattern has the <code>wrapAround</code> flag set, then the iterator can iterate over the entire inventory,
     * even if the pattern would not fill it by itself
     * <br><br>
     * If the provided pattern has no default value set, this method will set it to <code>false</code>
     * <br><br>
     * If you pass <code>null</code> into the <code>pattern</code> parameter, this functionality will be disabled and
     * the iterator will continue to work as normal.
     *
     * @param pattern The pattern to use as a guideline
     * @return <code>this</code>, for chained calls
     */
    SlotIterator withPattern(Pattern<Boolean> pattern);

    /**
     * Setting a pattern using this method will use it as a guideline where the slot iterator can set items or not.
     * If the pattern doesn't fill the whole inventory, the slot iterator is limited to the space the pattern provides.
     * If the pattern has the <code>wrapAround</code> flag set, then the iterator can iterate over the entire inventory,
     * even if the pattern would not fill it by itself
     * <br><br>
     * The offset defines the top-left corner of the pattern. If the <code>wrapAround</code> flag is set, then the entire
     * pattern will be just shifted by the given amount.
     * <br><br>
     * If the provided pattern has no default value set, this method will set it to <code>false</code>
     * <br><br>
     * If you pass <code>null</code> into the <code>pattern</code> parameter, this functionality will be disabled and
     * the iterator will continue to work as normal.
     *
     * @param pattern      The pattern to use as a guideline
     * @param rowOffset    The row offset from the top left corner
     * @param columnOffset The column offset from the top left corner
     * @return <code>this</code>, for chained calls
     */
    SlotIterator withPattern(Pattern<Boolean> pattern, int rowOffset, int columnOffset);

    /**
     * This method has the inverse effect of {@link #withPattern(Pattern)}, where the other method would only allow the
     * iterator to go, this method prohibits this slots to iterate over
     *
     * @param pattern The pattern where the slot iterator cannot iterate
     * @return <code>this</code>, for chained calls
     */
    SlotIterator blacklistPattern(Pattern<Boolean> pattern);

    /**
     * This method has the inverse effect of {@link #withPattern(Pattern, int, int)}, where the other method would only allow the
     * iterator to go, this method prohibits this slots to iterate over
     *
     * @param pattern      The pattern where the slot iterator cannot iterate
     * @param rowOffset    The row offset from the top left corner
     * @param columnOffset The column offset from the top left corner
     * @return <code>this</code>, for chained calls
     */
    SlotIterator blacklistPattern(Pattern<Boolean> pattern, int rowOffset, int columnOffset);

    class Impl implements SlotIterator {

        private InventoryContents contents;
        private final SmartInventory inv;

        private final Type type;
        private boolean started = false;
        private boolean allowOverride = true;
        private int endRow, endColumn;
        private int startRow, startColumn;
        private int row, column;

        private Set<SlotPos> blacklisted = new HashSet<>();

        private int patternRowOffset, patternColumnOffset;
        private Pattern<Boolean> pattern;

        private int blacklistPatternRowOffset, blacklistPatternColumnOffset;
        private Pattern<Boolean> blacklistPattern;

        public Impl(InventoryContents contents, SmartInventory inv,
                    Type type, int startRow, int startColumn) {

            this.contents = contents;
            this.inv = inv;

            this.type = type;

            this.endRow = this.inv.getRows() - 1;
            this.endColumn = this.inv.getColumns() - 1;

            this.startRow = this.row = startRow;
            this.startColumn = this.column = startColumn;
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
            if (canPlace())
                contents.set(row, column, item);

            return this;
        }

        @Override
        public SlotIterator previous() {
            if (row == 0 && column == 0) {
                this.started = true;
                return this;
            }

            do {
                if (!this.started) {
                    this.started = true;
                } else {
                    switch (type) {
                        case HORIZONTAL:
                            column--;

                            if (column == 0) {
                                column = inv.getColumns() - 1;
                                row--;
                            }
                            break;
                        case VERTICAL:
                            row--;

                            if (row == 0) {
                                row = inv.getRows() - 1;
                                column--;
                            }
                            break;
                    }
                }
            }
            while (!canPlace() && (row != 0 || column != 0));

            return this;
        }

        @Override
        public SlotIterator next() {
            if (ended()) {
                this.started = true;
                return this;
            }

            do {
                if (!this.started) {
                    this.started = true;
                } else {
                    switch (type) {
                        case HORIZONTAL:
                            column = ++column % inv.getColumns();

                            if (column == 0)
                                row++;
                            break;
                        case VERTICAL:
                            row = ++row % inv.getRows();

                            if (row == 0)
                                column++;
                            break;
                    }
                }
            }
            while (!canPlace() && !ended());

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
        public int row() {
            return row;
        }

        @Override
        public SlotIterator row(int row) {
            this.row = row;
            return this;
        }

        @Override
        public int column() {
            return column;
        }

        @Override
        public SlotIterator column(int column) {
            this.column = column;
            return this;
        }

        @Override
        public SlotIterator reset() {
            this.started = false;
            this.row = this.startRow;
            this.column = this.startColumn;
            return this;
        }

        @Override
        public boolean started() {
            return this.started;
        }

        @Override
        public boolean ended() {
            return row == endRow
                    && column == endColumn;
        }

        @Override
        public SlotIterator endPosition(int row, int column) {
            if (row < 0)
                row = this.inv.getRows() - 1;
            if (column < 0)
                column = this.inv.getColumns() - 1;
            Preconditions.checkArgument(row * column >= this.startRow * this.startColumn, "The end position needs to be after the start of the slot iterator");

            this.endRow = row;
            this.endColumn = column;

            return this;
        }

        @Override
        public SlotIterator endPosition(SlotPos endPosition) {
            return endPosition(endPosition.getRow(), endPosition.getColumn());
        }

        @Override
        public boolean doesAllowOverride() {
            return allowOverride;
        }

        @Override
        public SlotIterator allowOverride(boolean override) {
            this.allowOverride = override;
            return this;
        }

        @Override
        public SlotIterator withPattern(Pattern<Boolean> pattern) {
            return withPattern(pattern, 0, 0);
        }

        @Override
        public SlotIterator withPattern(Pattern<Boolean> pattern, int rowOffset, int columnOffset) {
            this.patternRowOffset = rowOffset;
            this.patternColumnOffset = columnOffset;
            if (pattern.getDefault() == null)
                pattern.setDefault(false);
            this.pattern = pattern;
            return this;
        }

        @Override
        public SlotIterator blacklistPattern(Pattern<Boolean> pattern) {
            return blacklistPattern(pattern, 0, 0);
        }

        @Override
        public SlotIterator blacklistPattern(Pattern<Boolean> pattern, int rowOffset, int columnOffset) {
            this.blacklistPatternRowOffset = rowOffset;
            this.blacklistPatternColumnOffset = columnOffset;
            if (pattern.getDefault() == null)
                pattern.setDefault(false);
            this.blacklistPattern = pattern;
            return this;
        }

        private boolean canPlace() {
            boolean patternAllows = true, blacklistPatternAllows = true;
            if (pattern != null) {
                patternAllows = checkPattern(pattern, patternRowOffset, patternColumnOffset);
            }
            if (blacklistPattern != null) {
                blacklistPatternAllows = !checkPattern(blacklistPattern, blacklistPatternRowOffset, blacklistPatternColumnOffset);
            }
            return !blacklisted.contains(SlotPos.of(row, column)) && (allowOverride || !this.get().isPresent()) && patternAllows && blacklistPatternAllows;
        }

        private boolean checkPattern(Pattern<Boolean> pattern, int rowOffset, int columnOffset) {
            if (pattern.isWrapAround()) {
                return pattern.getObject(row - rowOffset, column - columnOffset);
            } else {
                return row >= rowOffset && column >= columnOffset &&
                        row < (pattern.getRowCount() + rowOffset) && column < (pattern.getColumnCount() + columnOffset) &&
                        pattern.getObject(row - rowOffset, column - columnOffset);
            }
        }
    }
}
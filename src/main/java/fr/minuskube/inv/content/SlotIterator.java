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
import java.util.Optional;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * The SlotIterator system allows you to iterate through the slots of
 * an inventory either {@link SlotIterator.Type#HORIZONTAL horizontally}
 * or {@link SlotIterator.Type#VERTICAL vertically}.
 * </p>
 *///TODO: Add SlotIterator usage example
public interface SlotIterator {

    /**
     * Gets the item at the current position in the inventory.
     *
     * @return the item at the current position
     */
    @NotNull
    Optional<ClickableItem> get();

    /**
     * Replaces the item at the current position in the inventory
     * by the given item.
     *
     * @param item the new item
     * @return {@code this}, for chained calls
     */
    @NotNull
    SlotIterator set(@NotNull ClickableItem item);

    /**
     * Moves the cursor to the previous position inside
     * the inventory.
     * <br>
     * This has no effect if the cursor is already
     * at the first position of the inventory.
     *
     * @return {@code this}, for chained calls
     */
    @NotNull
    SlotIterator previous();

    /**
     * Moves the cursor to the next position inside
     * the inventory.
     * <br>
     * This has no effect if the cursor is already
     * at the last position of the inventory.
     *
     * @return {@code this}, for chained calls
     */
    @NotNull
    SlotIterator next();

    /**
     * Blacklists the given slot index.
     * <br>
     * Blacklisting a slot will make the iterator
     * skip the given slot and directly go to the next
     * unblacklisted slot.
     *
     * @param index the index to blacklist
     * @return {@code this}, for chained calls
     */
    @NotNull
    SlotIterator blacklist(int index);

    /**
     * Blacklists the given slot position.
     * <br>
     * Blacklisting a slot will make the iterator
     * skip the given slot and directly go to the next
     * unblacklisted slot.
     *
     * @param row the row of the slot to blacklist
     * @param column the column of the slot to blacklist
     * @return {@code this}, for chained calls
     */
    @NotNull
    SlotIterator blacklist(int row, int column);

    /**
     * Blacklists the given slot position.
     * <br>
     * Blacklisting a slot will make the iterator
     * skip the given slot and directly go to the next
     * unblacklisted slot.
     *
     * @param slotPos the slot to blacklist
     * @return {@code this}, for chained calls
     */
    @NotNull
    SlotIterator blacklist(@NotNull SlotPos slotPos);

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
     * @return {@code this}, for chained calls
     */
    @NotNull
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
     * @return {@code this}, for chained calls
     */
    @NotNull
    SlotIterator column(int column);

    /**
     * Resets iterator to its original position specified while creation.
     * <br>
     * When the iterator gets reset to its original position, {@code started} gets set back to {@code false}
     *
     * @return {@code this}, for chained calls
     */
    @NotNull
    SlotIterator reset();

    /**
     * Checks if this iterator has been started.
     * <br>
     * An iterator is not started until any
     * of the {@link SlotIterator#previous()}
     * or the {@link SlotIterator#next()} methods have been called.
     *
     * @return {@code true} if this iterator has been started
     */
    boolean started();

    /**
     * Checks if this iterator has been ended.
     * <br>
     * An iterator is not ended until it has reached the last
     * slot of the inventory.
     *
     * @return {@code true} if this iterator has been ended
     */
    boolean ended();

    /**
     * Sets the slot where the iterator should end.
     * <br><br>
     * If {@code row} is a negative value, it is set to the maximum row count.<br>
     * If {@code column} is a negative value, it is set to maximum column count.
     *
     * @param row The row where the iterator should end
     * @param column The column where the iterator should end
     * @return {@code this}, for chained calls
     */
    @NotNull
    SlotIterator endPosition(int row, int column);

    /**
     * Sets the slot where the iterator should end.
     * <br><br>
     * If the row of the SlotPos is a negative value, it is set to the maximum row count.<br>
     * If the column of the SlotPos is a negative value, it is set to maximum column count.
     *
     * @param endPosition The slot where the iterator should end
     * @return {@code this}, for chained calls
     */
    @NotNull
    SlotIterator endPosition(@NotNull SlotPos endPosition);

    /**
     * Gets the value of the allow override option.
     * <br>
     * - If this is {@code true}, the iterator will override any
     * existing item it founds on its way.
     * <br>
     * - If this is {@code false}, the iterator will skip
     * the slots which are not empty.
     *
     * @return {@code true} if this iterator allows to override
     */
    boolean doesAllowOverride();

    /**
     * Sets the value of the allow override option.
     * <br>
     * - If this is {@code true}, the iterator will override any
     * existing item it founds on its way.
     * <br>
     * - If this is {@code false}, the iterator will skip
     * the slots which are not empty.
     *
     * @param override the value of the allow override option
     * @return {@code this}, for chained calls
     */
    @NotNull
    SlotIterator allowOverride(boolean override);

    /**
     * Setting a pattern using this method will use it as a guideline where the slot iterator can set items or not.
     * If the pattern doesn't fill the whole inventory, the slot iterator is limited to the space the pattern provides.
     * If the pattern has the {@code wrapAround} flag set, then the iterator can iterate over the entire inventory,
     * even if the pattern would not fill it by itself
     * <br><br>
     * If the provided pattern has no default value set, this method will set it to {@code false}
     * <br><br>
     * If you pass {@code null} into the {@code pattern} parameter, this functionality will be disabled and
     * the iterator will continue to work as normal.
     *
     * @param pattern The pattern to use as a guideline
     * @return {@code this}, for chained calls
     */
    @NotNull
    SlotIterator withPattern(@NotNull Pattern<Boolean> pattern);

    /**
     * Setting a pattern using this method will use it as a guideline where the slot iterator can set items or not.
     * If the pattern doesn't fill the whole inventory, the slot iterator is limited to the space the pattern provides.
     * If the pattern has the {@code wrapAround} flag set, then the iterator can iterate over the entire inventory,
     * even if the pattern would not fill it by itself
     * <br><br>
     * The offset defines the top-left corner of the pattern. If the {@code wrapAround} flag is set, then the entire
     * pattern will be just shifted by the given amount.
     * <br><br>
     * If the provided pattern has no default value set, this method will set it to {@code false}
     * <br><br>
     * If you pass {@code null} into the {@code pattern} parameter, this functionality will be disabled and
     * the iterator will continue to work as normal.
     *
     * @param pattern The pattern to use as a guideline
     * @param rowOffset The row offset from the top left corner
     * @param columnOffset The column offset from the top left corner
     * @return {@code this}, for chained calls
     */
    @NotNull
    SlotIterator withPattern(@NotNull Pattern<Boolean> pattern, int rowOffset, int columnOffset);

    /**
     * This method has the inverse effect of {@link #withPattern(Pattern)}, where the other method would only allow the
     * iterator to go, this method prohibits this slots to iterate over
     *
     * @param pattern The pattern where the slot iterator cannot iterate
     * @return {@code this}, for chained calls
     */
    @NotNull
    SlotIterator blacklistPattern(@NotNull Pattern<Boolean> pattern);

    /**
     * This method has the inverse effect of {@link #withPattern(Pattern, int, int)}, where the other method would only allow the
     * iterator to go, this method prohibits this slots to iterate over
     *
     * @param pattern The pattern where the slot iterator cannot iterate
     * @param rowOffset The row offset from the top left corner
     * @param columnOffset The column offset from the top left corner
     * @return {@code this}, for chained calls
     */
    @NotNull
    SlotIterator blacklistPattern(@NotNull Pattern<Boolean> pattern, int rowOffset, int columnOffset);

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

    final class Impl implements SlotIterator {

        @NotNull
        private final SmartInventory inv;

        @NotNull
        private final SlotIterator.Type type;

        @NotNull
        private final InventoryContents contents;

        private final int startRow;

        private final int startColumn;

        private final Set<SlotPos> blacklisted = new HashSet<>();

        private boolean started;

        private boolean allowOverride = true;

        private int endRow;

        private int endColumn;

        private int row;

        private int column;

        private int patternRowOffset;

        private int patternColumnOffset;

        @Nullable
        private Pattern<Boolean> pattern;

        private int blacklistPatternRowOffset;

        private int blacklistPatternColumnOffset;

        @Nullable
        private Pattern<Boolean> blacklistPattern;

        public Impl(@NotNull final InventoryContents contents, @NotNull final SmartInventory inv,
                    @NotNull final SlotIterator.Type type, final int startRow, final int startColumn) {
            this.contents = contents;
            this.inv = inv;
            this.type = type;
            this.endRow = this.inv.getRows() - 1;
            this.endColumn = this.inv.getColumns() - 1;
            this.startRow = startRow;
            this.row = startRow;
            this.startColumn = startColumn;
            this.column = startColumn;
        }

        public Impl(@NotNull final InventoryContents contents, @NotNull final SmartInventory inv,
                    @NotNull final SlotIterator.Type type) {
            this(contents, inv, type, 0, 0);
        }

        @NotNull
        @Override
        public Optional<ClickableItem> get() {
            return this.contents.get(this.row, this.column);
        }

        @NotNull
        @Override
        public SlotIterator set(@NotNull final ClickableItem item) {
            if (this.canPlace()) {
                this.contents.set(this.row, this.column, item);
            }

            return this;
        }

        @NotNull
        @Override
        public SlotIterator previous() {
            if (this.row == 0 && this.column == 0) {
                this.started = true;
                return this;
            }
            do {
                if (this.started) {
                    switch (this.type) {
                        case HORIZONTAL:
                            this.column--;

                            if (this.column == 0) {
                                this.column = this.inv.getColumns() - 1;
                                this.row--;
                            }
                            break;
                        case VERTICAL:
                            this.row--;

                            if (this.row == 0) {
                                this.row = this.inv.getRows() - 1;
                                this.column--;
                            }
                            break;
                    }
                } else {
                    this.started = true;
                }
            }
            while (!this.canPlace() && (this.row != 0 || this.column != 0));

            return this;
        }

        @NotNull
        @Override
        public SlotIterator next() {
            if (this.ended()) {
                this.started = true;
                return this;
            }

            do {
                if (this.started) {
                    switch (this.type) {
                        case HORIZONTAL:
                            ++this.column;
                            this.column %= this.inv.getColumns();

                            if (this.column == 0) {
                                this.row++;
                            }
                            break;
                        case VERTICAL:
                            ++this.row;
                            this.row %= this.inv.getRows();

                            if (this.row == 0) {
                                this.column++;
                            }
                            break;
                    }
                } else {
                    this.started = true;
                }
            }
            while (!this.canPlace() && !this.ended());

            return this;
        }

        @NotNull
        @Override
        public SlotIterator blacklist(final int index) {
            final int count = this.inv.getColumns();
            this.blacklisted.add(SlotPos.of(index / count, index % count));
            return this;
        }

        @NotNull
        @Override
        public SlotIterator blacklist(final int row, final int column) {
            this.blacklisted.add(SlotPos.of(row, column));
            return this;
        }

        @NotNull
        @Override
        public SlotIterator blacklist(@NotNull final SlotPos slotPos) {
            return this.blacklist(slotPos.getRow(), slotPos.getColumn());
        }

        @Override
        public int row() {
            return this.row;
        }

        @NotNull
        @Override
        public SlotIterator row(final int row) {
            this.row = row;
            return this;
        }

        @Override
        public int column() {
            return this.column;
        }

        @NotNull
        @Override
        public SlotIterator column(final int column) {
            this.column = column;
            return this;
        }

        @NotNull
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
            return this.row == this.endRow
                && this.column == this.endColumn;
        }

        @NotNull
        @Override
        public SlotIterator endPosition(int row, int column) {
            if (row < 0) {
                row = this.inv.getRows() - 1;
            }
            if (column < 0) {
                column = this.inv.getColumns() - 1;
            }
            Preconditions.checkArgument(row * column >= this.startRow * this.startColumn,
                "The end position needs to be after the start of the slot iterator");
            this.endRow = row;
            this.endColumn = column;
            return this;
        }

        @NotNull
        @Override
        public SlotIterator endPosition(@NotNull final SlotPos endPosition) {
            return this.endPosition(endPosition.getRow(), endPosition.getColumn());
        }

        @Override
        public boolean doesAllowOverride() {
            return this.allowOverride;
        }

        @NotNull
        @Override
        public SlotIterator allowOverride(final boolean override) {
            this.allowOverride = override;
            return this;
        }

        @NotNull
        @Override
        public SlotIterator withPattern(@NotNull final Pattern<Boolean> pattern) {
            return this.withPattern(pattern, 0, 0);
        }

        @NotNull
        @Override
        public SlotIterator withPattern(@NotNull final Pattern<Boolean> pattern, final int rowOffset,
                                        final int columnOffset) {
            this.patternRowOffset = rowOffset;
            this.patternColumnOffset = columnOffset;
            if (pattern.getDefault() == null) {
                pattern.setDefault(false);
            }
            this.pattern = pattern;
            return this;
        }

        @NotNull
        @Override
        public SlotIterator blacklistPattern(@NotNull final Pattern<Boolean> pattern) {
            return this.blacklistPattern(pattern, 0, 0);
        }

        @NotNull
        @Override
        public SlotIterator blacklistPattern(@NotNull final Pattern<Boolean> pattern, final int rowOffset,
                                             final int columnOffset) {
            this.blacklistPatternRowOffset = rowOffset;
            this.blacklistPatternColumnOffset = columnOffset;
            if (pattern.getDefault() == null) {
                pattern.setDefault(false);
            }
            this.blacklistPattern = pattern;
            return this;
        }

        private boolean canPlace() {
            boolean patternAllows = true;
            if (this.pattern != null) {
                patternAllows = this.checkPattern(this.pattern, this.patternRowOffset, this.patternColumnOffset);
            }
            boolean blacklistPatternAllows = true;
            if (this.blacklistPattern != null) {
                blacklistPatternAllows = !this.checkPattern(this.blacklistPattern, this.blacklistPatternRowOffset,
                    this.blacklistPatternColumnOffset);
            }
            return !this.blacklisted.contains(SlotPos.of(this.row, this.column)) &&
                (this.allowOverride || !this.get().isPresent()) &&
                patternAllows &&
                blacklistPatternAllows;
        }

        private boolean checkPattern(@NotNull final Pattern<Boolean> pattern, final int rowOffset,
                                     final int columnOffset) {
            if (pattern.isWrapAround()) {
                return pattern.getObject(this.row - rowOffset, this.column - columnOffset);
            }
            return this.row >= rowOffset && this.column >= columnOffset &&
                this.row < pattern.getRowCount() + rowOffset &&
                this.column < pattern.getColumnCount() + columnOffset &&
                pattern.getObject(this.row - rowOffset, this.column - columnOffset);
        }
    }
}

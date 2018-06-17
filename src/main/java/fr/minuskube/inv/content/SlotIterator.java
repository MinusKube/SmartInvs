package fr.minuskube.inv.content;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public interface SlotIterator {

    enum Type {
        HORIZONTAL,
        VERTICAL
    }

    Optional<ClickableItem> get();
    SlotIterator set(ClickableItem item);

    SlotIterator previous();
    SlotIterator next();

    SlotIterator blacklist(int row, int column);
    SlotIterator blacklist(SlotPos slotPos);

    int row();
    SlotIterator row(int row);

    int column();
    SlotIterator column(int column);

    boolean ended();

    boolean doesAllowOverride();
    SlotIterator allowOverride(boolean override);


    class Impl implements SlotIterator {

        private InventoryContents contents;
        private SmartInventory inv;

        private Type type;
        private int row, column;
        private boolean allowOverride;

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
            contents.set(row, column, item);
            return this;
        }

        @Override
        public SlotIterator previous() {
            if(row == 0 && column == 0)
                return this;

            do {
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
            while((row != 0 || column != 0) && (blacklisted.contains(SlotPos.of(row, column))
                    || (!allowOverride && this.get().isPresent())));

            return this;
        }

        @Override
        public SlotIterator next() {
            if(ended())
                return this;

            do {
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
            while(!ended() && (blacklisted.contains(SlotPos.of(row, column))
                    || (!allowOverride && this.get().isPresent())));

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
    }

}
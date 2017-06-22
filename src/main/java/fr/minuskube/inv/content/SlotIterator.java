package fr.minuskube.inv.content;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;

import java.util.Optional;

public interface SlotIterator {

    enum Type {
        HORIZONTAL,
        VERTICAL
    }

    Optional<ClickableItem> get();
    SlotIterator set(ClickableItem item);

    SlotIterator next();

    int row();
    int column();

    boolean ended();


    class Impl implements SlotIterator {

        private InventoryContents contents;
        private SmartInventory inv;

        private Type type;
        private int row, column;

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
        public SlotIterator next() {
            if(ended())
                return this;

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

            return this;
        }

        @Override
        public int row() { return row; }

        @Override
        public int column() { return column; }

        @Override
        public boolean ended() {
            return row == inv.getRows() - 1
                    && column == inv.getColumns() - 1;
        }

    }

}
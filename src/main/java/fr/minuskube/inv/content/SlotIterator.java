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


    class Impl implements SlotIterator {

        private SmartInventory inv;
        private Type type;
        private int row, column;

        public Impl(SmartInventory inv, Type type, int startRow, int startColumn) {
            this.inv = inv;
            this.type = type;

            this.row = startRow;
            this.column = startColumn;
        }

        public Impl(SmartInventory inv, Type type) {
            this(inv, type, 0, 0);
        }

        @Override
        public Optional<ClickableItem> get() {
            return inv.getContents().get(row, column);
        }

        @Override
        public SlotIterator set(ClickableItem item) {
            inv.getContents().set(row, column, item);
            return this;
        }

        @Override
        public SlotIterator next() {
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

    }

}
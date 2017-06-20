package fr.minuskube.inv.content;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;

import java.util.Arrays;
import java.util.Optional;

public interface InventoryContents {

    SlotIterator iterator(SlotIterator.Type type, int startRow, int startColumn);

    ClickableItem[][] all();

    Optional<ClickableItem> get(int row, int column);
    InventoryContents set(int row, int column, ClickableItem item);

    InventoryContents fill(ClickableItem item);
    InventoryContents fillRow(int row, ClickableItem item);
    InventoryContents fillColumn(int column, ClickableItem item);
    InventoryContents fillBorders(ClickableItem item);
    InventoryContents fillRect(int fromRow, int fromColumn,
                               int toRow, int toColumn, ClickableItem item);


    class Impl implements InventoryContents {

        private SmartInventory inv;
        private ClickableItem[][] contents;

        public Impl(SmartInventory inv) {
            this.inv = inv;
            this.contents = new ClickableItem[inv.getRows()][inv.getColumns()];
        }

        @Override
        public SlotIterator iterator(SlotIterator.Type type, int startRow, int startColumn) {
            return new SlotIterator.Impl(inv, type, startRow, startColumn);
        }

        @Override
        public ClickableItem[][] all() { return contents; }

        @Override
        public Optional<ClickableItem> get(int row, int column) {
            // TODO: Handle IOOBE

            return Optional.ofNullable(contents[row][column]);
        }

        @Override
        public InventoryContents set(int row, int column, ClickableItem item) {
            // TODO: Handle IOOBE

            contents[row][column] = item;
            return this;
        }

        @Override
        public InventoryContents fill(ClickableItem item) {
            for(ClickableItem[] row : contents)
                Arrays.fill(row, item);

            return this;
        }

        @Override
        public InventoryContents fillRow(int row, ClickableItem item) {
            // TODO: Handle IOOBE

            Arrays.fill(contents[row], item);
            return this;
        }

        @Override
        public InventoryContents fillColumn(int column, ClickableItem item) {
            // TODO: Handle IOOBE

            for(ClickableItem[] row : contents)
                row[column] = item;

            return this;
        }

        @Override
        public InventoryContents fillBorders(ClickableItem item) {
            fillRect(0, 0, inv.getRows() - 1, inv.getColumns() - 1, item);
            return this;
        }

        @Override
        public InventoryContents fillRect(int fromRow, int fromColumn, int toRow, int toColumn, ClickableItem item) {
            // TODO: Handle IOOBE

            for(int row = fromRow; row <= toRow; row++) {
                for(int column = fromColumn; column <= toColumn; column++) {
                    if(row != fromRow && row != toRow)
                        continue;
                    if(column != fromColumn && column != toColumn)
                        continue;

                    contents[row][column] = item;
                }
            }

            return this;
        }

    }

}
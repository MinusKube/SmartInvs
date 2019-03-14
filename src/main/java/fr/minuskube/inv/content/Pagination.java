package fr.minuskube.inv.content;

import fr.minuskube.inv.ClickableItem;

import java.util.Arrays;

public interface Pagination {

    ClickableItem[] getPageItems();

    int getPage();
    Pagination page(int page);

    boolean isFirst();
    boolean isLast();

    Pagination first();
    Pagination previous();
    Pagination next();
    Pagination last();

    Pagination addToIterator(SlotIterator iterator);

    Pagination setItems(ClickableItem... items);
    Pagination setItemsPerPage(int itemsPerPage);


    class Impl implements Pagination {

        private int currentPage;

        private ClickableItem[] items = new ClickableItem[0];
        private int itemsPerPage = 5;

        @Override
        public ClickableItem[] getPageItems() {
            return Arrays.copyOfRange(items,
                    currentPage * itemsPerPage,
                    (currentPage + 1) * itemsPerPage);
        }

        @Override
        public int getPage() {
            return this.currentPage;
        }

        @Override
        public Pagination page(int page) {
            this.currentPage = page;
            return this;
        }

        @Override
        public boolean isFirst() {
            return this.currentPage == 0;
        }

        @Override
        public boolean isLast() {
            int pageCount = (int) Math.ceil((double) this.items.length / this.itemsPerPage);
            return this.currentPage >= pageCount - 1;
        }

        @Override
        public Pagination first() {
            this.currentPage = 0;
            return this;
        }

        @Override
        public Pagination previous() {
            if(!isFirst())
                this.currentPage--;

            return this;
        }

        @Override
        public Pagination next() {
            if(!isLast())
                this.currentPage++;

            return this;
        }

        @Override
        public Pagination last() {
            this.currentPage = this.items.length / this.itemsPerPage;
            return this;
        }

        @Override
        public Pagination addToIterator(SlotIterator iterator) {
            for(ClickableItem item : getPageItems()) {
                iterator.next().set(item);

                if(iterator.ended())
                    break;
            }

            return this;
        }

        @Override
        public Pagination setItems(ClickableItem... items) {
            this.items = items;
            return this;
        }

        @Override
        public Pagination setItemsPerPage(int itemsPerPage) {
            this.itemsPerPage = itemsPerPage;
            return this;
        }

    }

}

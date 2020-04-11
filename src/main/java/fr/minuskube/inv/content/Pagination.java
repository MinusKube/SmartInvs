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

import fr.minuskube.inv.ClickableItem;

import java.util.Arrays;

/**
 * <p>
 *     Pagination system which lets you switch pages;
 *     easily get items in the given page,
 *     easily manipulate the pages and
 *     check if a page is the first or the last one
 *     ({@link Pagination#isFirst()} / {@link Pagination#isLast()}).
 * </p>
 *
 * <p>
 *     You must start by setting the <b>items</b> and the <b>itemsPerPage</b>,
 *     then you can manipulate the pages by using the
 *     {@link Pagination#page(int)} /
 *     {@link Pagination#first()} /
 *     {@link Pagination#previous()} /
 *     {@link Pagination#next()} /
 *     {@link Pagination#last()}
 *     methods.
 * </p>
 *
 * <p>
 *     Then, when you need to get all the items of the current page,
 *     either use the {@link Pagination#getPageItems()} method, or directly
 *     add the items to your inventory with a SlotIterator and the
 *     method {@link Pagination#addToIterator(SlotIterator)}
 * </p>
 */
public interface Pagination {

    /**
     * Gets the items of the current page.
     * <br>
     * This returns an array of the size of the items per page.
     *
     * @return the current page items
     */
    ClickableItem[] getPageItems();

    /**
     * Gets the current page.
     *
     * @return the current page
     */
    int getPage();

    /**
     * Sets the current page.
     *
     * @param page the current page
     * @return <code>this</code>, for chained calls
     */
    Pagination page(int page);

    /**
     * Checks if the current page is the first page.
     * <br>
     * This is equivalent to: <code>page == 0</code>
     *
     * @return <code>true</code> if this page is the first page
     */
    boolean isFirst();

    /**
     * Checks if the current page is the last page.
     * <br>
     * This is equivalent to: <code>page == itemsCount / itemsPerPage</code>
     *
     * @return <code>true</code> if this page is the last page
     */
    boolean isLast();

    /**
     * Sets the current page to the first page.
     * <br>
     * This is equivalent to: <code>page(0)</code>
     *
     * @return <code>this</code>, for chained calls
     */
    Pagination first();

    /**
     * Sets the current page to the previous page,
     * if the current page is already the first page, this do nothing.
     *
     * @return <code>this</code>, for chained calls
     */
    Pagination previous();

    /**
     * Sets the current page to the next page,
     * if the current page is already the last page, this do nothing.
     *
     * @return <code>this</code>, for chained calls
     */
    Pagination next();

    /**
     * Sets the current page to the last page.
     * <br>
     * This is equivalent to: <code>page(itemsCount / itemsPerPage)</code>
     *
     * @return <code>this</code>, for chained calls
     */
    Pagination last();

    /**
     * Adds all the current page items to the given
     * iterator.
     *
     * @param iterator the iterator
     * @return <code>this</code>, for chained calls
     */
    Pagination addToIterator(SlotIterator iterator);

    /**
     * Sets all the items for this Pagination.
     *
     * @param items the items
     * @return <code>this</code>, for chained calls
     */
    Pagination setItems(ClickableItem... items);

    /**
     * Sets the maximum amount of items per page.
     *
     * @param itemsPerPage the maximum amount of items per page
     * @return <code>this</code>, for chained calls
     */
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

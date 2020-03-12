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

package fr.minuskube.inv;

import com.google.common.base.Preconditions;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import fr.minuskube.inv.internal.InventoryListener;
import fr.minuskube.inv.opener.InventoryOpener;
import java.util.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

@SuppressWarnings("unchecked")
public final class SmartInventory {

    private final InventoryManager manager;

    private final String id;

    private final String title;

    private final InventoryType type;

    private final int rows;

    private final int columns;

    private final long updateFrequency;

    private final InventoryProvider provider;

    private final SmartInventory parent;

    private final List<InventoryListener<? extends Event>> listeners;

    private boolean closeable;

    public SmartInventory(final InventoryManager manager, final String id,
                          final String title, final InventoryType type, final int rows,
                          final int columns, final boolean closeable, final long updateFrequency,
                          final InventoryProvider provider, final SmartInventory parent,
                          final List<InventoryListener<? extends Event>> listeners) {
        this.manager = manager;
        this.id = id;
        this.title = title;
        this.type = type;
        this.rows = rows;
        this.columns = columns;
        this.closeable = closeable;
        this.updateFrequency = updateFrequency;
        this.provider = provider;
        this.parent = parent;
        this.listeners = listeners;
    }

    public static SmartInventory.Builder builder() {
        return new SmartInventory.Builder();
    }

    public Inventory open(final Player player) {
        return this.open(player, 0, Collections.emptyMap());
    }

    public Inventory open(final Player player, final int page, final Map<String, Object> properties) {
        final Optional<SmartInventory> oldInv = this.manager.getInventory(player);
        oldInv.ifPresent(inv -> {
            inv.getListeners().stream()
                .filter(listener -> listener.getType().equals(InventoryCloseEvent.class))
                .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener)
                    .accept(new InventoryCloseEvent(player.getOpenInventory())));

            this.manager.setInventory(player, null);
        });
        final InventoryContents contents = new InventoryContents.Impl(this, player);
        contents.pagination().page(page);
        properties.forEach(contents::setProperty);
        this.manager.setContents(player, contents);
        this.provider.init(player, contents);
        final InventoryOpener opener = this.manager.findOpener(this.type)
            .orElseThrow(() ->
                new IllegalStateException("No opener found for the inventory type " + this.type.name())
            );
        final Inventory handle = opener.open(this, player);
        this.manager.setInventory(player, this);
        this.manager.scheduleUpdateTask(player, this);
        return handle;
    }

    List<InventoryListener<? extends Event>> getListeners() {
        return Collections.unmodifiableList(this.listeners);
    }

    public Inventory open(final Player player, final int page) {
        return this.open(player, page, Collections.emptyMap());
    }

    public Inventory open(final Player player, final Map<String, Object> properties) {
        return this.open(player, 0, properties);
    }

    public void close(final Player player) {
        this.listeners.stream()
            .filter(listener -> listener.getType().equals(InventoryCloseEvent.class))
            .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener)
                .accept(new InventoryCloseEvent(player.getOpenInventory())));

        this.manager.setInventory(player, null);
        player.closeInventory();

        this.manager.setContents(player, null);
        this.manager.cancelUpdateTask(player);
    }

    /**
     * Checks if this inventory has a slot at the specified position
     *
     * @param row Slot row (starts at 0)
     * @param col Slot column (starts at 0)
     */
    public boolean checkBounds(final int row, final int col) {
        if (row < 0 || col < 0) {
            return false;
        }
        return row < this.rows && col < this.columns;
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public InventoryType getType() {
        return this.type;
    }

    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }

    public boolean isCloseable() {
        return this.closeable;
    }

    public void setCloseable(final boolean closeable) {
        this.closeable = closeable;
    }

    public long getUpdateFrequency() {
        return this.updateFrequency;
    }

    public InventoryProvider getProvider() {
        return this.provider;
    }

    public Optional<SmartInventory> getParent() {
        return Optional.ofNullable(this.parent);
    }

    public InventoryManager getManager() {
        return this.manager;
    }

    public static final class Builder {

        private final List<InventoryListener<? extends Event>> listeners = new ArrayList<>();

        private String id = "unknown";

        private String title = "";

        private InventoryType type = InventoryType.CHEST;

        private Optional<Integer> rows = Optional.empty();

        private Optional<Integer> columns = Optional.empty();

        private boolean closeable = true;

        private long updateFrequency = 1L;

        private InventoryManager manager;

        private InventoryProvider provider;

        private SmartInventory parent;

        /**
         * Private ctor.
         */
        private Builder() {
        }

        public SmartInventory.Builder id(final String id) {
            this.id = id;
            return this;
        }

        public SmartInventory.Builder title(final String title) {
            this.title = title;
            return this;
        }

        public SmartInventory.Builder type(final InventoryType type) {
            this.type = type;
            return this;
        }

        public SmartInventory.Builder size(final int rows, final int columns) {
            this.rows = Optional.of(rows);
            this.columns = Optional.of(columns);
            return this;
        }

        public SmartInventory.Builder closeable(final boolean closeable) {
            this.closeable = closeable;
            return this;
        }

        /**
         * This method is used to configure the frequency at which the {@link InventoryProvider#update(Player, InventoryContents)}
         * method is called. Defaults to 1
         *
         * @param frequency The inventory update frequency, in ticks
         * @throws IllegalArgumentException If frequency is smaller than 1.
         */
        public SmartInventory.Builder updateFrequency(final long frequency) {
            Preconditions.checkArgument(frequency > 0L, "frequency must be > 0");
            this.updateFrequency = frequency;
            return this;
        }

        public SmartInventory.Builder provider(final InventoryProvider provider) {
            this.provider = provider;
            return this;
        }

        public SmartInventory.Builder parent(final SmartInventory parent) {
            this.parent = parent;
            return this;
        }

        public SmartInventory.Builder listener(final InventoryListener<? extends Event> listener) {
            this.listeners.add(listener);
            return this;
        }

        public SmartInventory.Builder manager(final InventoryManager manager) {
            this.manager = manager;
            return this;
        }

        public String getId() {
            return this.id;
        }

        public String getTitle() {
            return this.title;
        }

        public InventoryType getType() {
            return this.type;
        }

        public Optional<Integer> getRows() {
            return this.rows;
        }

        public Optional<Integer> getColumns() {
            return this.columns;
        }

        public boolean isCloseable() {
            return this.closeable;
        }

        public long getUpdateFrequency() {
            return this.updateFrequency;
        }

        public Optional<InventoryProvider> getProvider() {
            return Optional.ofNullable(this.provider);
        }

        public Optional<SmartInventory> getParent() {
            return Optional.ofNullable(this.parent);
        }

        public List<InventoryListener<? extends Event>> getListeners() {
            return Collections.unmodifiableList(this.listeners);
        }

        public SmartInventory build() {
            if (this.provider == null) {
                throw new IllegalStateException("The provider of the SmartInventory.Builder must be set.");
            }
            if (this.manager == null) {
                this.manager = SmartInvsPlugin.getManager();
            }
            return new SmartInventory(
                this.manager,
                this.id,
                this.title,
                this.type,
                this.rows.orElseGet(() -> this.getDefaultDimensions(this.type).getRow()),
                this.columns.orElseGet(() -> this.getDefaultDimensions(this.type).getColumn()),
                this.closeable,
                this.updateFrequency,
                this.provider,
                this.parent,
                this.listeners
            );
        }

        private SlotPos getDefaultDimensions(final InventoryType type) {
            return this.getManager().orElseThrow(() ->
                new IllegalStateException("Cannot find InventoryManager for type " + type)
            ).findOpener(type).orElseThrow(() ->
                new IllegalStateException("Cannot find InventoryOpener for type " + type)
            ).defaultSize(type);
        }

        public Optional<InventoryManager> getManager() {
            return Optional.ofNullable(this.manager);
        }

    }

}

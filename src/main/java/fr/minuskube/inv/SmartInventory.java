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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public final class SmartInventory {

    @NotNull
    private final InventoryManager manager;

    @NotNull
    private final String id;

    @NotNull
    private final String title;

    @NotNull
    private final InventoryType type;

    private final int rows;

    private final int columns;

    private final long updateFrequency;

    @NotNull
    private final InventoryProvider provider;

    @Nullable
    private final SmartInventory parent;

    @NotNull
    private final List<InventoryListener<? extends Event>> listeners;

    private boolean closeable;

    public SmartInventory(@NotNull final InventoryManager manager, @NotNull final String id,
                          @NotNull final String title, @NotNull final InventoryType type, final int rows,
                          final int columns, final boolean closeable, final long updateFrequency,
                          @NotNull final InventoryProvider provider, @Nullable final SmartInventory parent,
                          @NotNull final List<InventoryListener<? extends Event>> listeners) {
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

    @NotNull
    public static SmartInventory.Builder builder() {
        return new SmartInventory.Builder();
    }

    @NotNull
    public Inventory open(@NotNull final Player player) {
        return this.open(player, 0, Collections.emptyMap());
    }

    @NotNull
    public Inventory open(@NotNull final Player player, final int page, @NotNull final Map<String, Object> properties) {
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

    @NotNull
    public Inventory open(@NotNull final Player player, final int page) {
        return this.open(player, page, Collections.emptyMap());
    }

    @NotNull
    public Inventory open(@NotNull final Player player, @NotNull final Map<String, Object> properties) {
        return this.open(player, 0, properties);
    }

    public void close(@NotNull final Player player) {
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

        @NotNull
        private String id = "unknown";

        @NotNull
        private String title = "";

        @NotNull
        private InventoryType type = InventoryType.CHEST;

        @NotNull
        private Optional<Integer> rows = Optional.empty();

        @NotNull
        private Optional<Integer> columns = Optional.empty();

        private boolean closeable = true;

        private long updateFrequency = 1L;

        @Nullable
        private InventoryManager manager;

        @Nullable
        private InventoryProvider provider;

        @Nullable
        private SmartInventory parent;

        /**
         * Private ctor.
         */
        private Builder() {
        }

        @NotNull
        public SmartInventory.Builder id(@NotNull final String id) {
            this.id = id;
            return this;
        }

        @NotNull
        public SmartInventory.Builder title(@NotNull final String title) {
            this.title = title;
            return this;
        }

        @NotNull
        public SmartInventory.Builder type(@NotNull final InventoryType type) {
            this.type = type;
            return this;
        }

        @NotNull
        public SmartInventory.Builder size(final int rows, final int columns) {
            this.rows = Optional.of(rows);
            this.columns = Optional.of(columns);
            return this;
        }

        @NotNull
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
        @NotNull
        public SmartInventory.Builder updateFrequency(final long frequency) {
            Preconditions.checkArgument(frequency > 0L, "frequency must be > 0");
            this.updateFrequency = frequency;
            return this;
        }

        @NotNull
        public SmartInventory.Builder provider(@NotNull final InventoryProvider provider) {
            this.provider = provider;
            return this;
        }

        @NotNull
        public SmartInventory.Builder parent(@NotNull final SmartInventory parent) {
            this.parent = parent;
            return this;
        }

        @NotNull
        public SmartInventory.Builder listener(@NotNull final InventoryListener<? extends Event> listener) {
            this.listeners.add(listener);
            return this;
        }

        @NotNull
        public SmartInventory.Builder manager(@NotNull final InventoryManager manager) {
            this.manager = manager;
            return this;
        }

        @NotNull
        public String getId() {
            return this.id;
        }

        @NotNull
        public String getTitle() {
            return this.title;
        }

        @NotNull
        public InventoryType getType() {
            return this.type;
        }

        @NotNull
        public Optional<Integer> getRows() {
            return this.rows;
        }

        @NotNull
        public Optional<Integer> getColumns() {
            return this.columns;
        }

        public boolean isCloseable() {
            return this.closeable;
        }

        public long getUpdateFrequency() {
            return this.updateFrequency;
        }

        @NotNull
        public Optional<InventoryProvider> getProvider() {
            return Optional.ofNullable(this.provider);
        }

        @NotNull
        public Optional<SmartInventory> getParent() {
            return Optional.ofNullable(this.parent);
        }

        @NotNull
        public List<InventoryListener<? extends Event>> getListeners() {
            return Collections.unmodifiableList(this.listeners);
        }

        @NotNull
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

        @NotNull
        private SlotPos getDefaultDimensions(@NotNull final InventoryType type) {
            return this.getManager().orElseThrow(() ->
                new IllegalStateException("Cannot find InventoryManager for type " + type)
            ).findOpener(type).orElseThrow(() ->
                new IllegalStateException("Cannot find InventoryOpener for type " + type)
            ).defaultSize(type);
        }

        @NotNull
        public Optional<InventoryManager> getManager() {
            return Optional.ofNullable(this.manager);
        }

    }

}

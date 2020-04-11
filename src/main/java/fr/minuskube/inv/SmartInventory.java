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

import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import fr.minuskube.inv.opener.InventoryOpener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

@SuppressWarnings("unchecked")
public class SmartInventory {

    private String id;
    private String title;
    private InventoryType type;
    private int rows, columns;
    private boolean closeable;
    private int updateFrequency;

    private InventoryProvider provider;
    private SmartInventory parent;

    private List<InventoryListener<? extends Event>> listeners;
    private final InventoryManager manager;

    private SmartInventory(InventoryManager manager) {
        this.manager = manager;
    }

    public Inventory open(Player player) {	
        return open(player, 0, Collections.EMPTY_MAP);	
    }	
    
    public Inventory open(Player player, int page) {	
        return open(player, page, Collections.EMPTY_MAP);	
    }	
    
    public Inventory open(Player player, Map<String, Object> properties) {	
        return open(player, 0, properties);	
    }	

    public Inventory open(Player player, int page, Map<String, Object> properties) {	
        Optional<SmartInventory> oldInv = this.manager.getInventory(player);

        oldInv.ifPresent(inv -> {
            inv.getListeners().stream()
                    .filter(listener -> listener.getType() == InventoryCloseEvent.class)
                    .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener)
                            .accept(new InventoryCloseEvent(player.getOpenInventory())));

            this.manager.setInventory(player, null);
        });

        InventoryContents contents = new InventoryContents.Impl(this, player);
        contents.pagination().page(page);
        properties.forEach(contents::setProperty);
        
        this.manager.setContents(player, contents);
        this.provider.init(player, contents);

        InventoryOpener opener = this.manager.findOpener(type)
                .orElseThrow(() -> new IllegalStateException("No opener found for the inventory type " + type.name()));
        Inventory handle = opener.open(this, player);

        this.manager.setInventory(player, this);
        this.manager.scheduleUpdateTask(player, this);
        
        return handle;
    }

    public void close(Player player) {
        listeners.stream()
                .filter(listener -> listener.getType() == InventoryCloseEvent.class)
                .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener)
                        .accept(new InventoryCloseEvent(player.getOpenInventory())));

        this.manager.setInventory(player, null);
        player.closeInventory();

        this.manager.setContents(player, null);
        this.manager.cancelUpdateTask(player);
    }
    /**
     * Checks if this inventory has a slot at the specified position
     * @param row Slot row (starts at 0)
     * @param col Slot column (starts at 0)
     */
    public boolean checkBounds(int row, int col) {
        if(row < 0 || col < 0)
            return false;
        return row < this.rows && col < this.columns;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public InventoryType getType() { return type; }
    public int getRows() { return rows; }
    public int getColumns() { return columns; }

    public boolean isCloseable() { return closeable; }
    public void setCloseable(boolean closeable) { this.closeable = closeable; }
    
    public int getUpdateFrequency() { return updateFrequency; }

    public InventoryProvider getProvider() { return provider; }
    public Optional<SmartInventory> getParent() { return Optional.ofNullable(parent); }

    public InventoryManager getManager() { return manager; }

    List<InventoryListener<? extends Event>> getListeners() { return listeners; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {

        private String id = "unknown";
        private String title = "";
        private InventoryType type = InventoryType.CHEST;
        private Optional<Integer> rows = Optional.empty();
        private Optional<Integer> columns = Optional.empty();
        private boolean closeable = true;
        private int updateFrequency = 1;

        private InventoryManager manager;
        private InventoryProvider provider;
        private SmartInventory parent;

        private List<InventoryListener<? extends Event>> listeners = new ArrayList<>();

        private Builder() {}

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder type(InventoryType type) {
            this.type = type;
            return this;
        }

        public Builder size(int rows, int columns) {
            this.rows = Optional.of(rows);
            this.columns = Optional.of(columns);
            return this;
        }

        public Builder closeable(boolean closeable) {
            this.closeable = closeable;
            return this;
        }
        
        /**
         * This method is used to configure the frequency at which the {@link InventoryProvider#update(Player, InventoryContents)}
         * method is called. Defaults to 1
         * @param frequency The inventory update frequency, in ticks
         * @throws IllegalArgumentException If frequency is smaller than 1.
         */
        public Builder updateFrequency(int frequency) {
        	Preconditions.checkArgument(frequency > 0, "frequency must be > 0");
        	this.updateFrequency = frequency;
        	return this;
        }

        public Builder provider(InventoryProvider provider) {
            this.provider = provider;
            return this;
        }

        public Builder parent(SmartInventory parent) {
            this.parent = parent;
            return this;
        }

        public Builder listener(InventoryListener<? extends Event> listener) {
            this.listeners.add(listener);
            return this;
        }

        public Builder manager(InventoryManager manager) {
            this.manager = manager;
            return this;
        }
        
        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public InventoryType getType() {
            return type;
        }

        public Optional<Integer> getRows() {
            return rows;
        }

        public Optional<Integer> getColumns() {
            return columns;
        }

        public boolean isCloseable() {
            return closeable;
        }

        public int getUpdateFrequency() {
            return updateFrequency;
        }

        public InventoryManager getManager() {
            return manager;
        }

        public InventoryProvider getProvider() {
            return provider;
        }

        public SmartInventory getParent() {
            return parent;
        }

        public List<InventoryListener<? extends Event>> getListeners() {
            return Collections.unmodifiableList(listeners);
        }

        public SmartInventory build() {
            if(this.provider == null)
                throw new IllegalStateException("The provider of the SmartInventory.Builder must be set.");

            if(this.manager == null) {          // if it's null, use the default instance
                this.manager = SmartInvsPlugin.manager();   
                if(this.manager == null) {      // if it's still null, throw an exception
                    throw new IllegalStateException("Manager of the SmartInventory.Builder must be set, or SmartInvs should be loaded as a plugin.");
                }
            }

            SmartInventory inv = new SmartInventory(manager);
            inv.id = this.id;
            inv.title = this.title;
            inv.type = this.type;
            inv.rows = this.rows.orElseGet(() -> getDefaultDimensions(type).getRow());
            inv.columns = this.columns.orElseGet(() -> getDefaultDimensions(type).getColumn());
            inv.closeable = this.closeable;
            inv.updateFrequency = this.updateFrequency;
            inv.provider = this.provider;
            inv.parent = this.parent;
            inv.listeners = this.listeners;
            return inv;
        }

        private SlotPos getDefaultDimensions(InventoryType type) {
            InventoryOpener opener = this.manager.findOpener(type).orElse(null);
            if(opener == null)
                throw new IllegalStateException("Cannot find InventoryOpener for type " + type);
            
            SlotPos size = opener.defaultSize(type);
            if(size == null)
                throw new IllegalStateException(String.format("%s returned null for input InventoryType %s", opener.getClass().getSimpleName(), type));
            
            return size;
        }

    }

}

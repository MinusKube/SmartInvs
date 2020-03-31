package fr.minuskube.inv;

import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.opener.InventoryOpener;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Base event for creating SmartInvs.
 */
@SuppressWarnings("unchecked")
public class SmartInventory {
    /**
     * ID string. The field is the unique id for the custom inventory.
     */
    private String id;
    /**
     * Title String. The field is the title for the custom inventory.
     */
    private String title;
    /**
     * Inventory type. The field is the inventory type. The inventory type
     * may be changed to any of the following:
     * <ul>
     *       <li>Chest</li>
     *       <li>Dispenser</li>
     *      <li>Dropper</li>
     *      <li>Furnace</li>
     *      <li>Workbench</li>
     *      <li>Crafting</li>
     *      <li>Enchanting</li>
     *      <li>Brewing</li>
     *      <li>Player</li>
     *      <li>creative</li>
     *      <li>Merchant</li>
     *      <li>Ender Chest</li>
     *      <li>Anvil</li>
     *      <li>Beacon</li>
     *      <li>Hopper</li>
     *  </ul>
     */
    private InventoryType type;
    /**
     * Rows &amp; Columns Integer. This field has default values for both rows (6) &amp; columns (9).
     */
    private int rows, columns;
    private boolean closeable;
    /**
     * Inventory close boolean. This field being a boolean, is the key to allowing the inventory to
     * be closed or not.
     */

    /**
     * Provider Inventory. This field is the provider interface for getting contents inside the SmartInvs.
     */
    private InventoryProvider provider;
    /**
     * Parent smart inventory. This field is the key to making the inventory a parent
     */
    private SmartInventory parent;

    /**
     * Listener array. This field being an array is the key to the inventory listeners which also extends
     * the {@link Event} class and will lost all inventory listeners which ave been created.
     */
    private List<InventoryListener<? extends Event>> listeners;
    /**
     * Inventory Manager. This field is the core component for managing SmartInvs inventories.
     */
    private InventoryManager manager;

    /**
     * This default constructor will initialize the manager property. This constructor
     * will try to find an Inventory Opener that supports the {@link InventoryType} of
     * of the inventory. If no opener is found, the inventory will not be opened, and
     * an exception will be thrown.
     *
     * @param manager Throw exception is no supported inventory type found
     */
    private SmartInventory(InventoryManager manager) {
        this.manager = manager;
    }

    /**
     * Opens up the given inventory on the first page.
     *
     * @param player Gets the player opening up the SmartInvs
     * @return the opened SmartInvs inventory
     */
    public Inventory open(Player player) { return open(player, 0); }

    /**
     * Opens up the given inventory with the inputted page number.
     *
     * @param player Gets the player opening up the SmartInvs
     * @param page Open inventory to inputted page number
     * @return SmartInvs inventory to the given page number
     */
    public Inventory open(Player player, int page) {
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

        this.manager.setContents(player, contents);
        this.provider.init(player, contents);

        InventoryOpener opener = this.manager.findOpener(type)
                .orElseThrow(() -> new IllegalStateException("No opener found for the inventory type " + type.name()));
        Inventory handle = opener.open(this, player);

        this.manager.setInventory(player, this);

        return handle;
    }

    /**
     * Close the SmartInvs for the given player.
     *
     * @param player Gets the player looking at the SmartInvs
     */
    @SuppressWarnings("unchecked")
    public void close(Player player) {
        listeners.stream()
                .filter(listener -> listener.getType() == InventoryCloseEvent.class)
                .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener)
                        .accept(new InventoryCloseEvent(player.getOpenInventory())));

        this.manager.setInventory(player, null);
        player.closeInventory();

        this.manager.setContents(player, null);
    }

    /**
     * Gets the custom id for the given SmartInvs.
     *
     * @return custom SmartInvs id
     */
    public String getId() { return id; }

    /**
     * Gets the SmartInvs custom title.
     *
     * @return SmartInvs custom title
     */
    public String getTitle() { return title; }

    /**
     * Gets the SmartInvs inventory type.
     *
     * @return SmartInvs inventory type
     */
    public InventoryType getType() { return type; }

    /**
     * Gets the number of rows for the given SmartInvs.
     *
     * @return number of rows
     */
    public int getRows() { return rows; }

    /**
     * Gets the number of columns for the given SmartInvs.
     *
     * @return number columns
     */
    public int getColumns() { return columns; }

    /**
     * Will return a boolean value if the given SmartInvs is closable.
     *
     * @return true if closable, false if not closable
     */
    public boolean isCloseable() { return closeable; }

    /**
     * Set whether the SmartInvs is closable or not. If you set the setCloseable() to true, then the SmartInvs
     * will return true for {@link #isCloseable()}. If you set the setCloseable() to false, then the SmartInvs will return
     * false for {@link #isCloseable()}.
     *
     * @param closeable Enable or Disable SmartInvs closeable feature
     */
    public void setCloseable(boolean closeable) { this.closeable = closeable; }

    /**
     * Will return the custom SmartInvs with all of its contents for the given player.
     *
     * @return custom SmartInvs
     */
    public InventoryProvider getProvider() { return provider; }

    /**
     * Gets the parent of a Smart Inventory.
     *
     * @return smart inventory parent
     */
    public Optional<SmartInventory> getParent() { return Optional.ofNullable(parent); }

    /**
     * Get Inventory Manager and manage the operations of a SmartInvs.
     *
     * @return inventory manager property
     */
    public InventoryManager getManager() { return manager; }

    /**
     * Any custom listeners that is associated with {@link InventoryListener} will be instantiated
     * as a List. This List also extends the {@link Event} class to require the new listener return
     * a {@link org.bukkit.event.HandlerList}
     *
     * @return handlerList as listener
     */
    List<InventoryListener<? extends Event>> getListeners() { return listeners; }

    /**
     * Used for creating a custom SmartInvs inventory. This builder method has a predefined inventory
     * type (chest), row amount (6) and column amount (9). By the inventory is closeable.
     *
     * @return chest inventory with custom data.
     */
    public static Builder builder() { return new Builder(); }

    /**
     * A static builder for creating a custom chest inventory.
     */
    public static final class Builder {
        /**
         * ID string. This field is the inventory's unique id.
         */
        private String id = "unknown";
        /**
         * Title string. This field is the inventory's title.
         */
        private String title = "";
        /**
         * Inventory Type. This field has a default inventory type of chest. The inventory type
         * may be changed to any of the following:
         * <ul>
         *     <li>Chest</li>
         *     <li>Dispenser</li>
         *     <li>Dropper</li>
         *     <li>Furnace</li>
         *     <li>Workbench</li>
         *     <li>Crafting</li>
         *     <li>Enchanting</li>
         *     <li>Brewing</li>
         *     <li>Player</li>
         *     <li>creative</li>
         *     <li>Merchant</li>
         *     <li>Ender Chest</li>
         *     <li>Anvil</li>
         *     <li>Beacon</li>
         *     <li>Hopper</li>
         * </ul>
         */
        private InventoryType type = InventoryType.CHEST;
        /**
         * Rows &amp; Columns Integer. This field has default values for both rows (6) &amp; columns (9).
         */
        private int rows = 6, columns = 9;
        /**
         * Inventory close boolean. This field being a boolean, is the key to allowing the inventory to
         * be closed or not.
         */
        private boolean closeable = true;
        /**
         * Inventory Manager. This field is the core component for managing SmartInvs inventories.
         */
        private InventoryManager manager;
        /**
         * Provider Inventory. This field is the provider interface for getting contents inside the SmartInvs.
         */
        private InventoryProvider provider;
        /**
         * Parent SmartInventory. This field uses the base event for SmartInvs.
         */
        private SmartInventory parent;
        /**
         * Listener array. This field being an array is the key to the inventory listeners which also extends
         * the {@link Event} class.
         */
        private List<InventoryListener<? extends Event>> listeners = new ArrayList<>();

        /**
         * The default constructor is defined for cleaner code.
         */
        private Builder() {}

        /**
         * Set a specific SmartInvs unique id.
         *
         * @param id set SmartInvs id
         * @return the unique set id
         */
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        /**
         * Set a specific SmartInvs title.
         *
         * @param title set SmartInvs title
         * @return the unique set title
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Set a specific inventory type.
         *
         * @param type set inventory type
         * @return the type of inventory
         */
        public Builder type(InventoryType type) {
            this.type = type;
            return this;
        }

        /**
         * Set the amount of rows and set the amount of columns in a SmartInvs.
         *
         * @param rows set row amount
         * @param columns set column amount
         * @return the row amount and column amount
         */
        public Builder size(int rows, int columns) {
            this.rows = rows;
            this.columns = columns;
            return this;
        }

        /**
         * Set whether the inventory is closeable or not.
         *
         * @param closeable false indicates that the inventory is not closeable, true by default
         *                  from closeable field indicates that the inventory is closeable
         * @return the boolean state for closeable inventory
         */
        public Builder closeable(boolean closeable) {
            this.closeable = closeable;
            return this;
        }

        /**
         * Method to get all the contents from inside the SmartInvs inventory.
         *
         * @param provider update inventory with all contents inside
         * @return all contents from inventory
         */
        public Builder provider(InventoryProvider provider) {
            this.provider = provider;
            return this;
        }

        /**
         * Provides an association to the parent inventory for the children inventories. This method will
         * allow you to create a hierarchy of inventories.
         *
         * @param parent get parent inventory
         * @return the inventory associated with the parent inventory
         */
        public Builder parent(SmartInventory parent) {
            this.parent = parent;
            return this;
        }

        /**
         * Listens for custom events. This method extends {@link Event} with the generics lower bounded wildcard.
         *
         * @param listener listen for events within SmartInvs
         * @return the listener event
         */
        public Builder listener(InventoryListener<? extends Event> listener) {
            this.listeners.add(listener);
            return this;
        }

        /**
         * Manages the custom inventory. This method handles all the actions that can be performs within
         * a custom inventory using SmartInvs.
         *
         * @param manager manage the custom actions of an inventory
         * @return the action for the inventory
         */
        public Builder manager(InventoryManager manager) {
            this.manager = manager;
            return this;
        }

        /**
         * Builds the custom SmartInvs inventory.
         *
         * @return the custom inventory with its prerequisites
         */
        public SmartInventory build() {
            if(this.provider == null)
                throw new IllegalStateException("The provider of the SmartInventory.Builder must be set.");

            InventoryManager manager = this.manager != null ? this.manager : SmartInvsPlugin.manager();

            if(manager == null)
                throw new IllegalStateException("The manager of the SmartInventory.Builder must be set, "
                        + "or the SmartInvs should be loaded as a plugin.");

            SmartInventory inv = new SmartInventory(manager);
            inv.id = this.id;
            inv.title = this.title;
            inv.type = this.type;
            inv.rows = this.rows;
            inv.columns = this.columns;
            inv.closeable = this.closeable;
            inv.provider = this.provider;
            inv.parent = this.parent;
            inv.listeners = this.listeners;

            return inv;
        }
    }

}
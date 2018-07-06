package fr.minuskube.inv;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public class ItemClickData {

    private Event event;
    private Player player;
    private ItemStack item;
    private int slot;

    public ItemClickData(Event event, Player player, ItemStack item, int slot) {
        this.event = event;
        this.player = player;
        this.item = item;
        this.slot = slot;
    }

    public Event getEvent() { return event; }
    public Player getPlayer() { return player; }
    public ItemStack getItem() { return item; }
    public int getSlot() { return slot; }

}

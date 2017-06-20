package fr.minuskube.inv.content;

public abstract class InventoryProvider {

    protected Pagination pagination = new Pagination.Impl();

    public abstract void init(InventoryContents contents);
    public abstract void update(InventoryContents contents);

}

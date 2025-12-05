package model;

public class Item extends Resource<Item> {
    private static final long serialVersionUID = 1L;
    private int quantity;
    private String addedByUserId;
    private String addedByUserName; 


    public Item(String id, String name, int qty, String addedByUserId, String addedByUserName) {
        super(id, name, null);
        this.quantity = qty;
        this.addedByUserId = addedByUserId;
        this.addedByUserName = addedByUserName;
    }


    public int getQuantity() { return quantity; }
    public void setQuantity(int q) { this.quantity = q; }
    public String getAddedByUserId() { return addedByUserId; }
    public String getAddedByUserName() { return addedByUserName; }


    @Override
    public String brief() {
        return String.format("%s (qty=%d)", name, quantity);
    }

    @Override
    public String toString() {
        return String.format("Item[%s|%s|qty=%d|added by %s(%s)]",
            id, name, quantity, addedByUserName, addedByUserId);
    }

}

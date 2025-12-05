package model;

public class Item extends Resource<Item> {
    private static final long serialVersionUID = 1L;
    private int quantity;

    public Item(String id, String name, int qty) {
        super(id, name, null);
        this.quantity = qty;
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int q) { this.quantity = q; }

    @Override
    public String brief() {
        return String.format("%s (qty=%d)", name, quantity);
    }

    @Override
    public String toString() {
        return String.format("Item[%s|%s|qty=%d]", id, name, quantity);
    }
}

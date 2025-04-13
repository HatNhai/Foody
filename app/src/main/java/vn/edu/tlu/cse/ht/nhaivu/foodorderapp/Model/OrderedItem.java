package vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model;

public class OrderedItem {
    public String id;
    public String name;
    public int quantity;

    public OrderedItem() {}

    public OrderedItem(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

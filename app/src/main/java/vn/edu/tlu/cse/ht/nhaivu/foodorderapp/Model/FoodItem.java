package vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model;

import java.io.Serializable;
public class FoodItem implements Serializable {
    private String id;
    private String name;
    private String description;
    private String image;
    private int price;private int quantity = 1;



    public FoodItem() {
        // Constructor rỗng cho Firebase
    }

    public FoodItem(String id, String name, String description, String image, int price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
    }

    // Getter & Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

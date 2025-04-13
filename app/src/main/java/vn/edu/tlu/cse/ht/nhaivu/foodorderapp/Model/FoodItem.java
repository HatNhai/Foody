package vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model;

import java.io.Serializable;
public class FoodItem implements Serializable {
    private String id;
    private String name;
    private String description;
    private String image;
    private int price;private int quantity = 1;
    private String category;
    public boolean is_favorite;
    private float avgRating;     // ⭐ trung bình đánh giá
    private int reviewCount;     // 📊 số lượng đánh giá



    public FoodItem() {
        // Constructor rỗng cho Firebase
    }

    public FoodItem(String id, String name, String description, String image, int price,String category,boolean is_favorite) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.category = category;
        this.is_favorite = is_favorite;
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
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public boolean is_favorite() { return is_favorite;}

    public void setIs_favorite(boolean is_favorite) { this.is_favorite = is_favorite;}
    public float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
}

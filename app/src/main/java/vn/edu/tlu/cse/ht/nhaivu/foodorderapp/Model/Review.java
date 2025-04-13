package vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model;

public class Review {
    public float rating;
    public String comment;
    public long timestamp;

    public Review() {}

    public Review(float rating, String comment, long timestamp) {
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }
}
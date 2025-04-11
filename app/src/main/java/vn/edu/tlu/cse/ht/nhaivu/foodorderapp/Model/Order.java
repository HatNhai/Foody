package vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model;

import java.util.Map;
public class Order {
    public String name;
    public String address;
    public String phone;
    public long totalAmount;
    public String status;
    public Map<String, OrderedItem> items;
    public String uid; // 👈 Thêm dòng này

    public Order() {}

    public Order(String name, String address, String phone, long totalAmount, String status, Map<String, OrderedItem> items, String uid) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.totalAmount = totalAmount;
        this.status = status;
        this.items = items;
        this.uid = uid;
    }
}



package vn.edu.tlu.cse.ht.nhaivu.foodorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Adapter.CartAdapter;
import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model.FoodItem;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private ArrayList<FoodItem> cartList;
    private DatabaseReference cartRef;
    private Button btnCompleteOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartList);
        recyclerView.setAdapter(cartAdapter);

        btnCompleteOrder = findViewById(R.id.btnCompleteOrder);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userId);

        loadCartItems();

        // Xử lý khi nhấn nút "Đặt hàng"
        btnCompleteOrder.setOnClickListener(v -> {
            if (cartList.isEmpty()) {
                Toast.makeText(CartActivity.this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
            } else {
                // (Tùy chọn) truyền tổng tiền sang CheckoutActivity
                int total = calculateTotal();
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putExtra("totalAmount", total);
                startActivity(intent);
            }
        });
    }

    private void loadCartItems() {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartList.clear();
                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    FoodItem item = itemSnap.getValue(FoodItem.class);
                    if (item != null) {
                        cartList.add(item);
                    }
                }
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Lỗi khi tải giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Tính tổng số tiền giỏ hàng
    private int calculateTotal() {
        int total = 0;
        for (FoodItem item : cartList) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }
}

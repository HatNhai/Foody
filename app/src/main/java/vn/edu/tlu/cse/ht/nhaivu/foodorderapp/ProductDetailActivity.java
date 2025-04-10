package vn.edu.tlu.cse.ht.nhaivu.foodorderapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model.FoodItem;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView ivProductImage;
    private TextView tvProductName, tvProductPrice, tvProductDescription;
    private Button btnAddToCart;

    private FoodItem foodItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail); // đảm bảo file XML này đã được tạo

        // Ánh xạ view
        ivProductImage = findViewById(R.id.imageProduct);
        tvProductName = findViewById(R.id.tvName);
        tvProductPrice = findViewById(R.id.tvPrice);
        tvProductDescription = findViewById(R.id.tvDescription);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        // Nhận dữ liệu từ intent
        foodItem = (FoodItem) getIntent().getSerializableExtra("food");

        if (foodItem != null) {
            // Hiển thị thông tin
            Glide.with(this).load(foodItem.getImage()).into(ivProductImage);
            tvProductName.setText(foodItem.getName());
            tvProductPrice.setText(String.format("₫%s", foodItem.getPrice()));
            tvProductDescription.setText(foodItem.getDescription());
        }

        // Xử lý nút "Thêm vào giỏ hàng"
        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void addToCart() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userId);

        String itemId = foodItem.getId();
        cartRef.child(itemId).setValue(foodItem).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

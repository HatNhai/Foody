package vn.edu.tlu.cse.ht.nhaivu.foodorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
    private ImageButton btnFavorite;
    private boolean isFavorite = false;

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
        btnFavorite = findViewById(R.id.btnFavorite);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
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
        loadFavoriteStatus();
        btnFavorite.setOnClickListener(v -> onFavoriteClicked(v));
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
    private void loadFavoriteStatus() {
        if (foodItem == null || foodItem.getId() == null) {
            Toast.makeText(this, "Không thể tải trạng thái yêu thích!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String itemId = foodItem.getId();
        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("Favorites");

        favRef.child(userId).child(itemId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Boolean favoriteStatus = task.getResult().getValue(Boolean.class);
                isFavorite = favoriteStatus != null && favoriteStatus;
                foodItem.setIs_favorite(isFavorite);
                updateFavoriteButton();
            } else {
                Toast.makeText(this, "Lỗi khi tải trạng thái yêu thích", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Thay đổi trạng thái yêu thích khi nhấn nút yêu thích
    public void onFavoriteClicked(View view) {
        isFavorite = !isFavorite;
        foodItem.setIs_favorite(isFavorite);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String itemId = foodItem.getId();
        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("Favorites");

        favRef.child(userId).child(itemId).setValue(isFavorite).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateFavoriteButton();
                Toast.makeText(this, "Cập nhật yêu thích thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Cập nhật yêu thích thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateFavoriteButton() {
        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_selected); // Biểu tượng yêu thích
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_unselected); // Biểu tượng chưa yêu thích
        }
    }

}

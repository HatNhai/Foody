package vn.edu.tlu.cse.ht.nhaivu.foodorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Adapter.FavoriteAdapter;
import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Adapter.FoodAdapter;
import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model.FoodItem;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteAdapter adapter;
    private List<FoodItem> favoriteList = new ArrayList<>();

    private DatabaseReference favRef, menuRef;
    private String userId;
    private static final int REQUEST_CODE_FAVORITE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite); // tạo file XML này nha

        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoriteAdapter(this, favoriteList);
        recyclerView.setAdapter(adapter);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favRef = FirebaseDatabase.getInstance().getReference("Favorites").child(userId);
        menuRef = FirebaseDatabase.getInstance().getReference("menu");

        loadFavoriteItems();

    }

    private void loadFavoriteItems() {
        favRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteList.clear();
                List<String> favoriteIds = new ArrayList<>();

                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    Boolean isFav = itemSnap.getValue(Boolean.class);
                    if (isFav != null && isFav) {
                        favoriteIds.add(itemSnap.getKey()); // lưu ID
                    }
                }

                fetchFoodDetails(favoriteIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FavoriteActivity.this, "Lỗi khi tải dữ liệu yêu thích", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFoodDetails(List<String> favoriteIds) {
        menuRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteList.clear();
                for (DataSnapshot categorySnap : snapshot.getChildren()) {
                    for (DataSnapshot itemSnap : categorySnap.getChildren()) {
                        FoodItem item = itemSnap.getValue(FoodItem.class);
                        if (item != null) {
                            item.setId(itemSnap.getKey()); // Đặt ID từ key của Firebase
                            item.setCategory(categorySnap.getKey()); // 👈 phải có dòng này!
                            if (favoriteIds.contains(item.getId())) {
                                item.setIs_favorite(true);
                                favoriteList.add(item);
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FavoriteActivity.this, "Lỗi khi tải món ăn", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FAVORITE && resultCode == RESULT_OK) {
            // Có thể reload toàn bộ hoặc chỉ cập nhật 1 item nếu muốn tối ưu
            loadFavoriteItems(); // hoặc favoriteAdapter.notifyDataSetChanged();
        }
    }
}



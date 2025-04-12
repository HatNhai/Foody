package vn.edu.tlu.cse.ht.nhaivu.foodorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Adapter.FoodAdapter;
import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model.FoodItem;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private List<FoodItem> foodList;
    private BottomNavigationView bottomNav;
    private TabLayout tabLayout;
    private ImageView cartIcon;

    // === Bổ sung cho tìm kiếm ===
    private AutoCompleteTextView edtSearch;
    private ImageView imgSearch;
    private List<FoodItem> allFoodItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Ánh xạ view
        recyclerView = findViewById(R.id.rvFoodList);
        bottomNav = findViewById(R.id.bottomNav);
        tabLayout = findViewById(R.id.tabLayout);
        cartIcon = findViewById(R.id.cartIcon); // ánh xạ giỏ hàng
        edtSearch = findViewById(R.id.edtSearch); // ánh xạ ô tìm kiếm
        imgSearch = findViewById(R.id.imageView3); // biểu tượng tìm kiếm

        // Sự kiện click giỏ hàng
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodList = new ArrayList<>();
        adapter = new FoodAdapter(this, foodList);
        recyclerView.setAdapter(adapter);

        // Load mặc định
        loadDataFromFirebase("Ưu đãi");
        // Tải tất cả món ăn để dùng cho tìm kiếm
        loadAllFoodItems();

        // Sự kiện khi chọn tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tabTitle = tab.getText().toString();
                loadDataFromFirebase(tabTitle);  // Tên tab trùng tên nhánh trong Firebase
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Navigation bar
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_favorite) {
                startActivity(new Intent(this, FavoriteActivity.class));
                return true;
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadDataFromFirebase(String category) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("menu").child(category);

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    FoodItem food = itemSnapshot.getValue(FoodItem.class);
                    if (food != null) {
                        food.setId(itemSnapshot.getKey());
                        foodList.add(food);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // === Load toàn bộ dữ liệu món ăn để dùng cho tìm kiếm ===
    private void loadAllFoodItems() {
        DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference("menu");
        menuRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                allFoodItems.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    for (DataSnapshot foodSnapshot : categorySnapshot.getChildren()) {
                        FoodItem food = foodSnapshot.getValue(FoodItem.class);
                        if (food != null) {
                            food.setId(foodSnapshot.getKey());
                            allFoodItems.add(food);
                        }
                    }
                }
                setupSearchFunctionality(); // ✅ thiết lập sau khi có danh sách
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Lỗi tải tất cả món ăn: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // === Thiết lập chức năng tìm kiếm và gợi ý ===
    private void setupSearchFunctionality() {
        List<String> foodNames = new ArrayList<>();
        for (FoodItem item : allFoodItems) {
            foodNames.add(item.getName());
        }

        ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, foodNames);
        edtSearch.setAdapter(searchAdapter);
        edtSearch.setThreshold(1);

        // ✅ Khi chọn 1 gợi ý từ danh sách, chuyển sang SearchResultActivity
        edtSearch.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            Intent intent = new Intent(HomeActivity.this, SearchResultActivity.class);
            intent.putExtra("keyword", selectedName);
            startActivity(intent);
        });

        // ✅ Khi nhấn Enter để tìm kiếm
        edtSearch.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                performSearch(edtSearch.getText().toString().trim());
                return true;
            }
            return false;
        });

        // Khi nhấn vào biểu tượng tìm kiếm
        imgSearch.setOnClickListener(v -> {
            String keyword = edtSearch.getText().toString().trim();
            if (!keyword.isEmpty()) {
                Intent intent = new Intent(HomeActivity.this, SearchResultActivity.class);
                intent.putExtra("keyword", keyword);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });


    }

    // === Tìm kiếm và cập nhật danh sách món ăn ===
    private void performSearch(String keyword) {
        List<FoodItem> resultList = new ArrayList<>();
        for (FoodItem item : allFoodItems) {
            if (item.getName().toLowerCase().contains(keyword.toLowerCase())) {
                resultList.add(item);
            }
        }

        if (resultList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy món ăn", Toast.LENGTH_SHORT).show();
        } else {
            foodList.clear();
            foodList.addAll(resultList);
            adapter.notifyDataSetChanged();
        }
    }
}

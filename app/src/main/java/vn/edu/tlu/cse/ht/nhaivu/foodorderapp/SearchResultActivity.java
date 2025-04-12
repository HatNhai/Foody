package vn.edu.tlu.cse.ht.nhaivu.foodorderapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Adapter.FoodAdapter;
import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model.FoodItem;

public class SearchResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private List<FoodItem> allFoodItems = new ArrayList<>();
    private TextView tvSearchTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        recyclerView = findViewById(R.id.rvSearchResult);
        tvSearchTitle = findViewById(R.id.tvSearchTitle);

        String keyword = getIntent().getStringExtra("keyword");
        if (keyword != null && !keyword.isEmpty()) {
            tvSearchTitle.setText("Kết quả tìm kiếm cho: \"" + keyword + "\"");
            loadAndSearchFood(keyword);
        }

        ImageView imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FoodAdapter(this, allFoodItems);
        recyclerView.setAdapter(adapter);
    }

    private void loadAndSearchFood(String keyword) {
        DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference("menu");
        menuRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                allFoodItems.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    for (DataSnapshot foodSnapshot : categorySnapshot.getChildren()) {
                        FoodItem food = foodSnapshot.getValue(FoodItem.class);
                        if (food != null && food.getName() != null &&
                                food.getName().toLowerCase().contains(keyword.toLowerCase())) {
                            food.setId(foodSnapshot.getKey());
                            allFoodItems.add(food);
                        }
                    }
                }

                if (allFoodItems.isEmpty()) {
                    Toast.makeText(SearchResultActivity.this, "Không tìm thấy món ăn phù hợp", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchResultActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

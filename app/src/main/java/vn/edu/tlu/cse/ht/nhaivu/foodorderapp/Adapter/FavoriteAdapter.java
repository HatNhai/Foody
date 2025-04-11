package vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model.FoodItem;
import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.R;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<FoodItem> foodItems;
    private Context context;

    public FavoriteAdapter(Context context, List<FoodItem> foodItems) {
        this.context = context;
        this.foodItems = foodItems;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new FavoriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FoodItem food = foodItems.get(position);
        holder.tvName.setText(food.getName());
        holder.tvPrice.setText(food.getPrice() + "đ");
        holder.tvDesc.setText(food.getDescription());

        Glide.with(context)
                .load(food.getImage())
                .into(holder.imgFood);


        if (food.is_favorite()) {  // Nếu món ăn là yêu thích, hiển thị nút xóa
            holder.btnRemoveFavorite.setVisibility(View.VISIBLE);  // Hiện nút xóa
        } else {
            holder.btnRemoveFavorite.setVisibility(View.GONE);  // Ẩn nút xóa
        }

        // Thêm hành động khi nhấn nút xóa khỏi yêu thích
        holder.btnRemoveFavorite.setOnClickListener(v -> removeFromFavorites(food, position));

    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }
    private void removeFromFavorites(FoodItem foodItem, int position) {
        String itemId = foodItem.getId();
        String category = foodItem.getCategory();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // In ra giá trị của itemId và category để kiểm tra
        Log.d("REMOVE_FAVORITE", "Item ID: " + itemId);
        Log.d("REMOVE_FAVORITE", "Category: " + category);

        // Kiểm tra itemId và category không null trước khi thực hiện
        if (itemId != null && !itemId.isEmpty() && category != null && !category.isEmpty()) {
            DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference("menu");
            DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("Favorites").child(userId);

            // 1. Cập nhật trạng thái favorite trong menu
            menuRef.child(category).child(itemId).child("is_favorite").setValue(false);

            // 2. Xoá khỏi Favorites node của user
            favRef.child(itemId).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Xóa khỏi danh sách local
                    foodItems.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Đã xoá khỏi yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Lỗi khi xoá khỏi yêu thích", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Nếu itemId hoặc category không hợp lệ
            Toast.makeText(context, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }



    public class FavoriteViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFood;
        TextView tvName, tvPrice, tvDesc;
        ImageButton btnRemoveFavorite;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvDesc = itemView.findViewById(R.id.tvFoodDesc);
            btnRemoveFavorite = itemView.findViewById(R.id.btnRemoveFavorite);
        }
    }
}


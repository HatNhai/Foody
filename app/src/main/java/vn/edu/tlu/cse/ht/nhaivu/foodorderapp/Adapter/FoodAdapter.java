package vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model.FoodItem;
import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.ProductDetailActivity;
import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.R;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private Context context;
    private List<FoodItem> foodList;

    public FoodAdapter(Context context, List<FoodItem> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem food = foodList.get(position);
        holder.tvName.setText(food.getName());
        holder.tvPrice.setText(food.getPrice() + "đ");
        holder.tvDesc.setText(food.getDescription());

        Glide.with(context)
                .load(food.getImage())
                .into(holder.imgFood);
        holder.itemView.setOnClickListener(v -> {
            // Tạo Intent để chuyển đến ProductDetailActivity
            Intent intent = new Intent(context, ProductDetailActivity.class);

            // Truyền đối tượng FoodItem vào intent
            intent.putExtra("food", food);  // Truyền dữ liệu FoodItem

            // Khởi chạy ProductDetailActivity
            context.startActivity(intent);
        });

        //Lắng nghe sự kiện khi ấn vào món ăn
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("food", foodList.get(position)); // truyền món ăn được click
            context.startActivity(intent);
        });
        if (food.getAvgRating() > 0) {
            holder.tvRating.setText("★ " + String.format("%.1f", food.getAvgRating()) +
                    " (" + food.getReviewCount() + " đánh giá)");
        } else {
            holder.tvRating.setText("Chưa có đánh giá");
        }

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView tvName, tvPrice, tvDesc, tvRating;


        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvDesc = itemView.findViewById(R.id.tvFoodDesc);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}

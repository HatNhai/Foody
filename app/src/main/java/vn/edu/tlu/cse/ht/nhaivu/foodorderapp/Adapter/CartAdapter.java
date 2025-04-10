package vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<FoodItem> cartList;
    private DatabaseReference cartRef;

    public CartAdapter(Context context, List<FoodItem> cartList) {
        this.context = context;
        this.cartList = cartList;

        // Lấy userId để tham chiếu đến nhánh Cart của người dùng hiện tại
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userId);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        FoodItem item = cartList.get(position);

        // Gán dữ liệu cho View
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText("₫" + item.getPrice());
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        Glide.with(context).load(item.getImage()).into(holder.ivImage);

        // Sự kiện khi nhấn nút cộng
        holder.btnPlus.setOnClickListener(v -> {
            int quantity = item.getQuantity() + 1;
            item.setQuantity(quantity);

            cartRef.child(item.getId()).setValue(item); // Cập nhật Firebase
            notifyItemChanged(position); // Cập nhật lại UI
        });

        // Sự kiện khi nhấn nút trừ
        holder.btnMinus.setOnClickListener(v -> {
            int quantity = item.getQuantity();

            if (quantity > 0) {
                // Giảm số lượng nếu > 1
                quantity--;
                item.setQuantity(quantity);
                cartRef.child(item.getId()).setValue(item); // Cập nhật Firebase
                notifyItemChanged(position);
            } else {
                // Nếu số lượng = 1 → xóa món khỏi Firebase & danh sách
                cartRef.child(item.getId()).removeValue();
                cartList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartList.size());

                Toast.makeText(context, "Đã xoá món khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice, tvQuantity;
        TextView btnMinus, btnPlus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.imgFood);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }
    }
}

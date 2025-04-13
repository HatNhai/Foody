package vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model.Order;
import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model.OrderedItem;
import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model.Review;
import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.R;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orderList;
    private Runnable refreshCallback; // Thêm dòng này

    public OrderAdapter(Context context, List<Order> orderList,Runnable refreshCallback) {
        this.context = context;
        this.orderList = orderList;
        this.refreshCallback = refreshCallback;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        List<String> itemNames = new ArrayList<>();
        int totalQuantity = 0;

        if (order.items != null) {
            for (Map.Entry<String, OrderedItem> entry : order.items.entrySet()) {
                OrderedItem item = entry.getValue();
                if (item != null) {
                    itemNames.add(item.name != null ? item.name : "Tên món");
                    totalQuantity += item.quantity;
                }
            }
        }

        String displayName = itemNames.isEmpty() ? "Đơn hàng" : itemNames.get(0);
        if (itemNames.size() > 1) {
            displayName += " +" + (itemNames.size() - 1) + " món khác";
        }

        holder.tvItemName.setText(displayName);
        holder.tvQuantity.setText("Số lượng: x" + totalQuantity);
        holder.tvTotalAmount.setText("Tổng tiền: " + order.totalAmount + " đ");
        if (order.review != null) {
            holder.tvRating.setText("Đánh giá: " + order.review.rating + " ★");
            holder.btnReview.setVisibility(View.GONE);
        } else {
            holder.tvRating.setText("Chưa có đánh giá");
            holder.btnReview.setVisibility(View.VISIBLE);
            holder.btnReview.setOnClickListener(v -> showReviewDialog(order));
        }
    }
    private void showReviewDialog(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_review, null);
        builder.setView(view);

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        EditText edtComment = view.findViewById(R.id.edtComment);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);

        AlertDialog dialog = builder.create();

        btnSubmit.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String comment = edtComment.getText().toString().trim();

            if (rating == 0) {
                Toast.makeText(context, "Vui lòng chọn số sao.", Toast.LENGTH_SHORT).show();
                return;
            }

            Review review = new Review(rating, comment, System.currentTimeMillis());

            FirebaseDatabase.getInstance().getReference("Orders")
                    .child(order.id) // Đảm bảo Order có trường id
                    .child("review")
                    .setValue(review)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Đánh giá thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        new AlertDialog.Builder(context)
                                .setTitle("Đánh giá món ăn")
                                .setMessage("Bạn có muốn đánh giá từng món ăn trong đơn này không?")
                                .setPositiveButton("Có", (d, w) -> {
                                    showItemReviewDialog(order); // 👉 bước tiếp theo
                                })
                                .setNegativeButton("Không", (d, w) -> {
                                    refreshCallback.run(); // Cập nhật lại giao diện nếu không đánh giá món
                                })
                                .show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Lỗi khi gửi đánh giá", Toast.LENGTH_SHORT).show();
                    });
        });

        dialog.show();
    }
    private void showItemReviewDialog(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_item_review, null);
        builder.setView(view);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerItemReview);
        Button btnSubmit = view.findViewById(R.id.btnSubmitItemReview);

        List<OrderedItem> itemList = new ArrayList<>(order.items.values());
        ItemReviewAdapter adapter = new ItemReviewAdapter(itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        AlertDialog dialog = builder.create();

        btnSubmit.setOnClickListener(v -> {
            Map<String, Review> reviewMap = adapter.getReviewMap();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Foods");

            for (Map.Entry<String, Review> entry : reviewMap.entrySet()) {
                String foodId = entry.getKey();
                Review review = entry.getValue();

                foodRef.child(foodId).child("reviews").child(userId).setValue(review);

                foodRef.child(foodId).child("reviews").addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        float sum = 0;
                        int count = 0;

                        for (DataSnapshot child : snapshot.getChildren()) {
                            Review r = child.getValue(Review.class);
                            if (r != null) {
                                sum += r.rating;
                                count++;
                            }
                        }

                        float avg = (count == 0) ? 0 : sum / count;
                        foodRef.child(foodId).child("avgRating").setValue(avg);
                        foodRef.child(foodId).child("reviewCount").setValue(count);
                    }

                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            Toast.makeText(context, "Đã đánh giá món ăn", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            refreshCallback.run();
        });

        dialog.show();
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvQuantity, tvTotalAmount,tvRating;
        Button btnReview;
        ImageView imageView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvRating = itemView.findViewById(R.id.tvRating);
            btnReview = itemView.findViewById(R.id.btnReview);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

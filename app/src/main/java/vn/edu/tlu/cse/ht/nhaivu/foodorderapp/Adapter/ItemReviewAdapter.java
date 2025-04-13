package vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model.OrderedItem;
import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model.Review;
import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.R;

public class ItemReviewAdapter extends RecyclerView.Adapter<ItemReviewAdapter.ViewHolder> {
    private List<OrderedItem> itemList;
    private Map<String, Review> reviewMap = new HashMap<>();

    public ItemReviewAdapter(List<OrderedItem> itemList) {
        this.itemList = itemList;
    }

    public Map<String, Review> getReviewMap() {
        return reviewMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderedItem item = itemList.get(position);
        holder.tvItemName.setText(item.name);

        holder.ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            reviewMap.put(item.getId(), new Review(rating, "", System.currentTimeMillis()));
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}

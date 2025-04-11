package vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model.Order;
import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model.OrderedItem;
import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.R;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
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
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvQuantity, tvTotalAmount;
        ImageView imageView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

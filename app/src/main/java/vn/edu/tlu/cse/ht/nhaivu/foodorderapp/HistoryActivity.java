package vn.edu.tlu.cse.ht.nhaivu.foodorderapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Adapter.OrderAdapter;
import vn.edu.tlu.cse.ht.nhaivu.foodorderapp.Model.Order;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrderHistory;
    private ImageView btnBack;

    private OrderAdapter orderAdapter;
    private List<Order> orderList;

    private static final String TAG = "HistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rvOrderHistory = findViewById(R.id.rvOrderHistory);
        btnBack = findViewById(R.id.btnBack);

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);

        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        rvOrderHistory.setAdapter(orderAdapter);

        btnBack.setOnClickListener(v -> finish());

        loadOrders();
    }

    private void loadOrders() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "Tải đơn hàng cho UID: " + uid);

        FirebaseDatabase.getInstance().getReference("Orders")
                .orderByChild("uid")
                .equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot orderSnap : snapshot.getChildren()) {
                                Order order = orderSnap.getValue(Order.class);
                                if (order != null) {
                                    orderList.add(order);
                                }
                            }
                            Log.d(TAG, "Tải thành công " + orderList.size() + " đơn hàng.");
                        } else {
                            Log.d(TAG, "Không có đơn hàng nào cho UID: " + uid);
                            Toast.makeText(HistoryActivity.this, "Chưa có đơn hàng nào.", Toast.LENGTH_SHORT).show();
                        }
                        orderAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Lỗi Firebase: " + error.getMessage());
                        Toast.makeText(HistoryActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

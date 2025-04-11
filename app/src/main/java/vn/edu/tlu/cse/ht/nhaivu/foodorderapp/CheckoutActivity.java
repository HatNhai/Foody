package vn.edu.tlu.cse.ht.nhaivu.foodorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvTotalAmount;
    private Button btnProceedToPayment;
    private EditText etName, etAddress, etPhone;
    private ImageView btnBack;
    private int totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Ánh xạ view
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnProceedToPayment = findViewById(R.id.btnProceedToPayment);
        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        btnBack = findViewById(R.id.btnBack);

        // Nhận tổng tiền từ Intent
        totalAmount = getIntent().getIntExtra("totalAmount", 0);
        tvTotalAmount.setText(String.format("%,d đ", totalAmount));

        // Xử lý khi nhấn nút quay lại
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, CartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Xử lý khi nhấn nút Đặt hàng
        btnProceedToPayment.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(CheckoutActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Tạo đơn hàng mới trong Orders
            DatabaseReference orderRef = FirebaseDatabase.getInstance()
                    .getReference("Orders")
                    .push(); // ✅ Tạo key tự động để phù hợp cấu trúc

            // ✅ Lưu thông tin đơn hàng kèm uid
            orderRef.child("uid").setValue(userId);
            orderRef.child("name").setValue(name);
            orderRef.child("address").setValue(address);
            orderRef.child("phone").setValue(phone);
            orderRef.child("totalAmount").setValue(totalAmount);
            orderRef.child("status").setValue("Hoàn thành");

            // Lấy dữ liệu từ giỏ hàng và thêm vào đơn hàng
            DatabaseReference cartRef = FirebaseDatabase.getInstance()
                    .getReference("Cart")
                    .child(userId);

            cartRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (DataSnapshot itemSnapshot : task.getResult().getChildren()) {
                        String itemId = itemSnapshot.getKey();
                        String itemName = itemSnapshot.child("name").getValue(String.class);
                        Integer quantity = itemSnapshot.child("quantity").getValue(Integer.class);

                        if (itemId != null && itemName != null && quantity != null) {
                            orderRef.child("items").child(itemId).child("name").setValue(itemName);
                            orderRef.child("items").child(itemId).child("quantity").setValue(quantity);
                        }
                    }

                    // Xoá giỏ hàng sau khi đặt
                    cartRef.removeValue();

                    // Thông báo & chuyển về HomeActivity
                    Toast.makeText(CheckoutActivity.this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CheckoutActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CheckoutActivity.this, "Lỗi khi xử lý giỏ hàng!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}

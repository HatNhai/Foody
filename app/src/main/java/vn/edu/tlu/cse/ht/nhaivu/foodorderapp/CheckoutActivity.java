package vn.edu.tlu.cse.ht.nhaivu.foodorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvTotalAmount;
    private Button btnProceedToPayment;
    private EditText etName, etAddress, etPhone;

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

        // Nhận tổng tiền từ Intent
        totalAmount = getIntent().getIntExtra("totalAmount", 0);
        tvTotalAmount.setText(String.format("%,d đ", totalAmount));

        // Xử lý khi nhấn nút "Tiếp tục thanh toán"
        btnProceedToPayment.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(CheckoutActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lưu đơn hàng vào Firebase
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference orderRef = FirebaseDatabase.getInstance()
                    .getReference("Orders")
                    .child(userId)
                    .push(); // Tạo ID mới

            orderRef.child("name").setValue(name);
            orderRef.child("address").setValue(address);
            orderRef.child("phone").setValue(phone);
            orderRef.child("totalAmount").setValue(totalAmount);
            orderRef.child("status").setValue("Pending");

            // Chuyển sang màn hình thanh toán
            Intent intent = new Intent(CheckoutActivity.this, MethodPaymentActivity.class);
            intent.putExtra("totalAmount", totalAmount); // Gửi tiếp nếu cần dùng bên đó
            startActivity(intent);
        });
    }
}

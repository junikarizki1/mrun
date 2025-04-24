package com.example.formmelawirun;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText etName, etEmail, etPhone;
    RadioGroup rgGender;
    Spinner spinnerDistance;
    Button btnSubmit;
    TextView tvViewRegistrants;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Inisialisasi komponen
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        rgGender = findViewById(R.id.rgGender);
        spinnerDistance = findViewById(R.id.spinnerDistance);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvViewRegistrants = findViewById(R.id.tvViewRegistrants);
        dbHelper = new DatabaseHelper(this);

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String gender = "";
            int selectedGenderId = rgGender.getCheckedRadioButtonId();
            if (selectedGenderId != -1) {
                RadioButton selectedGender = findViewById(selectedGenderId);
                gender = selectedGender.getText().toString();
            }
            String distance = spinnerDistance.getSelectedItem().toString();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(gender)) {
                Toast.makeText(this, "Mohon lengkapi semua data!", Toast.LENGTH_SHORT).show();
            } else {
                boolean inserted = dbHelper.insertData(name, gender, email, phone, distance);
                if (inserted) {
                    Toast.makeText(this, "Pendaftaran berhasil!", Toast.LENGTH_LONG).show();
                    etName.setText("");
                    etEmail.setText("");
                    etPhone.setText("");
                    rgGender.clearCheck();
                    spinnerDistance.setSelection(0);
                } else {
                    Toast.makeText(this, "Gagal menyimpan data!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvViewRegistrants.setOnClickListener(v -> showRegistrantsDialog());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Fungsi untuk menampilkan data peserta dalam AlertDialog
    private void showRegistrantsDialog() {
        Cursor res = dbHelper.getAllData();
        if (res.getCount() == 0) {
            Toast.makeText(MainActivity.this, "Belum ada peserta terdaftar.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder buffer = new StringBuilder();
        while (res.moveToNext()) {
            buffer.append("Nama          : ").append(res.getString(res.getColumnIndexOrThrow("nama"))).append("\n");
            buffer.append("Jenis Kelamin : ").append(res.getString(res.getColumnIndexOrThrow("jenis_kelamin"))).append("\n");
            buffer.append("Email         : ").append(res.getString(res.getColumnIndexOrThrow("email"))).append("\n");
            buffer.append("No HP         : ").append(res.getString(res.getColumnIndexOrThrow("no_hp"))).append("\n");
            buffer.append("Jarak Lari    : ").append(res.getString(res.getColumnIndexOrThrow("jarak_lari"))).append("\n\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Daftar Peserta Melawi Run");
        builder.setMessage(buffer.toString());
        builder.show();
    }
}

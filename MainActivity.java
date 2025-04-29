package com.example.formmelawirun;

import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
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

        // Fungsi Simpan
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

        // Fungsi Edit
        Button btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buat AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Edit Data Peserta");

                // Buat layout vertical manual
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(16, 16, 16, 16);

                // Input Nama untuk mencari
                final EditText inputNama = new EditText(MainActivity.this);
                inputNama.setHint("Nama (yang ingin diedit)");
                layout.addView(inputNama);

                // Input Email baru
                final EditText inputEmail = new EditText(MainActivity.this);
                inputEmail.setHint("Email Baru");
                layout.addView(inputEmail);

                // Input No HP baru
                final EditText inputNoHp = new EditText(MainActivity.this);
                inputNoHp.setHint("Nomor HP Baru");
                layout.addView(inputNoHp);

                // Input Jarak Lari baru
                final EditText inputJarak = new EditText(MainActivity.this);
                inputJarak.setHint("Jarak Lari Baru");
                layout.addView(inputJarak);

                builder.setView(layout);

                builder.setPositiveButton("Simpan", (dialog, which) -> {
                    String nama = inputNama.getText().toString().trim();
                    String email = inputEmail.getText().toString().trim();
                    String noHp = inputNoHp.getText().toString().trim();
                    String jarak = inputJarak.getText().toString().trim();

                    if (!nama.isEmpty() && !email.isEmpty() && !noHp.isEmpty() && !jarak.isEmpty()) {
                        boolean updated = dbHelper.updateDataByNama(nama, email, noHp, jarak);
                        if (updated) {
                            Toast.makeText(MainActivity.this, "Data berhasil diubah", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Data gagal diubah (nama tidak ditemukan)", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Batal", null);

                builder.show();
            }
        });

        // Fungsi Hapus
        Button btnDeleteNama = findViewById(R.id.btnDeleteNama);
        btnDeleteNama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Bikin Alert Dialog input nama
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Hapus Data Peserta");

                final EditText input = new EditText(MainActivity.this);
                input.setHint("Masukkan Nama Peserta");
                builder.setView(input);

                builder.setPositiveButton("Hapus", (dialog, which) -> {
                    String nama = input.getText().toString().trim();
                    if (!nama.isEmpty()) {
                        boolean deleted = dbHelper.deleteDataByNama(nama);
                        if (deleted) {
                            Toast.makeText(MainActivity.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Batal", null);
                builder.show();
            }
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

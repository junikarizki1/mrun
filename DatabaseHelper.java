package com.example.formmelawirun;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "melawirun.db";
    public static final String TABLE_NAME = "pendaftaran";
    public static final String COL_1 = "id";
    public static final String COL_2 = "nama";
    public static final String COL_3 = "jenis_kelamin";
    public static final String COL_4 = "email";
    public static final String COL_5 = "no_hp";
    public static final String COL_6 = "jarak_lari";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    // Buat tabel
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT, " +
                COL_3 + " TEXT, " +
                COL_4 + " TEXT, " +
                COL_5 + " TEXT, " +
                COL_6 + " TEXT)");
    }

    // Jika versi berubah
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Fungsi simpan data
    public boolean insertData(String nama, String jenisKelamin, String email, String noHp, String jarakLari) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, nama);
        contentValues.put(COL_3, jenisKelamin);
        contentValues.put(COL_4, email);
        contentValues.put(COL_5, noHp);
        contentValues.put(COL_6, jarakLari);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1; // true jika berhasil
    }

    // Fungsi ambil semua data
    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // Fungsi Edit
    public boolean updateDataByNama(String nama, String email, String noHp, String jarakLari) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("no_hp", noHp);
        contentValues.put("jarak_lari", jarakLari);

        // update berdasarkan nama
        int result = db.update(TABLE_NAME, contentValues, "nama = ?", new String[]{nama});
        return result > 0; // kalau result > 0 artinya sukses update
    }


    // Fungsi Hapus
    public boolean deleteDataByNama(String nama) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, "nama = ?", new String[]{nama});
        return result > 0;
    }

}

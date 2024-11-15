package com.example.alive;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class create_m extends AppCompatActivity {

    private EditText namee, place, datee, timee, desc;
    private Button cr_m;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_m);

        // Инициализация views
        namee = findViewById(R.id.namee);
        place = findViewById(R.id.place);
        datee = findViewById(R.id.datee);
        timee = findViewById(R.id.timee);
        desc = findViewById(R.id.desc);
        cr_m = findViewById(R.id.cr_m);

        // Инициализация helper'а базы данных
        dbHelper = new DatabaseHelper(this);

        cr_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (createMeet()) {
                    // Закрываем текущую активность
                    finish();
                }
            }
        });
    }

    private boolean createMeet() {
        String name = namee.getText().toString().trim();
        String placeStr = place.getText().toString().trim();
        String dateStr = datee.getText().toString().trim();
        String timeStr = timee.getText().toString().trim();
        String descStr = desc.getText().toString().trim();

        // Простая валидация
        if (name.isEmpty() || placeStr.isEmpty() || dateStr.isEmpty() || timeStr.isEmpty() || descStr.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Создание новой встречи
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_PLACE, placeStr);
        values.put(DatabaseHelper.COLUMN_DATE, dateStr);
        values.put(DatabaseHelper.COLUMN_TIME, timeStr);
        values.put(DatabaseHelper.COLUMN_DECS, descStr);

        // Вставка строки в базу данных
        long newRowId = db.insert(DatabaseHelper.TABLE_MEET, null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "Встреча успешно создана!", Toast.LENGTH_SHORT).show();
            clearFields();
            return true;
        } else {
            Toast.makeText(this, "Ошибка при создании встречи", Toast.LENGTH_SHORT).show();
            Log.e("create_m", "Error inserting meet: " + db.getPath());
            return false;
        }
    }

    private void clearFields() {
        namee.setText("");
        place.setText("");
        datee.setText("");
        timee.setText("");
        desc.setText("");
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
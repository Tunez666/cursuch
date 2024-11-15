package com.example.alive;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class create_m extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_m); // Убедитесь, что имя файла макета совпадает

        // Инициализация DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Найдем элементы макета
        EditText nameField = findViewById(R.id.namee);
        EditText placeField = findViewById(R.id.place);
        EditText dateField = findViewById(R.id.datee);
        EditText timeField = findViewById(R.id.timee);
        EditText descField = findViewById(R.id.desc);
        Button createButton = findViewById(R.id.cr_m);

        // Обработка нажатия кнопки "Создать"
        createButton.setOnClickListener(view -> {
            String name = nameField.getText().toString().trim();
            String place = placeField.getText().toString().trim();
            String date = dateField.getText().toString().trim();
            String time = timeField.getText().toString().trim();
            String description = descField.getText().toString().trim();

            if (name.isEmpty() || place.isEmpty() || date.isEmpty() || time.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            } else {
                // Сохранение данных в базе
                if (addMeetingToDatabase(name, date, time, place, description)) {
                    Toast.makeText(this, "Встреча создана успешно", Toast.LENGTH_SHORT).show();
                    finish(); // Закрываем активность
                } else {
                    Toast.makeText(this, "Ошибка при создании встречи", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean addMeetingToDatabase(String name, String date, String time, String place, String description) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_DATE, date);
        values.put(DatabaseHelper.COLUMN_TIME, time);
        values.put(DatabaseHelper.COLUMN_PLACE, place);
        values.put(DatabaseHelper.COLUMN_DECS, description);

        long result = database.insert(DatabaseHelper.TABLE_MEET, null, values);
        return result != -1;
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}

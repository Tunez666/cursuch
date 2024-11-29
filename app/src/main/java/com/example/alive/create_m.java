package com.example.alive;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class create_m extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView dateField, timeField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_m);

        dbHelper = new DatabaseHelper(this);

        // Найдем элементы макета
        TextView nameField = findViewById(R.id.namee);
        TextView placeField = findViewById(R.id.place);
        dateField = findViewById(R.id.datee);
        timeField = findViewById(R.id.timee);
        TextView descField = findViewById(R.id.desc);
        Button createButton = findViewById(R.id.cr_m);

        // Обработка выбора даты
        dateField.setOnClickListener(v -> showDatePicker());

        // Обработка выбора времени
        timeField.setOnClickListener(v -> showTimePicker());

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
                if (addMeetingToDatabase(name, date, time, place, description)) {
                    Toast.makeText(this, "Встреча создана успешно", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Ошибка при создании встречи", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = String.format("%02d-%02d-%d", dayOfMonth, month1 + 1, year1);
            dateField.setText(selectedDate);
        }, year, month, day).show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
            timeField.setText(selectedTime);
        }, hour, minute, true).show();
    }

    private boolean addMeetingToDatabase(String name, String date, String time, String place, String description) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Преобразуем дату и время в метку времени
        long timestamp = convertToTimestamp(date, time);
        if (timestamp == -1) {
            Toast.makeText(this, "Некорректный формат даты или времени", Toast.LENGTH_SHORT).show();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_DATE, timestamp); // Вставляем метку времени
        values.put(DatabaseHelper.COLUMN_PLACE, place);
        values.put(DatabaseHelper.COLUMN_DECS, description);
        values.put(DatabaseHelper.COLUMN_TIME, time); // Вставляем время

        long result = database.insert(DatabaseHelper.TABLE_MEET, null, values);
        return result != -1; // Проверяем, успешно ли выполнена вставка
    }

    private long convertToTimestamp(String date, String time) {
        try {
            // Объединяем дату и время в одну строку
            String dateTime = date + " " + time; // Формат: "дд-ММ-гггг чч:мм"

            // Используем правильный формат
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
            java.util.Date parsedDate = format.parse(dateTime);

            // Преобразуем в миллисекунды и делим на 1000, чтобы получить секунды
            return parsedDate.getTime() / 1000; // Возвращаем секунды
        } catch (Exception e) {
            Log.e("CreateMeeting", "Ошибка преобразования даты/времени: " + e.getMessage());
            return -1; // Если ошибка, возвращаем -1
        }
    }


    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}

package com.example.alive;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.List;

public class create_m extends AppCompatActivity {

    private static final String TAG = "create_m";
    private DatabaseHelper dbHelper;
    private TextView dateField, timeField;
    private BottomNavigationView bottomNavigationView;
    private Spinner categorySpinner;
    private Spinner eventSpinner;
    private List<String> categories;
    private List<String> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_m);

        dbHelper = new DatabaseHelper(this);

        // Найдем элементы макета
        EditText nameField = findViewById(R.id.namee);
        EditText placeField = findViewById(R.id.place);
        dateField = findViewById(R.id.datee);
        timeField = findViewById(R.id.timee);
        EditText descField = findViewById(R.id.desc);
        Button createButton = findViewById(R.id.cr_m);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        categorySpinner = findViewById(R.id.category_spinner);
        eventSpinner = findViewById(R.id.event_spinner);

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

        setupBottomNavigation();
        loadCategoriesAndEvents();
        setupSpinners();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(create_m.this, glavnay.class));
                return true;
            } else if (itemId == R.id.nav_friends) {
                startActivity(new Intent(create_m.this, FriendsListActivity.class));
                return true;
            } else if (itemId == R.id.nav_create) {
                // Уже на странице создания
                return true;
            } else if (itemId == R.id.nav_md) {
                startActivity(new Intent(create_m.this, md.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(create_m.this, lk.class));
                return true;
            }
            return false;
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
        long timestamp = convertToTimestamp(date, time);
        if (timestamp == -1) {
            Toast.makeText(this, "Некорректный формат даты или времени", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (categorySpinner.getSelectedItem() == null || eventSpinner.getSelectedItem() == null) {
            Toast.makeText(this, "Пожалуйста, выберите категорию и событие", Toast.LENGTH_SHORT).show();
            return false;
        }

        String selectedCategory = categorySpinner.getSelectedItem().toString();
        String selectedEvent = eventSpinner.getSelectedItem().toString();

        long categoryId = dbHelper.getCategoryId(selectedCategory);
        long eventId = dbHelper.getEventId(selectedEvent);

        if (categoryId == -1 || eventId == -1) {
            Toast.makeText(this, "Ошибка при получении ID категории или события", Toast.LENGTH_SHORT).show();
            return false;
        }

        long userId = getCurrentUserId();

        Log.d(TAG, "Попытка добавить встречу в базу данных");
        Log.d(TAG, "Имя: " + name + ", Дата: " + date + ", Время: " + time + ", Место: " + place + ", Описание: " + description);
        Log.d(TAG, "ID пользователя: " + userId + ", ID категории: " + categoryId + ", ID события: " + eventId);
        dbHelper.addMeet(name, timestamp, time, place, description, userId, categoryId, eventId);
        Log.d(TAG, "Встреча успешно добавлена в базу данных");
        return true;
    }

    private long convertToTimestamp(String date, String time) {
        try {
            String dateTime = date + " " + time;
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
            java.util.Date parsedDate = format.parse(dateTime);
            return parsedDate.getTime() / 1000;
        } catch (Exception e) {
            Log.e(TAG, "Ошибка преобразования даты/времени: " + e.getMessage());
            return -1;
        }
    }

    private void loadCategoriesAndEvents() {
        categories = dbHelper.getAllCategories();
        events = dbHelper.getAllEvents();

        if (categories.isEmpty()) {
            dbHelper.addCategory("Рабочая");
            dbHelper.addCategory("Дружеская");
            dbHelper.addCategory("Школьная");
            dbHelper.addCategory("Студенческая");
            categories = dbHelper.getAllCategories();
        }
        if (events.isEmpty()) {
            dbHelper.addEvent("День рождения");
            dbHelper.addEvent("Прогулка");
            dbHelper.addEvent("Созвон");
            dbHelper.addEvent("Совещание");
            dbHelper.addEvent("Вечеринка");
            events = dbHelper.getAllEvents();
        }
    }

    private void setupSpinners() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<String> eventAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, events);
        eventAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventSpinner.setAdapter(eventAdapter);
    }

    private long getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        long userId = prefs.getLong("currentUserId", -1);
        Log.d(TAG, "Получен ID пользователя: " + userId);
        return userId;
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}


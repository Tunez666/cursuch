package com.example.alive;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.Manifest;


public class create_m extends AppCompatActivity {

    private static final String TAG = "create_m";
    private DatabaseHelper dbHelper;
    private TextView dateField, timeField;
    private BottomNavigationView bottomNavigationView;
    private TextView eventField, categoryField;
    private Spinner friendsSpinner;
    private List<Long> selectedFriendIds = new ArrayList<>();
    private List<Long> allFriendIds = new ArrayList<>();
    private int selectedEventId = -1, selectedCategoryId = -1;

    private long getCurrentUserId() {
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        long userId = preferences.getLong("userId", -1);
        Log.d(TAG, "Retrieved user ID: " + userId);
        return userId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_m);

        dbHelper = new DatabaseHelper(this);

        // Initialize UI elements
        TextView nameField = findViewById(R.id.namee);
        TextView placeField = findViewById(R.id.place);
        dateField = findViewById(R.id.datee);
        timeField = findViewById(R.id.timee);
        TextView descField = findViewById(R.id.desc);
        Button createButton = findViewById(R.id.cr_m);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        eventField = findViewById(R.id.eventField);
        categoryField = findViewById(R.id.categoryField);
        friendsSpinner = findViewById(R.id.friendsSpinner);

        loadFriends();  // Загружаем друзей

        // Set up click listeners
        eventField.setOnClickListener(v -> showEventPicker());
        categoryField.setOnClickListener(v -> showCategoryPicker());
        dateField.setOnClickListener(v -> showDatePicker());
        timeField.setOnClickListener(v -> showTimePicker());

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, 100);
        }

        setupBottomNavigation();
    }

    private void loadFriends() {
        long currentUserId = getCurrentUserId();
        List<String> friendNames = dbHelper.getFriends(currentUserId);
        allFriendIds.clear();
        allFriendIds.addAll(dbHelper.getFriendIds(currentUserId));

        // Создаем адаптер для Spinner с кастомизированным стилем
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, friendNames) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);

                // Устанавливаем цвет текста и стиль
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(16f);
                textView.setPadding(16, 16, 16, 16);
                view.setBackgroundResource(R.drawable.fox_edit_text_background);

                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);

                // Применяем тот же стиль для выпадающего списка
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(16f);
                textView.setPadding(16, 16, 16, 16);

                return view;
            }
        };

        // Устанавливаем адаптер для Spinner
        friendsSpinner.setAdapter(adapter);

        // Обработчик выбора друзей
        friendsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position >= 0 && position < allFriendIds.size()) {
                    long selectedFriendId = allFriendIds.get(position);
                    if (!selectedFriendIds.contains(selectedFriendId)) {
                        selectedFriendIds.add(selectedFriendId);
                        Toast.makeText(create_m.this, "Друг добавлен: " + friendNames.get(position), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Ничего не делаем
            }
        });
    }

    private long convertToTimestamp(String date, String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
            return sdf.parse(date + " " + time).getTime() / 1000;
        } catch (Exception e) {
            Log.e(TAG, "Error converting date and time to timestamp", e);
            return -1;
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateField(calendar);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            updateTimeField(calendar);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void updateDateField(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        dateField.setText(dateFormat.format(calendar.getTime()));
    }

    private void updateTimeField(Calendar calendar) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeField.setText(timeFormat.format(calendar.getTime()));
    }

    private void showEventPicker() {
        String[] events = {"День рождения", "Прогулка", "Свидание", "Путешествие"};
        new android.app.AlertDialog.Builder(this)
                .setTitle("Выберите событие")
                .setItems(events, (dialog, which) -> {
                    eventField.setText(events[which]);
                    selectedEventId = which + 1;  // Пример: ID события = индекс + 1
                })
                .show();
    }

    private void showCategoryPicker() {
        String[] categories = {"Рабочая", "Дружеская", "Семейная"};
        new android.app.AlertDialog.Builder(this)
                .setTitle("Выберите категорию")
                .setItems(categories, (dialog, which) -> {
                    categoryField.setText(categories[which]);
                    selectedCategoryId = which + 1;  // Пример: ID категории = индекс + 1
                })
                .show();
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
                // Already on create page
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

    private boolean addMeetingToDatabase(String name, String date, String time, String place, String description) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long currentUserId = getCurrentUserId();

        if (currentUserId == -1) return false;

        if (selectedCategoryId == -1 || selectedEventId == -1) {
            Log.e(TAG, "Ошибка: не выбраны категория или событие");
            Toast.makeText(this, "Пожалуйста, выберите категорию и событие", Toast.LENGTH_SHORT).show();
            return false;
        }

        long timestamp = convertToTimestamp(date, time);
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_DATE, timestamp);
        values.put(DatabaseHelper.COLUMN_TIME, time);
        values.put(DatabaseHelper.COLUMN_PLACE, place);
        values.put(DatabaseHelper.COLUMN_DESC, description);
        values.put(DatabaseHelper.COLUMN_USER_ID, currentUserId);
        values.put(DatabaseHelper.COLUMN_ID_C, selectedCategoryId);
        values.put(DatabaseHelper.COLUMN_ID_E, selectedEventId);

        long meetingId = database.insert(DatabaseHelper.TABLE_MEET, null, values);
        if (meetingId != -1) {
            dbHelper.addMeetParticipants(meetingId, selectedFriendIds);
            // Добавление события в календарь
            addEventToCalendar(name, date, time, place, description);
            return true;
        }
        return false;
    }

    private void addEventToCalendar(String title, String date, String time, String location, String description) {
        try {
            long startMillis = convertToTimestamp(date, time) * 1000; // Перевод в миллисекунды
            long endMillis = startMillis + (60 * 60 * 1000); // Допустим, продолжительность встречи — 1 час

            ContentValues eventValues = new ContentValues();
            eventValues.put(CalendarContract.Events.CALENDAR_ID, 1); // ID календаря (обычно 1 для основного)
            eventValues.put(CalendarContract.Events.TITLE, title);
            eventValues.put(CalendarContract.Events.DESCRIPTION, description);
            eventValues.put(CalendarContract.Events.EVENT_LOCATION, location);
            eventValues.put(CalendarContract.Events.DTSTART, startMillis);
            eventValues.put(CalendarContract.Events.DTEND, endMillis);
            eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, "UTC");

            getContentResolver().insert(CalendarContract.Events.CONTENT_URI, eventValues);
            Log.d(TAG, "Событие успешно добавлено в календарь");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при добавлении события в календарь", e);
        }
    }


    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}


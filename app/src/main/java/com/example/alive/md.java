package com.example.alive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class md extends AppCompatActivity {

    private static final String TAG = "mdActivity"; // Added TAG for logging
    private DatabaseHelper dbHelper;
    private ListView meetingsListView;
    private Button clearMeetingsButton;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_md);

        meetingsListView = findViewById(R.id.meetingsListView);
        clearMeetingsButton = findViewById(R.id.clearMeetingsButton);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        dbHelper = new DatabaseHelper(this);

        long currentUserId = getCurrentUserId();
        if (currentUserId != -1) {
            displayUserMeetings(currentUserId);
        } else {
            Log.e(TAG, "Ошибка: Не удалось получить ID пользователя");
            Toast.makeText(this, "Ошибка: Не удалось получить ID пользователя. Пожалуйста, войдите снова.", Toast.LENGTH_LONG).show();
            // Redirect to login activity
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
            return;
        }

        clearMeetingsButton.setOnClickListener(v -> {
            clearUserMeetings(currentUserId);
        });

        setupBottomNavigation();
    }

    private long getCurrentUserId() {
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        long userId = preferences.getLong("userId", -1); // Changed from "currentUserId" to "userId"
        Log.d(TAG, "Получен ID пользователя: " + userId);
        return userId;
    }

    private void displayUserMeetings(long userId) {
        Log.d(TAG, "Попытка отобразить встречи для пользователя с ID: " + userId); // Added log
        Cursor cursor = dbHelper.getMeetingsForUser(userId);
        List<String> meetingsList = new ArrayList<>();

        Log.d(TAG, "Количество найденных встреч: " + (cursor != null ? cursor.getCount() : 0)); // Added log

        if (cursor != null && cursor.getCount() > 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
                String dateTime = dateFormat.format(new Date(timestamp * 1000));
                String place = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLACE));

                Log.d(TAG, "Встреча: " + name + " - " + dateTime); // Added log

                meetingsList.add(name + " - " + dateTime + " at " + place);
            }
            cursor.close();
        }

        if (meetingsList.isEmpty()) {
            meetingsList.add("У вас пока нет созданных встреч.");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, meetingsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.BLACK);
                return view;
            }
        };
        meetingsListView.setAdapter(adapter);
    }


    private void clearUserMeetings(long userId) {
        dbHelper.clearMeetings(userId);
        Toast.makeText(this, "Список встреч очищен", Toast.LENGTH_SHORT).show();
        displayUserMeetings(userId);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(md.this, glavnay.class));
                return true;
            } else if (itemId == R.id.nav_friends) {
                startActivity(new Intent(md.this, FriendsListActivity.class));
                return true;
            } else if (itemId == R.id.nav_create) {
                startActivity(new Intent(md.this, create_m.class));
                return true;
            } else if (itemId == R.id.nav_md) {
                // Уже на странице встреч
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(md.this, lk.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        long currentUserId = getCurrentUserId();
        if (currentUserId != -1) {
            displayUserMeetings(currentUserId);
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}


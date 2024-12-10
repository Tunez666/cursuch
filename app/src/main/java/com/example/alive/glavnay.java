package com.example.alive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class glavnay extends AppCompatActivity {
    private Button addb;
    private BottomNavigationView bottomNavigationView;
    private ListView meetingsListView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glavnay);

        addb = findViewById(R.id.add);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        meetingsListView = findViewById(R.id.meetingsListView);
        dbHelper = new DatabaseHelper(this);

        // Настройка нижней навигации
        setupBottomNavigation();

        // Существующие обработчики кнопок
        addb.setOnClickListener(v -> {
            Intent intent = new Intent(glavnay.this, create_m.class);
            startActivity(intent);
        });

        // Отображение ближайших встреч

    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Уже на главной странице
                return true;
            } else if (itemId == R.id.nav_create) {
                startActivity(new Intent(glavnay.this, create_m.class));
                return true;
            } else if (itemId == R.id.nav_friends) {
                startActivity(new Intent(glavnay.this, FriendsListActivity.class));
                return true;
            } else if (itemId == R.id.nav_md) {
                startActivity(new Intent(glavnay.this, md.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(glavnay.this, lk.class));
                return true;
            }
            return false;
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
    }
}


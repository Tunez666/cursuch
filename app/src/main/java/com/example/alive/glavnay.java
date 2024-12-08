package com.example.alive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class glavnay extends AppCompatActivity {
    private Button addb;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glavnay);

        addb = findViewById(R.id.add);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Настройка нижней навигации
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

        // Существующие обработчики кнопок
        addb.setOnClickListener(v -> {
            Intent intent = new Intent(glavnay.this, create_m.class);
            startActivity(intent);
        });
    }
}

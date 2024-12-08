package com.example.alive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class lk extends AppCompatActivity {
    private static final String TAG = "LkActivity";


    private FloatingActionButton settingsButton;
    private CircleImageView profileImageView;
    private TextView userNameTextView;
    private DatabaseHelper databaseHelper;
    private long userId;
    private BottomNavigationView bottomNavigationView;

    // Новый лаунчер для выбора изображения
    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        String imagePath = imageUri.toString();
                        databaseHelper.updateUserAvatar(userId, imagePath);
                        loadUserAvatar();
                        Toast.makeText(this, "Аватар обновлен", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lk);

        databaseHelper = new DatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);
        Log.d(TAG, "ID пользователя из SharedPreferences: " + userId);

        // Проверка на случай если userId не был найден в SharedPreferences
        if (userId == -1) {
            Toast.makeText(this, "Ошибка: ID пользователя не найден", Toast.LENGTH_SHORT).show();
            return; // Если ID не найден, не продолжать выполнение
        }



        profileImageView = findViewById(R.id.profileImageView);
        userNameTextView = findViewById(R.id.userNameTextView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        settingsButton = findViewById(R.id.settingsButton);

        loadUserName();
        loadUserAvatar();
        databaseHelper.logAllUsers();

        setupBottomNavigation();
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(lk.this, settings.class);
            startActivity(intent);
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(lk.this, glavnay.class));
                return true;
            } else if (itemId == R.id.nav_friends) {
                startActivity(new Intent(lk.this, FriendsListActivity.class));
                return true;
            } else if (itemId == R.id.nav_create) {
                startActivity(new Intent(lk.this, create_m.class));
                return true;
            } else if (itemId == R.id.nav_md) {
                startActivity(new Intent(lk.this, md.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Уже на странице профиля
                return true;
            }
            return false;
        });
    }


    private void loadUserAvatar() {
        String avatarPath = databaseHelper.getUserAvatar(userId);
        if (avatarPath != null && !avatarPath.isEmpty()) {
            Glide.with(this)
                    .load(Uri.parse(avatarPath))
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.default_avatar);
        }
    }

    private void loadUserName() {
        if (userId != -1) {
            String userName = databaseHelper.getUserName(userId);
            Log.d(TAG, "Имя пользователя из базы данных: " + userName);
            if (userName != null && !userName.isEmpty()) {
                userNameTextView.setText(userName);
            } else {
                userNameTextView.setText("Пользователь");
                Log.e(TAG, "Имя пользователя пустое или null");
            }
        } else {
            Log.e(TAG, "Неверный ID пользователя: " + userId);
            userNameTextView.setText("Пользователь");
        }
    }
}


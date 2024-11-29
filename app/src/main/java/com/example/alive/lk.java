package com.example.alive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class lk extends AppCompatActivity {
    private static final String TAG = "LkActivity";
    private Button dBut;
    private Button debug_m;
    private Button addFriendButton;
    private Button viewFriendsButton;
    private Button logoutButton;
    private Button changeAvatarButton;
    private CircleImageView profileImageView;
    private TextView userNameTextView;
    private DatabaseHelper databaseHelper;
    private long userId;

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

        dBut = findViewById(R.id.deug);
        debug_m = findViewById(R.id.d_m);
        addFriendButton = findViewById(R.id.addFriendButton);
        viewFriendsButton = findViewById(R.id.viewFriendsButton);
        logoutButton = findViewById(R.id.logoutButton);
        changeAvatarButton = findViewById(R.id.changeAvatarButton);
        profileImageView = findViewById(R.id.profileImageView);
        userNameTextView = findViewById(R.id.userNameTextView);

        loadUserName();
        loadUserAvatar();
        databaseHelper.logAllUsers();

        dBut.setOnClickListener(v -> {
            Intent intent = new Intent(lk.this, user_list.class);
            startActivity(intent);
        });

        debug_m.setOnClickListener(v -> {
            Intent intent = new Intent(lk.this, md.class);
            startActivity(intent);
        });

        addFriendButton.setOnClickListener(v -> {
            // Передаем userId в AddFriendActivity
            Intent intent = new Intent(lk.this, AddFriendActivity.class);
            intent.putExtra("user_id", userId); // Передаем ID пользователя
            startActivity(intent);
        });

        viewFriendsButton.setOnClickListener(v -> {
            Intent intent = new Intent(lk.this, FriendsListActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(view -> {
            SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(lk.this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(lk.this, Login.class);
            startActivity(intent);
            finish();
        });

        changeAvatarButton.setOnClickListener(v -> openGallery());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
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

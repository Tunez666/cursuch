package com.example.alive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class settings extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    private Button changeAvatarButton;
    private Button logoutButton;
    private Button saveChangesButton;
    private CircleImageView profileImageView;
    private TextView userNameTextView;
    private EditText newUsernameEditText;
    private EditText newPasswordEditText;
    private EditText newEmailEditText;

    private DatabaseHelper databaseHelper;
    private long userId;

    private Button backButton; // Added back button declaration

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
        setContentView(R.layout.activity_settings);

        databaseHelper = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        changeAvatarButton = findViewById(R.id.changeAvatarButton);
        logoutButton = findViewById(R.id.logoutButton);
        saveChangesButton = findViewById(R.id.saveChangesButton);
        profileImageView = findViewById(R.id.profileImageView);
        userNameTextView = findViewById(R.id.userNameTextView);
        newUsernameEditText = findViewById(R.id.newUsernameEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        newEmailEditText = findViewById(R.id.newEmailEditText);


        backButton = findViewById(R.id.backButton); // Added back button initialization

        loadUserName();
        loadUserAvatar();

        changeAvatarButton.setOnClickListener(v -> openGallery());



        logoutButton.setOnClickListener(view -> {
            SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(settings.this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(settings.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        saveChangesButton.setOnClickListener(v -> saveChanges());

        backButton.setOnClickListener(v -> { // Added back button click listener
            Intent intent = new Intent(settings.this, lk.class);
            startActivity(intent);
            finish();
        });
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

    private void saveChanges() {
        String newUsername = newUsernameEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String newEmail = newEmailEditText.getText().toString().trim(); // Добавляем строку для получения нового email

        boolean changes = false;

        if (!TextUtils.isEmpty(newUsername)) {
            databaseHelper.updateUsername(userId, newUsername);
            userNameTextView.setText(newUsername);
            changes = true;
        }

        if (!TextUtils.isEmpty(newPassword)) {
            databaseHelper.updatePassword(userId, newPassword);
            changes = true;
        }
        if (!TextUtils.isEmpty(newEmail)) {
            databaseHelper.updateEmail(userId, newEmail);
            changes = true;
            Toast.makeText(this, "Email успешно обновлен", Toast.LENGTH_SHORT).show();
        }
        if (changes) {
            Toast.makeText(this, "Изменения сохранены", Toast.LENGTH_SHORT).show();
            newUsernameEditText.setText("");
            newPasswordEditText.setText("");
        } else {
            Toast.makeText(this, "Нет изменений для сохранения", Toast.LENGTH_SHORT).show();
        }
    }
}


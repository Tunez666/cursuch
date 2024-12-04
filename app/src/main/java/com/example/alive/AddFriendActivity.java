package com.example.alive;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AddFriendActivity extends AppCompatActivity {

    private static final String TAG = "AddFriendActivity"; // Логирование
    private EditText usernameInput;
    private Button addFriendButton, cancelButton, clearB, searchButton;
    private TextView searchResult;

    private DatabaseHelper databaseHelper;
    private long currentUserId; // ID текущего пользователя

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        usernameInput = findViewById(R.id.usernameInput);
        addFriendButton = findViewById(R.id.addFriendButton);
        cancelButton = findViewById(R.id.cancelButton);
        clearB = findViewById(R.id.clearB);
        searchButton = findViewById(R.id.searchButton);
        searchResult = findViewById(R.id.searchResult);
        TextView currentUserIdView = findViewById(R.id.currentUserId);

        // Инициализация DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Получаем ID текущего пользователя из Intent
        currentUserId = getIntent().getLongExtra("user_id", -1);

        // Логирование для проверки значения currentUserId
        Log.d(TAG, "Получен ID пользователя: " + currentUserId);
        currentUserIdView.setText("Ваш ID: " + currentUserId);

        // Проверка на получение корректного ID пользователя
        if (currentUserId == -1) {
            Toast.makeText(this, "Ошибка: Не удалось получить ID пользователя", Toast.LENGTH_SHORT).show();
            finish(); // Закрываем активность, если ID не получен
            return;
        }

        // Обработчик для кнопки поиска
        searchButton.setOnClickListener(v -> {
            try {
                String inputId = usernameInput.getText().toString().trim();
                if (inputId.isEmpty()) {
                    Toast.makeText(AddFriendActivity.this, "Введите ID пользователя для поиска", Toast.LENGTH_SHORT).show();
                    return;
                }

                long friendId = Long.parseLong(inputId);
                String friendName = databaseHelper.getUserName(friendId);

                if (friendName != null) {
                    searchResult.setText("Найден пользователь: " + friendName);
                } else {
                    searchResult.setText("Пользователь с ID " + friendId + " не найден");
                }
            } catch (NumberFormatException e) {
                Toast.makeText(AddFriendActivity.this, "Введите корректный ID пользователя (число)", Toast.LENGTH_SHORT).show();
            }
        });


        // Обработчик для кнопки добавления друга
        addFriendButton.setOnClickListener(v -> {
            String inputId = usernameInput.getText().toString().trim();
            if (inputId.isEmpty()) {
                Toast.makeText(AddFriendActivity.this, "Введите ID пользователя для добавления", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                long friendId = Long.parseLong(inputId);
                if (friendId == currentUserId) {
                    Toast.makeText(AddFriendActivity.this, "Нельзя добавить себя в друзья", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseHelper.addFriend(currentUserId, friendId);
                Toast.makeText(AddFriendActivity.this, "Пользователь успешно добавлен в друзья", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(AddFriendActivity.this, "Введите корректный ID пользователя (число)", Toast.LENGTH_SHORT).show();
            }
        });

        // Обработчик для кнопки очистки друзей
        clearB.setOnClickListener(v -> {
            databaseHelper.clearFriends(currentUserId);
            Toast.makeText(AddFriendActivity.this, "Список друзей очищен", Toast.LENGTH_SHORT).show();
        });


        // Обработчик для кнопки отмены
        cancelButton.setOnClickListener(v -> {
            finish(); // Закрыть текущую активность
        });
    }

        // Метод для отображения информации о пользователе
    private void displayUserInfo(User user) {
        String userInfo = "ID: " + user.getId() + "\nИмя: " + user.getUsername() + "\nEmail: " + user.getEmail();
        searchResult.setText(userInfo);
    }
}

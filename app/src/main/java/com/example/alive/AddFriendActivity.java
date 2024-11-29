package com.example.alive;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AddFriendActivity extends AppCompatActivity {

    private static final String TAG = "AddFriendActivity"; // Логирование
    private EditText usernameInput;
    private Button addFriendButton, cancelButton, clearB;
    private DatabaseHelper databaseHelper;
    private long currentUserId; // ID текущего пользователя

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        usernameInput = findViewById(R.id.usernameInput);
        addFriendButton = findViewById(R.id.addFriendButton);
        cancelButton = findViewById(R.id.cancelButton);
        clearB = findViewById(R.id.clearB);

        // Инициализация DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Получаем ID текущего пользователя из Intent
        currentUserId = getIntent().getLongExtra("user_id", -1);

        // Логирование для проверки значения currentUserId
        Log.d(TAG, "Получен ID пользователя: " + currentUserId);

        // Проверка на получение корректного ID пользователя
        if (currentUserId == -1) {
            Toast.makeText(this, "Ошибка: Не удалось получить ID пользователя", Toast.LENGTH_SHORT).show();
            finish(); // Закрываем активность, если ID не получен
            return;
        }

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString().trim();

                if (username.isEmpty()) {
                    Toast.makeText(AddFriendActivity.this, "Введите имя пользователя", Toast.LENGTH_SHORT).show();
                } else {
                    long friendId = databaseHelper.getUserIdByUsername(username);
                    if (friendId != -1 && friendId != currentUserId) {
                        // Добавляем друга в базу данных
                        databaseHelper.addFriend(currentUserId, friendId);
                        Toast.makeText(AddFriendActivity.this, "Друг добавлен", Toast.LENGTH_SHORT).show();
                        finish(); // Закрываем текущую активность
                    } else {
                        Toast.makeText(AddFriendActivity.this, "Пользователь не найден или это ваш ID", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Обработчик для кнопки "Очистить список друзей"
        clearB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUserId != -1) {
                    databaseHelper.clearFriends(currentUserId); // Очистить список друзей
                    Toast.makeText(AddFriendActivity.this, "Список друзей очищен", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddFriendActivity.this, "Ошибка: Не удалось получить ID пользователя", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Обработчик для кнопки "Отмена"
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Закрываем текущую активность
            }
        });
    }
}

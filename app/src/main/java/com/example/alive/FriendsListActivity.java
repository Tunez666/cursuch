package com.example.alive;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class FriendsListActivity extends AppCompatActivity {

    private ListView friendsListView;
    private Button backButton;
    private DatabaseHelper databaseHelper;
    private long currentUserId; // ID текущего пользователя

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        friendsListView = findViewById(R.id.friendsListView);
        backButton = findViewById(R.id.backButton);

        // Инициализация DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Получаем ID текущего пользователя
        currentUserId = getCurrentUserId();

        if (currentUserId == -1) {
            Toast.makeText(this, "Ошибка: Не удалось получить ID пользователя", Toast.LENGTH_SHORT).show();
            Log.e("FriendsListActivity", "Не удалось получить ID пользователя");
            finish(); // Закрываем активность, если ID не получен
            return;
        }

        Log.d("FriendsListActivity", "Получен ID пользователя: " + currentUserId);

        // Получаем список друзей
        List<String> friendsList = databaseHelper.getFriends(currentUserId);

        // Проверка на пустой список
        if (friendsList.isEmpty()) {
            Toast.makeText(this, "У вас нет друзей", Toast.LENGTH_SHORT).show();
        }

        // Отображаем список друзей в ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friendsList);
        friendsListView.setAdapter(adapter);

        // Кнопка назад
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Метод для получения текущего ID пользователя из SharedPreferences.
     */
    private long getCurrentUserId() {
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE); // Используем правильный ключ
        return preferences.getLong("userId", -1); // Получаем сохраненный ID
    }
}

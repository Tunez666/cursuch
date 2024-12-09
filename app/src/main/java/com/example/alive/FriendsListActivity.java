package com.example.alive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class FriendsListActivity extends AppCompatActivity {

    private ListView friendsListView;
    private Button addFriendButton;
    private DatabaseHelper databaseHelper;
    private long currentUserId;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        // Инициализация UI элементов
        friendsListView = findViewById(R.id.friendsListView);
        addFriendButton = findViewById(R.id.addFriendButton);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Инициализация базы данных
        databaseHelper = new DatabaseHelper(this);

        // Настройка кнопки добавления друга
        addFriendButton.setOnClickListener(v -> {
            Intent intent = new Intent(FriendsListActivity.this, AddFriendActivity.class);
            intent.putExtra("user_id", currentUserId); // Передаем userId
            startActivity(intent);
        });

        // Настройка навигации
        setupBottomNavigation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Извлекаем userId в onStart() и проверяем его
        currentUserId = getCurrentUserId();
        Log.d("FriendsListActivity", "Получен ID пользователя в onStart: " + currentUserId);

        if (currentUserId == -1) {
            Toast.makeText(this, "Ошибка: Не удалось получить ID пользователя", Toast.LENGTH_SHORT).show();
            Log.e("FriendsListActivity", "Не удалось получить ID пользователя");
            // Перенаправляем на экран входа, если ID не найден
            Intent intent = new Intent(FriendsListActivity.this, Login.class);
            startActivity(intent);
            finish();
            return;
        }

        loadFriendsList(); // Загружаем список друзей
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFriendsList(); // Загружаем список друзей снова при возвращении на экран
    }

    private void loadFriendsList() {
        List<String> friendsList = databaseHelper.getFriends(currentUserId); // Получаем список друзей из базы данных

        if (friendsList.isEmpty()) {
            Toast.makeText(this, "У вас нет друзей", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friendsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Получаем View для текущего элемента списка
                View view = super.getView(position, convertView, parent);

                // Ищем TextView, который отображает текст в элементе списка
                TextView textView = view.findViewById(android.R.id.text1);

                // Меняем цвет текста на черный
                textView.setTextColor(Color.BLACK);

                // Возвращаем измененный view
                return view;
            }
        };
        friendsListView.setAdapter(adapter);
    } // Устанавливаем адаптер для ListView


    // Получаем userId из SharedPreferences
    private long getCurrentUserId() {
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        long userId = preferences.getLong("userId", -1);
        Log.d("FriendsListActivity", "Получен userId из SharedPreferences: " + userId); // Логируем userId
        return userId;
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                if (currentUserId == -1) {
                    Toast.makeText(FriendsListActivity.this, "Ошибка: Не удалось получить ID пользователя", Toast.LENGTH_SHORT).show();
                    return false;
                }
                startActivity(new Intent(FriendsListActivity.this, glavnay.class));
                return true;
            } else if (itemId == R.id.nav_friends) {
                // Уже на странице друзей
                return true;
            } else if (itemId == R.id.nav_create) {
                if (currentUserId == -1) {
                    Toast.makeText(FriendsListActivity.this, "Ошибка: Не удалось получить ID пользователя", Toast.LENGTH_SHORT).show();
                    return false;
                }
                startActivity(new Intent(FriendsListActivity.this, create_m.class));
                return true;
            }
            // Добавьте другие пункты меню аналогично
            return false;
        });
    }
}

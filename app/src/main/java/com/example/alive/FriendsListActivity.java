package com.example.alive;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

        // Получаем ID текущего пользователя из Intent
        currentUserId = getIntent().getLongExtra("user_id", -1);

        // Получаем список друзей
        List<String> friendsList = databaseHelper.getFriends(currentUserId);

        // Отображаем список друзей в ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friendsList);
        friendsListView.setAdapter(adapter);

        backButton.setOnClickListener(v -> finish()); // Закрываем активность
    }
}

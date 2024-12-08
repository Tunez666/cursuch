package com.example.alive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

        friendsListView = findViewById(R.id.friendsListView);
        addFriendButton = findViewById(R.id.addFriendButton);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        databaseHelper = new DatabaseHelper(this);
        currentUserId = getCurrentUserId();

        if (currentUserId == -1) {
            Toast.makeText(this, "Ошибка: Не удалось получить ID пользователя", Toast.LENGTH_SHORT).show();
            Log.e("FriendsListActivity", "Не удалось получить ID пользователя");
            finish();
            return;
        }

        Log.d("FriendsListActivity", "Получен ID пользователя: " + currentUserId);

        loadFriendsList();

        addFriendButton.setOnClickListener(v -> {
            Intent intent = new Intent(FriendsListActivity.this, AddFriendActivity.class);
            intent.putExtra("user_id", currentUserId);
            startActivity(intent);
        });

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFriendsList();
    }

    private void loadFriendsList() {
        List<String> friendsList = databaseHelper.getFriends(currentUserId);

        if (friendsList.isEmpty()) {
            Toast.makeText(this, "У вас нет друзей", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friendsList);
        friendsListView.setAdapter(adapter);
    }

    private long getCurrentUserId() {
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return preferences.getLong("userId", -1);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(FriendsListActivity.this, glavnay.class));
                return true;
            } else if (itemId == R.id.nav_friends) {
                // Уже на странице друзей
                return true;
            } else if (itemId == R.id.nav_create) {
                startActivity(new Intent(FriendsListActivity.this, create_m.class));
                return true;
            } else if (itemId == R.id.nav_md) {
                startActivity(new Intent(FriendsListActivity.this, md.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(FriendsListActivity.this, lk.class));
                return true;
            }
            return false;
        });
    }
}


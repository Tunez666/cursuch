package com.example.alive;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class FriendsListActivity extends AppCompatActivity {
    private ListView friendsListView;
    private Button backButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        friendsListView = findViewById(R.id.friendsListView);
        backButton = findViewById(R.id.backButton);

        dbHelper = new DatabaseHelper(this);

        loadFriendsList();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Возвращаемся на предыдущую страницу
            }
        });
    }

    private void loadFriendsList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> friends = new ArrayList<>();

        int currentUserId = getCurrentUserId();
        Cursor cursor = db.rawQuery(
                "SELECT u." + DatabaseHelper.COLUMN_USERNAME +
                        " FROM " + DatabaseHelper.TABLE_FRIENDS + " f " +
                        " JOIN " + DatabaseHelper.TABLE_USERS + " u " +
                        " ON f." + DatabaseHelper.COLUMN_FRIEND_ID + " = u." + DatabaseHelper.COLUMN_ID +
                        " WHERE f." + DatabaseHelper.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(currentUserId)}
        );

        if (cursor.moveToFirst()) {
            do {
                String friendName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME));
                friends.add(friendName);
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "У вас пока нет друзей", Toast.LENGTH_SHORT).show();
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                friends
        );
        friendsListView.setAdapter(adapter);
    }

    private int getCurrentUserId() {
        // Здесь предполагается, что ID текущего пользователя берётся из SharedPreferences или другого источника
        // Для примера, возвращаем заглушку
        return 1;
    }
}

package com.example.alive;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddFriendActivity extends AppCompatActivity {
    private EditText usernameInput;
    private Button addFriendButton;
    private Button cancelButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        usernameInput = findViewById(R.id.usernameInput);
        addFriendButton = findViewById(R.id.addFriendButton);
        cancelButton = findViewById(R.id.cancelButton);

        dbHelper = new DatabaseHelper(this);

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString().trim();
                if (username.isEmpty()) {
                    Toast.makeText(AddFriendActivity.this, "Введите имя пользователя", Toast.LENGTH_SHORT).show();
                } else {
                    addFriend(username);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Закрыть активность
            }
        });
    }

    private void addFriend(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS,
                new String[]{DatabaseHelper.COLUMN_ID},
                DatabaseHelper.COLUMN_USERNAME + "=?",
                new String[]{username},
                null, null, null);

        if (cursor.moveToFirst()) {
            int friendId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            cursor.close();

            // Проверяем, нет ли уже дружбы
            db = dbHelper.getReadableDatabase();
            Cursor friendCursor = db.query(DatabaseHelper.TABLE_FRIENDS,
                    null,
                    DatabaseHelper.COLUMN_USER_ID + "=? AND " + DatabaseHelper.COLUMN_FRIEND_ID + "=?",
                    new String[]{String.valueOf(getCurrentUserId()), String.valueOf(friendId)},
                    null, null, null);

            if (friendCursor.getCount() > 0) {
                Toast.makeText(this, "Этот пользователь уже у вас в друзьях", Toast.LENGTH_SHORT).show();
                friendCursor.close();
                return;
            }
            friendCursor.close();

            // Добавляем друга
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USER_ID, getCurrentUserId());
            values.put(DatabaseHelper.COLUMN_FRIEND_ID, friendId);

            db = dbHelper.getWritableDatabase();
            long result = db.insert(DatabaseHelper.TABLE_FRIENDS, null, values);
            if (result != -1) {
                Toast.makeText(this, "Друг успешно добавлен!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Ошибка добавления друга", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Пользователь с таким именем не найден", Toast.LENGTH_SHORT).show();
            cursor.close();
        }
    }

    private int getCurrentUserId() {
        // Здесь предполагается, что ID текущего пользователя берётся из SharedPreferences или другого источника
        // Для примера, возвращаем заглушку
        return 1;
    }
}

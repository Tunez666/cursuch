package com.example.alive;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class user_list extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView usersListTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        usersListTextView = findViewById(R.id.usersList);
        dbHelper = new DatabaseHelper(this);

        displayUsers();
    }

    private void displayUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Запрос данных пользователей
        String[] columns = {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_USERNAME,
                DatabaseHelper.COLUMN_EMAIL
        };
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, columns, null, null, null, null, DatabaseHelper.COLUMN_ID + " ASC");

        if (cursor != null && cursor.getCount() > 0) {
            StringBuilder users = new StringBuilder();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL));

                users.append("👤 ID: ").append(id)
                        .append("\nИмя: ").append(username)
                        .append("\nEmail: ").append(email)
                        .append("\n\n-------------------\n\n");
            }
            usersListTextView.setText(users.toString());
            cursor.close();
        } else {
            usersListTextView.setText("Нет зарегистрированных пользователей.");
            Toast.makeText(this, "Список пользователей пуст", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}

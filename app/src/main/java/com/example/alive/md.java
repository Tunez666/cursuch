package com.example.alive;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class md extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView detailsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_md);

        detailsTextView = findViewById(R.id.detailsTextView);
        dbHelper = new DatabaseHelper(this);

        displayAllMeetingDetails();
    }

    private void displayAllMeetingDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Запрос для получения всех данных встреч
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_MEET;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.getCount() > 0) {
            StringBuilder details = new StringBuilder();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_M));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME));
                String place = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLACE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DECS));

                details.append("ID: ").append(id)
                        .append("\nНазвание: ").append(name)
                        .append("\nДата: ").append(date)
                        .append("\nВремя: ").append(time)
                        .append("\nМесто: ").append(place)
                        .append("\nОписание: ").append(description)
                        .append("\n\n-------------------\n\n");
            }
            detailsTextView.setText(details.toString());
            cursor.close();
        } else {
            detailsTextView.setText("Нет данных о встречах.");
            Toast.makeText(this, "Данные о встречах отсутствуют", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}

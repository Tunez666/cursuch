// File: MeetingsListActivity.java
package com.example.alive;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class MeetingsListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView listViewMeetings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetings_list);

        dbHelper = new DatabaseHelper(this);
        listViewMeetings = findViewById(R.id.listViewMeetings);

        displayMeetings();
    }

    private void displayMeetings() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseHelper.COLUMN_ID_M,
                DatabaseHelper.COLUMN_NAME,
                DatabaseHelper.COLUMN_DATE,
                DatabaseHelper.COLUMN_TIME,
                DatabaseHelper.COLUMN_PLACE
        };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_MEET,
                projection,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_DATE + " ASC"
        );

        String[] fromColumns = {
                DatabaseHelper.COLUMN_NAME,
                DatabaseHelper.COLUMN_DATE,
                DatabaseHelper.COLUMN_TIME,
                DatabaseHelper.COLUMN_PLACE
        };

        int[] toViews = {
                R.id.textViewName,
                R.id.textViewDate,
                R.id.textViewTime,
                R.id.textViewPlace
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.meeting_list_item,
                cursor,
                fromColumns,
                toViews,
                0
        );

        listViewMeetings.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
package com.example.alive;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 2;  // Увеличиваем версию базы данных для обновления

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    public static final String TABLE_MEET = "meet";
    public static final String COLUMN_ID_M = "id_m";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_PLACE = "place";
    public static final String COLUMN_DECS = "descr";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_USERS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_USERNAME
            + " text not null, " + COLUMN_EMAIL
            + " text not null, " + COLUMN_PASSWORD
            + " text not null);";

    private static final String DATABASE_CREATE_M = "create table "
            + TABLE_MEET + "(" + COLUMN_ID_M
            + " integer primary key autoincrement, " + COLUMN_NAME
            + " text not null, " + COLUMN_DATE
            + " text not null, " + COLUMN_TIME
            + " text not null, " + COLUMN_PLACE
            + " text not null, " + COLUMN_DECS
            + " text not null);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        try {
            database.execSQL(DATABASE_CREATE);
            database.execSQL(DATABASE_CREATE_M);
            Log.i("DatabaseInfo", "Tables created successfully");
        } catch (Exception e) {
            Log.e("DatabaseError", "Error creating tables: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEET);
        onCreate(db);
    }

    // Метод для проверки существования таблицы "meet"
    public boolean isMeetTableExists() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_MEET + "'", null);
        boolean tableExists = cursor.getCount() > 0;
        cursor.close();
        if (tableExists) {
            Log.d("DatabaseCheck", "Таблица 'meet' найдена");
        } else {
            Log.e("DatabaseCheck", "Таблица 'meet' не существует");
        }
        return tableExists;
    }
}

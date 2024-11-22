package com.example.alive;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 4; // Увеличиваем версию базы данных

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_AVATAR = "avatar"; // Новый столбец для аватара

    public static final String TABLE_MEET = "meet";
    public static final String COLUMN_ID_M = "id_m";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_PLACE = "place";
    public static final String COLUMN_DECS = "descr";

    public static final String TABLE_FRIENDS = "friends";
    public static final String COLUMN_FRIENDSHIP_ID = "friendship_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_FRIEND_ID = "friend_id";

    private static final String DATABASE_CREATE_USERS = "CREATE TABLE "
            + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USERNAME + " TEXT NOT NULL, "
            + COLUMN_EMAIL + " TEXT NOT NULL, "
            + COLUMN_PASSWORD + " TEXT NOT NULL, "
            + COLUMN_AVATAR + " TEXT);"; // Добавляем столбец для аватара

    private static final String DATABASE_CREATE_MEET = "CREATE TABLE "
            + TABLE_MEET + "("
            + COLUMN_ID_M + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_DATE + " TEXT NOT NULL, "
            + COLUMN_TIME + " TEXT NOT NULL, "
            + COLUMN_PLACE + " TEXT NOT NULL, "
            + COLUMN_DECS + " TEXT NOT NULL);";

    private static final String DATABASE_CREATE_FRIENDS = "CREATE TABLE "
            + TABLE_FRIENDS + "("
            + COLUMN_FRIENDSHIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USER_ID + " INTEGER NOT NULL, "
            + COLUMN_FRIEND_ID + " INTEGER NOT NULL, "
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "), "
            + "FOREIGN KEY(" + COLUMN_FRIEND_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        try {
            database.execSQL(DATABASE_CREATE_USERS);
            database.execSQL(DATABASE_CREATE_MEET);
            database.execSQL(DATABASE_CREATE_FRIENDS);
            Log.i("DatabaseInfo", "Tables created successfully");
        } catch (Exception e) {
            Log.e("DatabaseError", "Error creating tables: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("DatabaseUpgrade", "Upgrading database from version " + oldVersion + " to " + newVersion);
        if (oldVersion < 3) {
            db.execSQL(DATABASE_CREATE_FRIENDS);
            Log.i("DatabaseUpgrade", "Table 'friends' created");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_AVATAR + " TEXT;");
            Log.i("DatabaseUpgrade", "Added avatar column to users table");
        }
    }

    // Метод для проверки существования таблицы
    public boolean isTableExists(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'", null);
        boolean tableExists = cursor.getCount() > 0;
        cursor.close();
        return tableExists;
    }

    // Метод для обновления аватара пользователя
    public void updateUserAvatar(long userId, String avatarPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_USERS + " SET " + COLUMN_AVATAR + " = ? WHERE " + COLUMN_ID + " = ?",
                new String[]{avatarPath, String.valueOf(userId)});
    }

    // Метод для получения пути к аватару пользователя
    public String getUserAvatar(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_AVATAR + " FROM " + TABLE_USERS + " WHERE " + COLUMN_ID + " = ?",
                new String[]{String.valueOf(userId)});
        String avatarPath = null;
        if (cursor.moveToFirst()) {
            avatarPath = cursor.getString(0);
        }
        cursor.close();
        return avatarPath;
    }
}


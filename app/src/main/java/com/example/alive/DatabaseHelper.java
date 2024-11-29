package com.example.alive;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_AVATAR = "avatar";

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
            + COLUMN_AVATAR + " TEXT);";

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
            Log.i(TAG, "Таблицы успешно созданы");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при создании таблиц: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Обновление базы данных с версии " + oldVersion + " до " + newVersion);
        if (oldVersion < 3) {
            db.execSQL(DATABASE_CREATE_FRIENDS);
            Log.i(TAG, "Таблица 'friends' создана");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_AVATAR + " TEXT;");
            Log.i(TAG, "Добавлен столбец avatar в таблицу users");
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Понижение версии базы данных с " + oldVersion + " до " + newVersion + " не поддерживается.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void updateUserAvatar(long userId, String avatarPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AVATAR, avatarPath);
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
        Log.d(TAG, "Обновлен аватар для пользователя " + userId + ". Затронуто строк: " + rowsAffected);
    }

    @SuppressLint("Range")
    public String getUserAvatar(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String avatarPath = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS, new String[]{COLUMN_AVATAR},
                    COLUMN_ID + "=?", new String[]{String.valueOf(userId)},
                    null, null, null);
            if (cursor.moveToFirst()) {
                avatarPath = cursor.getString(cursor.getColumnIndex(COLUMN_AVATAR));
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при получении аватара пользователя: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        Log.d(TAG, "Получен аватар для пользователя " + userId + ": " + avatarPath);
        return avatarPath;
    }

    @SuppressLint("Range")
    public String getUserName(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String userName = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS, new String[]{COLUMN_USERNAME},
                    COLUMN_ID + "=?", new String[]{String.valueOf(userId)},
                    null, null, null);
            if (cursor.moveToFirst()) {
                userName = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при получении имени пользователя: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        Log.d(TAG, "Получено имя пользователя для ID " + userId + ": " + userName);
        return userName;
    }

    public void logAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID, COLUMN_USERNAME, COLUMN_EMAIL},
                null, null, null, null, null);

        Log.d(TAG, "Все пользователи в базе данных:");
        while (cursor.moveToNext()) {
            @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
            @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));
            Log.d(TAG, "Пользователь: ID=" + id + ", Имя=" + name + ", Email=" + email);
        }
        cursor.close();
    }

    public long getUserIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") long userId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
            cursor.close();
            return userId;
        }
        return -1; // Пользователь не найден
    }

    // Метод для добавления друга
    public void addFriend(long userId, long friendId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Добавляем друга для userId
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_FRIEND_ID, friendId);
        long result1 = db.insert(TABLE_FRIENDS, null, values);

        // Добавляем userId для friendId
        values.put(COLUMN_USER_ID, friendId);
        values.put(COLUMN_FRIEND_ID, userId);
        long result2 = db.insert(TABLE_FRIENDS, null, values);

        if (result1 == -1 || result2 == -1) {
            Log.e(TAG, "Не удалось добавить друга");
        } else {
            Log.i(TAG, "Друг успешно добавлен");
        }
    }


    // Метод для получения списка друзей
    public List<String> getFriends(long userId) {
        List<String> friendsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_USERNAME + " FROM " + TABLE_USERS +
                " INNER JOIN " + TABLE_FRIENDS + " ON " + TABLE_USERS + "." + COLUMN_ID +
                " = " + TABLE_FRIENDS + "." + COLUMN_FRIEND_ID +
                " WHERE " + TABLE_FRIENDS + "." + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        Log.d(TAG, "Запрос выполнен: " + query);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String friendName = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
            friendsList.add(friendName);
            Log.d(TAG, "Друг: " + friendName); // Логирование каждого имени друга
        }
        cursor.close();
        return friendsList;
    }
    public void clearFriends(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Удаляем все записи из таблицы друзей для конкретного пользователя
        int rowsDeleted = db.delete(TABLE_FRIENDS, COLUMN_USER_ID + " = ? OR " + COLUMN_FRIEND_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(userId)});

        if (rowsDeleted > 0) {
            Log.i(TAG, "Список друзей успешно очищен");
        } else {
            Log.e(TAG, "Не удалось очистить список друзей");
        }
    }

    public Boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE username = ? AND password = ?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}

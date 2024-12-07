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
    private static final int DATABASE_VERSION = 5; // Увеличиваем версию базы данных

    // Константы для таблиц и колонок
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
    public static final String COLUMN_DESC = "descr"; // Исправлено название константы
    public static final String COLUMN_USER_ID = "user_id";

    public static final String TABLE_FRIENDS = "friends";
    public static final String COLUMN_FRIENDSHIP_ID = "friendship_id";
    public static final String COLUMN_USER_ID_FRIENDS = "user_id"; // Added to avoid naming conflict
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
            + COLUMN_DATE + " INTEGER NOT NULL, "
            + COLUMN_TIME + " TEXT NOT NULL, "
            + COLUMN_PLACE + " TEXT NOT NULL, "
            + COLUMN_DESC + " TEXT NOT NULL, "
            + COLUMN_USER_ID + " INTEGER NOT NULL, "
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "));"; // Updated to include user_id

    private static final String DATABASE_CREATE_FRIENDS = "CREATE TABLE "
            + TABLE_FRIENDS + "("
            + COLUMN_FRIENDSHIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USER_ID_FRIENDS + " INTEGER NOT NULL, "
            + COLUMN_FRIEND_ID + " INTEGER NOT NULL, "
            + "FOREIGN KEY(" + COLUMN_USER_ID_FRIENDS + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "), "
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
        if (oldVersion < 5) {
            // Добавляем колонку user_id в таблицу meet
            db.execSQL("ALTER TABLE " + TABLE_MEET + " ADD COLUMN " + COLUMN_USER_ID + " INTEGER DEFAULT 0;");
            Log.i(TAG, "Добавлен столбец user_id в таблицу meet");
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

    public User getUserById(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        try (Cursor cursor = db.rawQuery("SELECT " + COLUMN_ID + ", " + COLUMN_USERNAME + ", " + COLUMN_EMAIL +
                " FROM " + TABLE_USERS + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(userId)})) {
            if (cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
                user = new User(id, username, email);
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка выполнения запроса", e);
        }
        return user;
    }

    // Метод для добавления друга
    public void addFriend(long userId, long friendId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Проверка на уникальность
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FRIENDS +
                        " WHERE (" + COLUMN_USER_ID_FRIENDS + " = ? AND " + COLUMN_FRIEND_ID + " = ?) " +
                        "OR (" + COLUMN_USER_ID_FRIENDS + " = ? AND " + COLUMN_FRIEND_ID + " = ?)",
                new String[]{String.valueOf(userId), String.valueOf(friendId),
                        String.valueOf(friendId), String.valueOf(userId)});
        if (cursor.getCount() > 0) {
            Log.e(TAG, "Такая связь уже существует");
            cursor.close();
            return;
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_FRIENDS, userId);
        values.put(COLUMN_FRIEND_ID, friendId);
        long result = db.insert(TABLE_FRIENDS, null, values);

        if (result == -1) {
            Log.e(TAG, "Не удалось добавить друга");
        } else {
            Log.i(TAG, "Друг успешно добавлен");
        }
    }
    public void addMeet(String name, long timestamp, String time, String place, String description, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DATE, timestamp);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_PLACE, place);
        values.put(COLUMN_DESC, description);
        values.put(COLUMN_USER_ID, userId);
        long result = db.insert(TABLE_MEET, null, values);
        if (result == -1) {
            Log.e(TAG, "Не удалось добавить встречу");
        } else {
            Log.i(TAG, "Встреча успешно добавлена");
        }
    }
    public String getFormattedDate(long timestamp) {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
        return format.format(new java.util.Date(timestamp * 1000)); // Умножаем на 1000 для миллисекунд
    }


    // Метод для получения списка друзей
    public List<String> getFriends(long userId) {
        List<String> friends = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Запрос для получения имен друзей
        String query = "SELECT u." + COLUMN_USERNAME + " FROM " + TABLE_FRIENDS + " f " +
                "JOIN " + TABLE_USERS + " u ON f." + COLUMN_FRIEND_ID + " = u." + COLUMN_ID +
                " WHERE f." + COLUMN_USER_ID_FRIENDS + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                String friendName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                friends.add(friendName);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return friends;
    }



    public void clearFriends(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Удаляем все записи из таблицы друзей для конкретного пользователя
        int rowsDeleted = db.delete(TABLE_FRIENDS, COLUMN_USER_ID_FRIENDS + " = ? OR " + COLUMN_FRIEND_ID + " = ?",
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

    public void updateUsername(long userId, String newUsername) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, newUsername);
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
        Log.d(TAG, "Обновлено имя пользователя для ID " + userId + ". Затронуто строк: " + rowsAffected);
    }

    public void updatePassword(long userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
        Log.d(TAG, "Обновлен пароль для пользователя с ID " + userId + ". Затронуто строк: " + rowsAffected);
    }

    public Cursor getMeetingsForUser(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_MEET + " WHERE " + COLUMN_USER_ID + " = ? OR " + COLUMN_USER_ID + " IS NULL OR " + COLUMN_USER_ID + " = 0 ORDER BY " + COLUMN_DATE + " DESC";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }
    public void clearMeetings(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEET, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }
}


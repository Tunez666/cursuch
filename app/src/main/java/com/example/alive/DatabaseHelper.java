package com.example.alive;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 10; // Увеличиваем версию базы данных

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

    public static final String TABLE_CATEGORY = "category";
    public static final String COLUMN_ID_C = "id_c";
    public static final String COLUMN_CATEGORY_NAME = "name_c";

    public static final String TABLE_EVENT = "event";
    public static final String COLUMN_ID_E = "id_e";
    public static final String COLUMN_EVENT_NAME = "name_e";
    public static final String TABLE_MEET_PARTICIPANTS = "meet_participants";
    public static final String COLUMN_MEET_ID = "meet_id";
    public static final String COLUMN_PARTICIPANT_ID = "participant_id";
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
            + COLUMN_ID_C + " INTEGER NOT NULL, "
            + COLUMN_ID_E + " INTEGER NOT NULL, "
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "), "
            + "FOREIGN KEY(" + COLUMN_ID_C + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_ID_C + "), "
            + "FOREIGN KEY(" + COLUMN_ID_E + ") REFERENCES " + TABLE_EVENT + "(" + COLUMN_ID_E + "));";
    // Updated to include user_id

    private static final String DATABASE_CREATE_FRIENDS = "CREATE TABLE "
            + TABLE_FRIENDS + "("
            + COLUMN_FRIENDSHIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USER_ID_FRIENDS + " INTEGER NOT NULL, "
            + COLUMN_FRIEND_ID + " INTEGER NOT NULL, "
            + "FOREIGN KEY(" + COLUMN_USER_ID_FRIENDS + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "), "
            + "FOREIGN KEY(" + COLUMN_FRIEND_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "));";
    private static final String DATABASE_CREATE_CATEGORY = "CREATE TABLE "
            + TABLE_CATEGORY + "("
            + COLUMN_ID_C + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CATEGORY_NAME + " TEXT NOT NULL);";

    private static final String DATABASE_CREATE_EVENT = "CREATE TABLE "
            + TABLE_EVENT + "("
            + COLUMN_ID_E + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_EVENT_NAME+ " TEXT NOT NULL);";

    private static final String DATABASE_CREATE_MEET_PARTICIPANTS = "CREATE TABLE "
            + TABLE_MEET_PARTICIPANTS + "("
            + COLUMN_MEET_ID + " INTEGER, "
            + COLUMN_PARTICIPANT_ID + " INTEGER, "
            + "PRIMARY KEY (" + COLUMN_MEET_ID + ", " + COLUMN_PARTICIPANT_ID + "), "
            + "FOREIGN KEY(" + COLUMN_MEET_ID + ") REFERENCES " + TABLE_MEET + "(" + COLUMN_ID_M + "), "
            + "FOREIGN KEY(" + COLUMN_PARTICIPANT_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "));";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("PRAGMA foreign_keys = ON;");
        try {
            database.execSQL(DATABASE_CREATE_USERS);
            database.execSQL(DATABASE_CREATE_MEET);
            database.execSQL(DATABASE_CREATE_FRIENDS);
            database.execSQL(DATABASE_CREATE_CATEGORY);
            database.execSQL(DATABASE_CREATE_EVENT);
            database.execSQL(DATABASE_CREATE_MEET_PARTICIPANTS);
            Log.i(TAG, "Таблицы успешно созданы");

            initializeCategories(database);
            initializeEvents(database);

            Log.i(TAG, "Категории и события успешно добавлены");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при создании таблиц или добавлении данных: " + e.getMessage());
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
            db.execSQL("ALTER TABLE " + TABLE_MEET + " ADD COLUMN " + COLUMN_USER_ID + " INTEGER DEFAULT 0;");
            Log.i(TAG, "Добавлен столбец user_id в таблицу meet");
        }
        if (oldVersion < 6) {
            db.execSQL(DATABASE_CREATE_CATEGORY);
            db.execSQL(DATABASE_CREATE_EVENT);
            Log.i(TAG, "Таблицы 'category' и 'event' созданы");
        }
        if (oldVersion < 7) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEET);
            db.execSQL(DATABASE_CREATE_MEET);
            Log.i(TAG, "Таблица 'meet' обновлена с внешними ключами.");
        }
        if (oldVersion < 8) {
            initializeCategories(db);
            initializeEvents(db);
            Log.i(TAG, "Категории и события успешно добавлены");
        }
        if (oldVersion < 9) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEET);
            db.execSQL(DATABASE_CREATE_MEET);
            initializeCategories(db);
            initializeEvents(db);
        }
        if (oldVersion < 10) {
            // Add upgrade logic for version 10 here if needed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEET_PARTICIPANTS);
            db.execSQL(DATABASE_CREATE_MEET_PARTICIPANTS);
            Log.i(TAG, "Создана таблица участников встреч");
            Log.i(TAG, "Обновление до версии 10");
        }
    }


    private void initializeCategories(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + TABLE_CATEGORY + " (" + COLUMN_CATEGORY_NAME + ") VALUES ('Рабочая');");
        db.execSQL("INSERT INTO " + TABLE_CATEGORY + " (" + COLUMN_CATEGORY_NAME + ") VALUES ('Дружеская');");
        db.execSQL("INSERT INTO " + TABLE_CATEGORY + " (" + COLUMN_CATEGORY_NAME + ") VALUES ('Семейная');");
    }

    private void initializeEvents(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + TABLE_EVENT + " (" + COLUMN_EVENT_NAME + ") VALUES ('День рождения');");
        db.execSQL("INSERT INTO " + TABLE_EVENT + " (" + COLUMN_EVENT_NAME + ") VALUES ('Прогулка');");
        db.execSQL("INSERT INTO " + TABLE_EVENT + " (" + COLUMN_EVENT_NAME + ") VALUES ('Свидание');");
        db.execSQL("INSERT INTO " + TABLE_EVENT + " (" + COLUMN_EVENT_NAME + ") VALUES ('Путешествие');");
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Понижение версии базы данных с " + oldVersion + " до " + newVersion + " не поддерживается.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        onCreate(db);
    }
    public long addCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, name);
        return db.insert(TABLE_CATEGORY, null, values);
    }

    public long addEvent(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENT_NAME, title);
        return db.insert(TABLE_EVENT, null, values);
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

    public void addMeetParticipants(long meetId, List<Long> participantIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Long participantId : participantIds) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_MEET_ID, meetId);
                values.put(COLUMN_PARTICIPANT_ID, participantId);
                long result = db.insert(TABLE_MEET_PARTICIPANTS, null, values);
                if (result == -1) {
                    Log.e(TAG, "Не удалось добавить участника " + participantId + " к встрече " + meetId);
                } else {
                    Log.d(TAG, "Успешно добавлен участник " + participantId + " к встрече " + meetId);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при добавлении участников встречи: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
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
        db.beginTransaction();
        try {
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

            // Добавляем связь в обе стороны
            ContentValues values1 = new ContentValues();
            values1.put(COLUMN_USER_ID_FRIENDS, userId);
            values1.put(COLUMN_FRIEND_ID, friendId);
            long result1 = db.insert(TABLE_FRIENDS, null, values1);

            ContentValues values2 = new ContentValues();
            values2.put(COLUMN_USER_ID_FRIENDS, friendId);
            values2.put(COLUMN_FRIEND_ID, userId);
            long result2 = db.insert(TABLE_FRIENDS, null, values2);

            if (result1 == -1 || result2 == -1) {
                Log.e(TAG, "Не удалось добавить друга");
            } else {
                Log.i(TAG, "Друг успешно добавлен");
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    @SuppressLint("Range")
    public List<Long> getFriendIds(long userId) {
        List<Long> friendIds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Исправленный запрос
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_FRIEND_ID + " FROM " + TABLE_FRIENDS + " WHERE " + COLUMN_USER_ID_FRIENDS + " = ?", new String[]{String.valueOf(userId)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                friendIds.add(cursor.getLong(cursor.getColumnIndex(COLUMN_FRIEND_ID)));
            }
            cursor.close();
        }
        return friendIds;
    }
    public long addMeet(String name, long date, String time, String place, String desc, long userId, long categoryId, long eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_PLACE, place);
        values.put(COLUMN_DESC, desc);
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_ID_C, categoryId);
        values.put(COLUMN_ID_E, eventId);
        long result = db.insert(TABLE_MEET, null, values);
        Log.d(TAG, "Added meeting for user ID: " + userId + ", result: " + result);
        return result;
    }

    public String getFormattedDate(long timestamp) {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
        return format.format(new java.util.Date(timestamp * 1000)); // Умножаем на 1000 для миллисекунд
    }


    // Метод для получения списка друзей
    public List<String> getFriends(long userId) {
        List<String> friendNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u." + COLUMN_USERNAME + " FROM " + TABLE_USERS + " u " +
                "INNER JOIN " + TABLE_FRIENDS + " f ON u." + COLUMN_ID + " = f." + COLUMN_FRIEND_ID +
                " WHERE f." + COLUMN_USER_ID_FRIENDS + " = ? " +
                "UNION " +
                "SELECT u." + COLUMN_USERNAME + " FROM " + TABLE_USERS + " u " +
                "INNER JOIN " + TABLE_FRIENDS + " f ON u." + COLUMN_ID + " = f." + COLUMN_USER_ID_FRIENDS +
                " WHERE f." + COLUMN_FRIEND_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(userId)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                friendNames.add(cursor.getString(0));
            }
            cursor.close();
        }
        return friendNames;
    }

    public void clearFriends(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Удаляем все записи из таблицы друзей для конкретного пользователя
            int rowsDeleted = db.delete(TABLE_FRIENDS,
                    COLUMN_USER_ID_FRIENDS + " = ? OR " + COLUMN_FRIEND_ID + " = ?",
                    new String[]{String.valueOf(userId), String.valueOf(userId)});

            if (rowsDeleted > 0) {
                Log.i(TAG, "Список друзей успешно очищен");
            } else {
                Log.e(TAG, "Не удалось очистить список друзей");
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public Boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE username = ? AND password = ?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void updateEmail(long userId, String newEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, newEmail); // Указываем правильный столбец
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
        Log.d(TAG, "Обновлен email для пользователя с ID " + userId + ". Затронуто строк: " + rowsAffected);
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

    public Cursor getAllUserMeetings(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT m.* FROM " + TABLE_MEET + " m " +
                "LEFT JOIN " + TABLE_MEET_PARTICIPANTS + " mp ON m." + COLUMN_ID_M + " = mp." + COLUMN_MEET_ID +
                " WHERE m." + COLUMN_USER_ID + " = ? OR mp." + COLUMN_PARTICIPANT_ID + " = ? " +
                "ORDER BY m." + COLUMN_DATE + " DESC";
        return db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(userId)});
    }

    public void clearMeetings(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Удаляем встречи, созданные пользователем
            int deletedMeetings = db.delete(TABLE_MEET, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            Log.d(TAG, "Удалено встреч: " + deletedMeetings);

            // Получаем ID встреч, в которых пользователь участвует
            String selectQuery = "SELECT " + COLUMN_MEET_ID + " FROM " + TABLE_MEET_PARTICIPANTS +
                    " WHERE " + COLUMN_PARTICIPANT_ID + " = ?";
            Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});

            // Удаляем участие пользователя во встречах
            int deletedParticipations = db.delete(TABLE_MEET_PARTICIPANTS, COLUMN_PARTICIPANT_ID + " = ?", new String[]{String.valueOf(userId)});
            Log.d(TAG, "Удалено участий: " + deletedParticipations);

            // Удаляем встречи, которые остались без участников
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") long meetId = cursor.getLong(cursor.getColumnIndex(COLUMN_MEET_ID));
                    String checkParticipantsQuery = "SELECT COUNT(*) FROM " + TABLE_MEET_PARTICIPANTS +
                            " WHERE " + COLUMN_MEET_ID + " = ?";
                    Cursor participantsCursor = db.rawQuery(checkParticipantsQuery, new String[]{String.valueOf(meetId)});
                    if (participantsCursor != null && participantsCursor.moveToFirst()) {
                        int count = participantsCursor.getInt(0);
                        if (count == 0) {
                            db.delete(TABLE_MEET, COLUMN_ID_M + " = ?", new String[]{String.valueOf(meetId)});
                            Log.d(TAG, "Удалена встреча без участников: " + meetId);
                        }
                        participantsCursor.close();
                    }
                } while (cursor.moveToNext());
            }
            if (cursor != null) {
                cursor.close();
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при очистке встреч: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    @SuppressLint("Range")
    public String getUserEmail(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String email = null;

        // Строим запрос
        String query = "SELECT " + COLUMN_EMAIL + " FROM " + TABLE_USERS + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));
        }

        cursor.close();
        db.close();

        return email;
    }
    @SuppressLint("Range")
    public String getUserPassword(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String password = null;

        // Строим запрос
        String query = "SELECT " + COLUMN_PASSWORD + " FROM " + TABLE_USERS + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
        }

        cursor.close();
        db.close();

        return password;
    }

    // Add this method to the DatabaseHelper class
    public List<Meeting> getNearestMeetingsForCurrentWeek(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Meeting> meetings = new ArrayList<>();

        // Get the start and end of the current week
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        long startOfWeek = calendar.getTimeInMillis() / 1000;
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        long endOfWeek = calendar.getTimeInMillis() / 1000;

        String query = "SELECT m.* FROM " + TABLE_MEET + " m " +
                "LEFT JOIN " + TABLE_MEET_PARTICIPANTS + " mp ON m." + COLUMN_ID_M + " = mp." + COLUMN_MEET_ID +
                " WHERE (m." + COLUMN_USER_ID + " = ? OR mp." + COLUMN_PARTICIPANT_ID + " = ?) " +
                "AND m." + COLUMN_DATE + " BETWEEN ? AND ? " +
                "ORDER BY m." + COLUMN_DATE + " ASC LIMIT 3";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId),
                String.valueOf(userId),
                String.valueOf(startOfWeek),
                String.valueOf(endOfWeek)
        });

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Meeting meeting = new Meeting(
                        cursor.getLong(cursor.getColumnIndex(COLUMN_ID_M)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                        cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TIME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_PLACE))
                );
                meetings.add(meeting);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return meetings;
    }

    // Add this inner class to the DatabaseHelper class
    public static class Meeting {
        public long id;
        public String name;
        public long date;
        public String time;
        public String place;

        public Meeting(long id, String name, long date, String time, String place) {
            this.id = id;
            this.name = name;
            this.date = date;
            this.time = time;
            this.place = place;
        }
    }




}


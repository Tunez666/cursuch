package com.example.alive; // Убедитесь, что пакет совпадает с вашим проектом

public class User {
    private long id;
    private String username;
    private String email;

    // Конструктор
    public User(long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // Геттеры
    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    // Метод для отображения информации о пользователе (опционально)
    @Override
    public String toString() {
        return "ID: " + id + "\nИмя: " + username + "\nEmail: " + email;
    }
}

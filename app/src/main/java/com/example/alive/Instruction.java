package com.example.alive;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Instruction extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        TextView instructionTextView = findViewById(R.id.instructionText);
        String htmlText = "<h2>Инструкция по использованию приложения</h2>" +
                "<p>Добро пожаловать в приложение! Вот как вы можете использовать его:</p>" +
                "<ul>" +
                "<li>На главной странице вы можете увидеть список встреч и назначить новые.</li>" +
                "<li>Чтобы добавить встречу, нажмите кнопку 'Создать встречу', введите название, дату, время и описание.</li>" +
                "<li>Чтобы добавить друга, нажмите кнопку 'друзья', затем нажав на кнопку 'добавить друга'. В открывшемся окне введите id друга и нажмите кнопку 'Добавить'</li>" +
                "<li>Для просмотра списка друзей вернитеь на предыдущую страницу</li>" +
                "<li>Для изменения аватара откройте настройки и выберите новый файл изображения.</li>" +
                "<li>В разделе настроек вы можете изменить имя пользователя, пароль и email.</li>" +
                "<li>Для выхода из приложения перейдите в настройки и выберите 'Выйти'.</li>" +
                "</ul>" +
                "<p>Пользуйтесь приложением с удовольствием!</p>";

        instructionTextView.setText(Html.fromHtml(htmlText));// Убедитесь, что путь к макету правильный
    }

}

package com.example.alive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class glavnay extends AppCompatActivity {
    private Button addb;
    private Button openLkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_glavnay);

        addb = findViewById(R.id.add);
        openLkButton = findViewById(R.id.openLkButton);

        addb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход на страницу создания встречи
                Intent intent = new Intent(glavnay.this, create_m.class);
                startActivity(intent);
            }
        });

        openLkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход в личный кабинет
                Intent intent = new Intent(glavnay.this, lk.class);
                startActivity(intent);
            }
        });
    }
}


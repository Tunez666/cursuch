package com.example.alive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class glavnay extends AppCompatActivity {
    private Button debug_m;
    private Button dBut;
    private Button addb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_glavnay);

            dBut = findViewById(R.id.deug);
            debug_m = findViewById(R.id.d_m);
            addb=findViewById(R.id.add);

        debug_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход на страницу входа
                Intent intent = new Intent(glavnay.this, MeetingsListActivity.class);
                startActivity(intent);
            }
            });

            addb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Переход на страницу создания встречи
                    Intent intent = new Intent(glavnay.this, create_m.class);
                    startActivity(intent);
                }
        });

        dBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход на страницу входа
                Intent intent = new Intent(glavnay.this, user_list.class);
                startActivity(intent);
            }
        });

    }
}
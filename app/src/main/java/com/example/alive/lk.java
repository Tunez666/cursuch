package com.example.alive;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class lk extends AppCompatActivity {
    private Button dBut;
    private Button debug_m;
    private Button addFriendButton;
    private Button viewFriendsButton;
    private Button logoutButton;
    private Button changeAvatarButton;
    private CircleImageView profileImageView;
    private DatabaseHelper databaseHelper;
    private static final int PICK_IMAGE = 100;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lk);

        databaseHelper = new DatabaseHelper(this);

        // Получаем ID пользователя из SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        dBut = findViewById(R.id.deug);
        debug_m = findViewById(R.id.d_m);
        addFriendButton = findViewById(R.id.addFriendButton);
        viewFriendsButton = findViewById(R.id.viewFriendsButton);
        logoutButton = findViewById(R.id.logoutButton);
        changeAvatarButton = findViewById(R.id.changeAvatarButton);
        profileImageView = findViewById(R.id.profileImageView);

        loadUserAvatar();

        dBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(lk.this, user_list.class);
                startActivity(intent);
            }
        });

        debug_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(lk.this, md.class);
                startActivity(intent);
            }
        });

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(lk.this, AddFriendActivity.class);
                startActivity(intent);
            }
        });

        viewFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(lk.this, FriendsListActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(view -> {
            SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(lk.this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(lk.this, Login.class);
            startActivity(intent);
            finish();
        });

        changeAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    openGallery();
                } else {
                    requestPermission();
                }
            }
        });
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Разрешение на доступ к галерее отклонено", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                String imagePath = imageUri.toString();
                databaseHelper.updateUserAvatar(userId, imagePath);
                loadUserAvatar();
                Toast.makeText(this, "Аватар обновлен", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadUserAvatar() {
        String avatarPath = databaseHelper.getUserAvatar(userId);
        if (avatarPath != null && !avatarPath.isEmpty()) {
            Glide.with(this)
                    .load(Uri.parse(avatarPath))
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.default_avatar);
        }
    }
}


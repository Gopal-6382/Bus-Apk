package com.example.bus.ui.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bus.R;
import com.example.bus.MainActivity;
import com.example.bus.ui.login.LoginActivity;

public class front extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);

        // Delay for splash screen animation
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            String lastScreen = sharedPreferences.getString("LastScreen", "LoginActivity");

            Intent intent;

            if (isLoggedIn) {
                try {
                    // Dynamically open the last visited screen
                    Class<?> lastActivity = Class.forName("com.example.bus." + lastScreen);
                    intent = new Intent(front.this, lastActivity);
                } catch (ClassNotFoundException e) {
                    // If there's an error, fallback to MainActivity
                    intent = new Intent(front.this, MainActivity.class);
                }
            } else {
                intent = new Intent(front.this, LoginActivity.class);
            }

            startActivity(intent);
            finish();

        }, 4000); // 4 seconds delay
    }
}

package com.example.baggegecalc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

    private static final String APP_URL =
            "https://github.com/mharisssyahmi-lab/BaggageCalc.git";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load saved theme preference
        SharedPreferences prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("is_dark_mode", false);
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.aboutToolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("About BaggageCalc");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Back button
        toolbar.setNavigationOnClickListener(v -> finish());

        // Website link
        TextView tvWebsite = findViewById(R.id.tvWebsite);

        tvWebsite.setOnClickListener(v -> {

            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(APP_URL));

            startActivity(browserIntent);
        });
    }
}

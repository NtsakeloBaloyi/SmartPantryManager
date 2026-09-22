package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Button btnBackToPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_settings
        );

        btnBackToPantry = findViewById(
                R.id.btnBackToPantry
        );

        btnBackToPantry.setOnClickListener(v -> {

            Intent intent = new Intent(
                    SettingsActivity.this,
                    PantryListActivity.class
            );

            startActivity(intent);

            finish();
        });
    }
}
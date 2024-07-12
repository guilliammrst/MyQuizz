package com.example.myquizz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class DifficultyActivity extends AppCompatActivity {
    private SoundEffects soundEffects;

    // UI elements
    private Button easyButton;
    private Button normalButton;
    private Button hardButton;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        soundEffects = new SoundEffects(this);

        // Initialize shared preferences and layout items.
        InitSharedPreferences();
        InitLayoutItems();

        // Set click listeners for the difficulty buttons.
        easyButton.setOnClickListener(
                view -> {
                    StartGame("easy");
                }
        );

        normalButton.setOnClickListener(
                view -> {
                    StartGame("normal");
                }
        );

        hardButton.setOnClickListener(
                view -> {
                    StartGame("hard");
                }
        );
    }

    // Initialize shared preferences.
    private void InitSharedPreferences() {
        sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Initialize layout items.
    private void InitLayoutItems() {
        easyButton = findViewById(R.id.easyButton);
        normalButton = findViewById(R.id.normalButton);
        hardButton = findViewById(R.id.hardButton);
    }

    // Start the game with the selected difficulty.
    private void StartGame (String difficulty)
    {
        soundEffects.PlayClickSound();

        editor.putString("currentQuestionNumberKey", "1");
        editor.putString("playerScore", "0");
        editor.putStringSet("questionsAlreadyDone", null);
        editor.apply();

        Intent intent = new Intent(DifficultyActivity.this, MainActivity.class);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
    }

    // Release resources held by SoundEffects instance.
    @Override
    protected void onDestroy() {
        soundEffects.Release();
        super.onDestroy();
    }
}
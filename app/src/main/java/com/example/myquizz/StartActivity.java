package com.example.myquizz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import java.util.*;

// StartActivity class manages the start screen of the quiz application.
public class StartActivity extends AppCompatActivity {
    private SoundEffects soundEffects;

    // Constants for difficulty levels.
    private static final String DIFFICULTY_EASY = "easy";
    private static final String DIFFICULTY_NORMAL = "normal";
    private static final String DIFFICULTY_HARD = "hard";

    // UI elements
    private Button startButton;
    private EditText pseudoEditText;
    private Switch switchThemeButton;
    private Button changeLanguageButton;
    private Button leaderboardButton;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        InitSharedPreferences();

        // Set the locale if previously saved in shared preferences.
        if (sharedPreferences.contains("languageKey")) {
            SetLocale(sharedPreferences.getString("languageKey", "en"));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        soundEffects = new SoundEffects(this);

        InitLayoutItems();

        // Set saved pseudo if available.
        if (sharedPreferences.contains("pseudoKey")) {
            pseudoEditText.setText(sharedPreferences.getString("pseudoKey", ""));
        }

        // Set click listener for the start button.
        startButton.setOnClickListener(
                view -> {
                    String pseudo = pseudoEditText.getText().toString();
                    String separatorChar = getString(R.string.separatorChar);

                    if (pseudo.isEmpty()) {
                        Toast.makeText(StartActivity.this, getString(R.string.usernameToast), Toast.LENGTH_SHORT).show();
                        soundEffects.PlayToastSound();
                    } else if (pseudo.length() > 20) {
                        Toast.makeText(StartActivity.this, getString(R.string.usernameLengthToast), Toast.LENGTH_SHORT).show();
                        soundEffects.PlayToastSound();
                    } else if (pseudo.contains(separatorChar)) {
                        Toast.makeText(StartActivity.this, getString(R.string.usernameCharToast), Toast.LENGTH_SHORT).show();
                        soundEffects.PlayToastSound();
                    } else {
                        editor.putString("pseudoKey", pseudo);
                        editor.apply();

                        if (pseudo.equals("Faker")) {
                            soundEffects.PlayFakerSound();
                        }
                        else {
                            soundEffects.PlayClickSound();
                        }

                        Intent intent = new Intent(StartActivity.this, DifficultyActivity.class);
                        startActivity(intent);
                    }
                }
        );

        // Set theme switch listener.
        switchThemeButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                editor.putBoolean("themeKey", true);
                editor.apply();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                editor.putBoolean("themeKey", false);
                editor.apply();
            }
        });

        // Set theme based on saved preference.
        if (sharedPreferences.contains("themeKey")) {
            if (sharedPreferences.getBoolean("themeKey", false)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                switchThemeButton.setChecked(true);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

        // Set click listeners for language and leaderboard buttons.
        changeLanguageButton.setOnClickListener(view -> {
            soundEffects.PlayClickSound();
            ShowLanguageSelectionDialog();
        });

        leaderboardButton.setOnClickListener(view -> {
            soundEffects.PlayClickSound();
            ShowLeaderboardSelectionDialog();
        });
    }

    // Initialize shared preferences.
    private void InitSharedPreferences() {
        sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Initialize layout items.
    private void InitLayoutItems () {
        startButton = findViewById(R.id.startButton);
        pseudoEditText = findViewById(R.id.pseudoEditText);
        switchThemeButton = findViewById(R.id.switchTheme);
        changeLanguageButton = findViewById(R.id.changeLanguageButton);
        leaderboardButton = findViewById(R.id.leaderboardButton);
    }

    // Show dialog to select language.
    private void ShowLanguageSelectionDialog() {
        String[] languages = getResources().getStringArray(R.array.availableLanguages);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.selectLanguage))
                .setItems(languages, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                SetLocaleAndRestart("en");
                                break;
                            case 1:
                                SetLocaleAndRestart("fr");
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    // Set locale and restart activity to apply changes.
    private void SetLocaleAndRestart(String languageCode) {
        SetLocale(languageCode);

        SharedPreferences.Editor editor = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE).edit();
        editor.putString("languageKey", languageCode);
        editor.apply();

        // Restart the activity to apply the new locale
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    // Set locale for the application.
    private void SetLocale (String languageCode){
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    // Show dialog to select leaderboard difficulty.
    private void ShowLeaderboardSelectionDialog() {
        String[] difficulties = new String[] {getString(R.string.easyDifficultyText), getString(R.string.normalDifficultyText), getString(R.string.hardDifficultyText)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.selectLeaderboardDifficulty))
                .setItems(difficulties, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                LoadLeaderboard(DIFFICULTY_EASY);
                                break;
                            case 1:
                                LoadLeaderboard(DIFFICULTY_NORMAL);
                                break;
                            case 2:
                                LoadLeaderboard(DIFFICULTY_HARD);
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    // Load leaderboard activity with selected difficulty.
    private void LoadLeaderboard(String difficulty) {
        Intent intent = new Intent(StartActivity.this, LeaderboardActivity.class);
        intent.putExtra("leaderboardDifficulty", difficulty);
        startActivity(intent);
    }

    // Release resources held by SoundEffects instance.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundEffects.Release();
    }
}
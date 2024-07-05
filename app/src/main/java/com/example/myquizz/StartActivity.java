package com.example.myquizz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;

import java.util.*;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button startButton = findViewById(R.id.startButton);
        EditText pseudoEditText = findViewById(R.id.pseudoEditText);
        TextView bestScoreEditText = findViewById(R.id.bestScoreTextView);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("pseudoKey")) {
            pseudoEditText.setText(sharedPreferences.getString("pseudoKey", ""));
        }

        List<String> bestScores = new ArrayList<>(sharedPreferences.getStringSet("bestScoresKey", new HashSet<>()));
        String bestScoreText;
        if (!bestScores.isEmpty()) {
            bestScores.sort((a, b) -> Integer.parseInt(b) - Integer.parseInt(a));
            bestScoreText = bestScores.get(0);
        }
        else {
            bestScoreText = "N/A";
        }
        bestScoreEditText.setText(bestScoreEditText.getText() + " " + bestScoreText);

        startButton.setOnClickListener(
                view -> {
                    String pseudo = pseudoEditText.getText().toString();
                    if (pseudo.isEmpty()) {
                        Toast.makeText(StartActivity.this, getString(R.string.usernameToast), Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("pseudoKey", pseudo);
                        editor.putString("currentQuestionNumberKey", "1");
                        editor.putString("playerScore", "0");
                        editor.apply();

                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
        );

        Switch switchButton = findViewById(R.id.switchTheme);

        switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        Button changeLanguageButton = findViewById(R.id.changeLanguageButton);
        changeLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageSelectionDialog();
            }
        });
    }

    private void showLanguageSelectionDialog() {
        String[] languages = getResources().getStringArray(R.array.availableLanguages);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.selectLanguage))
                .setItems(languages, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                setLocale("en");
                                break;
                            case 1:
                                setLocale("fr");
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Restart the activity to apply the new locale
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}
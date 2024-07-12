package com.example.myquizz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private SoundEffects soundEffects;

    // UI elements
    private TextView leaderboardTextView;
    private Button returnToMainMenuButton;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        soundEffects = new SoundEffects(this);

        // Initialize shared preferences and layout items.
        InitSharedPreferences();
        InitLayoutItems();

        int MAX_SCORES = Integer.parseInt(getString(R.string.maxScores));

        String leaderboardDifficulty = getIntent().getExtras().getString("leaderboardDifficulty");
        String separatorChar = getString(R.string.separatorChar);

        leaderboardTextView.setText(getString(R.string.scoresTextView) + " - " + leaderboardDifficulty);

        List<String> bestScores = new ArrayList<>(sharedPreferences.getStringSet(leaderboardDifficulty + "bestScoresKey", new HashSet<>()));
        bestScores.sort((a, b) -> Integer.parseInt(GetScoreWithoutUsername(b, separatorChar)) - Integer.parseInt(GetScoreWithoutUsername(a, separatorChar)));

        int count = 0;

        // Display the best scores for the selected difficulty level.
        for (String bestScore : bestScores) {
            int numberOfCurrentScore = sharedPreferences.getInt(leaderboardDifficulty + "numberOf" + bestScore, 1);

            for (int i = 0; i < numberOfCurrentScore; i++) {
                count++;
                if (count == MAX_SCORES) {
                    break;
                }

                leaderboardTextView.setText(leaderboardTextView.getText() + "\n" + bestScore);

                if (bestScores.indexOf(bestScore) == 0 && count == 1) {
                    leaderboardTextView.setText(leaderboardTextView.getText() + " (Best!)");
                }
            }
        }

        // Display a message if there are no scores.
        if (count == 0) {
            leaderboardTextView.setText(leaderboardTextView.getText() + "\n" + getString(R.string.noScores));
        }

        // Set click listener for the return to main menu button.
        returnToMainMenuButton.setOnClickListener(
                view -> {
                    soundEffects.PlayClickSound();

                    Intent intent = new Intent(LeaderboardActivity.this, StartActivity.class);
                    startActivity(intent);
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
        leaderboardTextView = findViewById(R.id.leaderboardTextView);
        returnToMainMenuButton = findViewById(R.id.returnToStartButton);
    }

    // Get the score without the username.
    private String GetScoreWithoutUsername (String scoreWithUsername, String separatorChar) {
        String[] parts = scoreWithUsername.split(separatorChar);

        if (parts.length == 2) {
            return parts[1].trim();
        } else {
            return "";
        }
    }

    // Release resources held by SoundEffects instance.
    @Override
    protected void onDestroy() {
        soundEffects.Release();
        super.onDestroy();
    }
}
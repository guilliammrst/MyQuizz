package com.example.myquizz;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.*;

public class ResultsActivity extends AppCompatActivity {
    private SoundEffects soundEffects;

    private int MAX_SCORES;

    // UI elements
    private Button returnToStartButton;
    private Button restartGameButton;
    private Button shareButton;
    private TextView playerScoreTextView;
    private TextView scoresTextView;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        MAX_SCORES = Integer.parseInt(getString(R.string.maxScores));

        soundEffects = new SoundEffects(this);

        // Initialize shared preferences and layout items.
        InitSharedPreferences();
        InitLayoutItems();

        // Get the player score, pseudo, and difficulty level.
        String difficulty = getIntent().getExtras().getString("difficulty");
        String playerScore = sharedPreferences.getString("playerScore", "0");
        String pseudoKey = sharedPreferences.getString("pseudoKey", "Player");
        String separatorChar = getString(R.string.separatorChar);

        playerScoreTextView.setText(playerScore);

        // Display the player score and best scores for the selected difficulty level.
        if (sharedPreferences.contains(difficulty + "bestScoresKey")) {
            // Get the best scores for the selected difficulty level.
            List<String> bestScores = new ArrayList<>(sharedPreferences.getStringSet(difficulty + "bestScoresKey", new HashSet<>()));
            bestScores.sort((a, b) -> Integer.parseInt(GetScoreWithoutUsername(b, separatorChar)) - Integer.parseInt(GetScoreWithoutUsername(a, separatorChar)));

            // Add the player score to the best scores if it is one of the top scores or if max score is not passed.
            if (bestScores.size() < MAX_SCORES) {
                AddScore(pseudoKey, separatorChar, playerScore, bestScores, difficulty);
            }
            else {
                for (String bestScore : bestScores) {
                    if (Integer.parseInt(playerScore) > Integer.parseInt(GetScoreWithoutUsername(bestScore, separatorChar))) {
                        AddScore(pseudoKey, separatorChar, playerScore, bestScores, difficulty);
                        break;
                    }
                }
            }

            // Display the player score and best scores.
            ShowLeaderboard(separatorChar, pseudoKey, difficulty, playerScore, bestScores);
        } else {
            // Add the player score to the best scores if it is the first score.
            Set<String> playerScoreSet = new HashSet<>();
            String playerScoreWithUsername = pseudoKey + " " + separatorChar + " " + playerScore;
            playerScoreSet.add(playerScoreWithUsername);

            editor.putStringSet(difficulty + "bestScoresKey", playerScoreSet).apply();

            // Display the player score.
            scoresTextView.setText(scoresTextView.getText() + "\n" + playerScoreWithUsername + " (Best!) (New!)");
        }


        // Set click listeners for the buttons.
        returnToStartButton.setOnClickListener(view -> {
            soundEffects.PlayClickSound();

            Intent intent = new Intent(ResultsActivity.this, StartActivity.class);
            startActivity(intent);
        });

        restartGameButton.setOnClickListener(view -> {
            if (pseudoKey.equals("Faker")) {
                soundEffects.PlayFakerSound();
            }
            else {
                soundEffects.PlayClickSound();
            }

            Intent intent = new Intent(ResultsActivity.this, DifficultyActivity.class);
            startActivity(intent);
        });

        shareButton.setOnClickListener(view -> {
            soundEffects.PlayClickSound();

            ShareContent();
        });
    }

    // Initialize shared preferences.
    private void InitSharedPreferences() {
        sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Initialize layout items.
    private void InitLayoutItems() {
        returnToStartButton = findViewById(R.id.returnToStartButton);
        restartGameButton = findViewById(R.id.restartGameButton);
        shareButton = findViewById(R.id.shareButton);
        playerScoreTextView = findViewById(R.id.playerScoreTextView);
        scoresTextView = findViewById(R.id.scoresTextView);
    }

    // Add the player score to the best scores.
    private void AddScore (String pseudoKey, String separatorChar, String playerScore, List<String> bestScores, String difficulty) {
        String playerScoreWithUsername = pseudoKey + " " + separatorChar + " " + playerScore;
        if (bestScores.contains(playerScoreWithUsername)) {
            editor.putInt(difficulty + "numberOf" + playerScoreWithUsername, sharedPreferences.getInt(difficulty + "numberOf" + playerScoreWithUsername, 1) + 1).apply();
        } else {
            AddNewScore(playerScore, bestScores, difficulty, separatorChar, pseudoKey);
        }
    }
    // Add a new score to the best scores.
    private void AddNewScore (String playerScore, List<String> bestScores, String difficulty, String separatorChar, String pseudoKey) {
        bestScores.add(pseudoKey + " " + separatorChar + " " + playerScore);

        bestScores.sort((a, b) -> Integer.parseInt(GetScoreWithoutUsername(b, separatorChar)) - Integer.parseInt(GetScoreWithoutUsername(a, separatorChar)));
        if (bestScores.size() > MAX_SCORES) {
            bestScores.remove(bestScores.size() - 1);
        }

        Set<String> bestScoresSet = new HashSet<>(bestScores);
        editor.putStringSet(difficulty + "bestScoresKey", bestScoresSet).apply();
    }

    // Share the player score.
    private void ShareContent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareText) + " " + getIntent().getExtras().getString("difficulty") + " " + ((TextView) findViewById(R.id.playerScoreTextView)).getText());
        shareIntent.setType("text/plain");

        Intent chooser = Intent.createChooser(shareIntent, getString(R.string.shareTextChooser));
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
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

    // Display the player score and best scores.
    private void ShowLeaderboard (String separatorChar, String pseudoKey, String difficulty, String playerScore, List<String> bestScores) {
        boolean isPlayerScoreAdded = false;
        int count = 0;

        for (String bestScore : bestScores) {
            int numberOfCurrentScore = sharedPreferences.getInt(difficulty + "numberOf" + bestScore, 1);

            for (int i = 0; i < numberOfCurrentScore; i++) {
                count++;
                if (count == MAX_SCORES) {
                    break;
                }

                scoresTextView.setText(scoresTextView.getText() + "\n" + bestScore);

                if (bestScores.indexOf(bestScore) == 0 && count == 1) {
                    scoresTextView.setText(scoresTextView.getText() + " (Best!)");
                }

                if (bestScore.equals(pseudoKey + " " + separatorChar + " " + playerScore) && !isPlayerScoreAdded) {
                    scoresTextView.setText(scoresTextView.getText() + " (New!)");
                    isPlayerScoreAdded = true;
                }
            }
        }
    }

    // Release the sound effects.
    @Override
    protected void onDestroy() {
        soundEffects.Release();
        super.onDestroy();
    }
}
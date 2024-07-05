package com.example.myquizz;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.*;

public class ResultsActivity extends AppCompatActivity {

    private static final int MAX_SCORES = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        String playerScore = sharedPreferences.getString("playerScore", "0");

        TextView playerScoreTextView = findViewById(R.id.playerScoreTextView);
        playerScoreTextView.setText(playerScore);

        TextView scoresTextView = findViewById(R.id.scoresTextView);

        if (sharedPreferences.contains("bestScoresKey")) {
            List<String> bestScores = new ArrayList<>(sharedPreferences.getStringSet("bestScoresKey", new HashSet<>()));
            bestScores.sort((a, b) -> Integer.parseInt(b) - Integer.parseInt(a));

            for (String bestScore : bestScores) {
                if (Integer.parseInt(playerScore) > Integer.parseInt(bestScore)) {
                    if (bestScores.contains(playerScore)) {
                        switch (Integer.parseInt(playerScore))
                        {
                            case 0:
                                sharedPreferences.edit().putInt("numberOf0", sharedPreferences.getInt("numberOf0", 1) + 1).apply();
                                break;
                            case 1:
                                sharedPreferences.edit().putInt("numberOf1", sharedPreferences.getInt("numberOf1", 1) + 1).apply();
                                break;
                            case 2:
                                sharedPreferences.edit().putInt("numberOf2", sharedPreferences.getInt("numberOf2", 1) + 1).apply();
                                break;
                            case 3:
                                sharedPreferences.edit().putInt("numberOf3", sharedPreferences.getInt("numberOf3", 1) + 1).apply();
                                break;
                            case 4:
                                sharedPreferences.edit().putInt("numberOf4", sharedPreferences.getInt("numberOf4", 1) + 1).apply();
                                break;
                            case 5:
                                sharedPreferences.edit().putInt("numberOf5", sharedPreferences.getInt("numberOf5", 1) + 1).apply();
                                break;
                        }
                    }
                    else {
                        bestScores.add(playerScore);

                        bestScores.sort((a, b) -> Integer.parseInt(b) - Integer.parseInt(a));
                        if (bestScores.size() > MAX_SCORES) {
                            bestScores.remove(bestScores.size() - 1);
                        }

                        Set<String> bestScoresSet = new HashSet<>(bestScores);
                        sharedPreferences.edit().putStringSet("bestScoresKey", bestScoresSet).apply();
                    }

                    break;
                }
            }

            boolean isPlayerScoreAdded = false;
            for (String bestScore : bestScores) {

                int numberOfCurrentScore;
                if (Objects.equals(bestScore, "5")) {
                    numberOfCurrentScore = sharedPreferences.getInt("numberOf5", 1);
                }
                else if (Objects.equals(bestScore, "4")) {
                    numberOfCurrentScore = sharedPreferences.getInt("numberOf4", 1);
                }
                else if (Objects.equals(bestScore, "3")) {
                    numberOfCurrentScore = sharedPreferences.getInt("numberOf3", 1);
                }
                else if (Objects.equals(bestScore, "2")) {
                    numberOfCurrentScore = sharedPreferences.getInt("numberOf2", 1);
                }
                else if (Objects.equals(bestScore, "1")) {
                    numberOfCurrentScore = sharedPreferences.getInt("numberOf1", 1);
                }
                else {
                    numberOfCurrentScore = sharedPreferences.getInt("numberOf0", 1);
                }

                for (int i = 0; i < numberOfCurrentScore; i++) {
                    if (bestScores.indexOf(bestScore) == MAX_SCORES) {
                        break;
                    }

                    if (bestScores.indexOf(bestScore) == 0) {
                        scoresTextView.setText(scoresTextView.getText() + "\n" + bestScore + " (Best!)");
                    }
                    else {
                        scoresTextView.setText(scoresTextView.getText() + "\n" + bestScore);
                    }

                    if (bestScore.equals(playerScore) && !isPlayerScoreAdded) {
                        scoresTextView.setText(scoresTextView.getText() + " (New!)");
                        isPlayerScoreAdded = true;
                    }
                }
            }
        } else {
            Set<String> playerScoreSet = new HashSet<>();
            playerScoreSet.add(playerScore);
            sharedPreferences.edit().putStringSet("bestScoresKey", playerScoreSet).apply();
        }

        Button restartButton = findViewById(R.id.returnToStartButton);

        restartButton.setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("currentQuestionNumberKey");
            editor.remove("playerScore");
            editor.apply();

            Intent intent = new Intent(ResultsActivity.this, StartActivity.class);
            startActivity(intent);
        });
    }
}
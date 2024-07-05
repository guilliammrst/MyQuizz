package com.example.myquizz;

import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    String answerSelected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        String currentQuestionNumber = sharedPreferences.getString("currentQuestionNumberKey", "1");

        TextView questionTextView = findViewById(R.id.questionTextView);
        String question = getResources().getString(getResources().getIdentifier("question" + currentQuestionNumber, "string", getPackageName()));

        questionTextView.setText(question);

        String numberOfQuestions = getResources().getString(R.string.numberOfQuestions);
        TextView currentQuestionTextView = findViewById(R.id.currentQuestionTextView);

        currentQuestionTextView.setText(currentQuestionTextView.getText() + " " + currentQuestionNumber  + "/" + numberOfQuestions);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        String[] questionAnswersArray = getResources().getStringArray(getResources().getIdentifier("questionAnswers" + currentQuestionNumber, "array", getPackageName()));

        for (String answer : questionAnswersArray) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(answer);
            radioButton.setTextSize(16);
            radioButton.setPadding(8, 8, 8, 8);

            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RadioButton radioButton = (RadioButton) v;
                    answerSelected = radioButton.getText().toString();
                }
            });

            radioGroup.addView(radioButton);
        }

        Button validateAnswerButton = findViewById(R.id.validateAnswerButton);

        validateAnswerButton.setOnClickListener(view -> {
            if (answerSelected.isEmpty()) {
                Toast.makeText(MainActivity.this, getString(R.string.answerToast), Toast.LENGTH_SHORT).show();
                return;
            }

            int playerScore = Integer.parseInt(sharedPreferences.getString("playerScore", "0"));
            String questionAnswer = getResources().getString(getResources().getIdentifier("questionAnswer" + currentQuestionNumber, "string", getPackageName()));

            if (Objects.equals(answerSelected, questionAnswer)) {
                sharedPreferences.edit().putString("playerScore", String.valueOf(playerScore + 1)).apply();
            }

            if (numberOfQuestions.equals(currentQuestionNumber)) {
                Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                startActivity(intent);
            }
            else {
                sharedPreferences.edit().putString("currentQuestionNumberKey", String.valueOf(Integer.parseInt(currentQuestionNumber) + 1)).apply();

                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
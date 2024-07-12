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
import org.w3c.dom.Text;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    RadioButton answerSelected = null;
    Boolean isValidation = true;
    private SoundEffects soundEffects;

    // UI elements
    private TextView questionTextView;
    private TextView currentQuestionTextView;
    private TextView questionAnswerTextView;
    private RadioGroup radioGroup;
    private Button validateAnswerButton;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundEffects = new SoundEffects(this);

        // Initialize shared preferences and layout items.
        InitSharedPreferences();
        InitLayoutItems();

        String currentQuestionNumber = sharedPreferences.getString("currentQuestionNumberKey", "1");
        int questionNumberChoice = ChooseRandomQuestion();

        String question = getResources().getString(getResources().getIdentifier("question" + questionNumberChoice, "string", getPackageName()));

        questionTextView.setText(question);

        // Display the current question number and total number of questions.
        String numberOfQuestions;
        if (getIntent().getExtras().getString("difficulty").equals("hard")) {
            numberOfQuestions = getResources().getString(R.string.hard_numberOfQuestions);
        }
        else if (getIntent().getExtras().getString("difficulty").equals("normal")) {
            numberOfQuestions = getResources().getString(R.string.normal_numberOfQuestions);
        }
        else {
            numberOfQuestions = getResources().getString(R.string.easy_numberOfQuestions);
        }

        currentQuestionTextView.setText(currentQuestionTextView.getText() + " " + currentQuestionNumber  + "/" + numberOfQuestions);

        // Display the question answers as radio buttons.
        String[] questionAnswersArray = getResources().getStringArray(getResources().getIdentifier("questionAnswers" + questionNumberChoice, "array", getPackageName()));
        for (String answer : questionAnswersArray) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(answer);
            radioButton.setTextSize(16);
            radioButton.setPadding(8, 8, 8, 8);

            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     answerSelected = (RadioButton) v;
                }
            });

            radioGroup.addView(radioButton);
        }

        // Set click listener for the validate answer button.
        validateAnswerButton.setOnClickListener(view -> {
            if (answerSelected == null) {
                soundEffects.PlayToastSound();
                Toast.makeText(MainActivity.this, getString(R.string.answerToast), Toast.LENGTH_SHORT).show();
                return;
            }

            int playerScore = Integer.parseInt(sharedPreferences.getString("playerScore", "0"));
            String questionAnswer = getResources().getString(getResources().getIdentifier("questionAnswer" + questionNumberChoice, "string", getPackageName()));

            // Validate the answer and display the result.
            if (isValidation) {
                if (Objects.equals(answerSelected.getText().toString(), questionAnswer)) {
                    soundEffects.PlayCongratsSound();

                    sharedPreferences.edit().putString("playerScore", String.valueOf(playerScore + 1)).apply();
                    questionAnswerTextView.setText(getString(R.string.correctAnswer));
                    questionAnswerTextView.setTextColor(getResources().getColor(R.color.green));

                    answerSelected.setTextColor(getResources().getColor(R.color.green));
                }
                else {
                    soundEffects.PlayErrorSound();

                    questionAnswerTextView.setText(getString(R.string.wrongAnswer) + " " + questionAnswer);
                    questionAnswerTextView.setTextColor(getResources().getColor(R.color.red));

                    answerSelected.setTextColor(getResources().getColor(R.color.red));

                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                        if (radioButton.getText().equals(questionAnswer)) {
                            radioButton.setTextColor(getResources().getColor(R.color.green));
                        }
                    }
                }

                validateAnswerButton.setText(getString(R.string.nextQuestion));
                isValidation = false;
            }
            // Go to the next question or display the results.
            else {
                soundEffects.PlayClickSound();

                if (numberOfQuestions.equals(currentQuestionNumber)) {
                    soundEffects.PlayResultsSound();

                    Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                    intent.putExtra("difficulty", getIntent().getExtras().getString("difficulty"));
                    startActivity(intent);
                } else {
                    sharedPreferences.edit().putString("currentQuestionNumberKey", String.valueOf(Integer.parseInt(currentQuestionNumber) + 1)).apply();

                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("difficulty", getIntent().getExtras().getString("difficulty"));
                    startActivity(intent);
                }
            }
        });

    }

    // Initialize shared preferences.
    private void InitSharedPreferences() {
        sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Initialize layout items.
    private void InitLayoutItems() {
        questionTextView = findViewById(R.id.questionTextView);
        currentQuestionTextView = findViewById(R.id.currentQuestionTextView);
        questionAnswerTextView = findViewById(R.id.questionAnswerTextView);
        radioGroup = findViewById(R.id.radioGroup);
        validateAnswerButton = findViewById(R.id.validateAnswerButton);
    }

    // Choose a random question that has not been done yet.
    private int ChooseRandomQuestion() {
        Set<String> questionsAlreadyDone = sharedPreferences.getStringSet("questionsAlreadyDone", null);

        int questionNumber = (int) (Math.random() * 50) + 1;
        if (questionsAlreadyDone != null) {
            while (questionsAlreadyDone.contains(String.valueOf(questionNumber)))  {
                questionNumber = (int) (Math.random() * 50) + 1;
            }
        }

        if (questionsAlreadyDone == null) {
            questionsAlreadyDone = new HashSet<String>();
        }

        questionsAlreadyDone.add(String.valueOf(questionNumber));

        editor.putStringSet("questionsAlreadyDone", questionsAlreadyDone);
        editor.apply();

        return questionNumber;
    }

    // Release resources held by SoundEffects instance.
    @Override
    protected void onDestroy() {
        soundEffects.Release();
        super.onDestroy();
    }
}
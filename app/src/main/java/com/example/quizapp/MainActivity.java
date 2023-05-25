package com.example.quizapp;


import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quizapp.DatabaseHelper;

class Quiz {
    private final String question;
    private final String[] options;
    private final int answerIndex;

    public Quiz(String question, String[] options, int answerIndex) {
        this.question = question;
        this.options = options;
        this.answerIndex = answerIndex;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options;
    }

    public boolean checkAnswer(int userAnswer) {
        return userAnswer == answerIndex;
    }

    public int getAnswerIndex() {
        return answerIndex;
    }
}

public class MainActivity extends AppCompatActivity {
    private TextView questionTextView;
    private LinearLayout optionsButtonContainer;

    private final Quiz[] quizzes = {
            new Quiz("Hufs global campus의 건물 수는?", new String[]{"10", "8", "14", "12"}, 0),
            new Quiz("통학버스를 탈 수 없는 곳은?", new String[]{"학생회관 앞", "도서관 앞", "어문관 앞", "백년관 앞"}, 0),
            new Quiz("23년 1학기 외대 글로벌캠 공식 종강 날짜는?", new String[]{"6월 21일", "6월 20일", "6월 22일", "6월 23일"}, 0),
            new Quiz("What is the national animal of India?", new String[]{"Tiger", "Elephant", "Lion", "Peacock"}, 0),
            new Quiz("Which country won the FIFA World Cup in 2018?", new String[]{"France", "Brazil", "Germany", "Argentina"}, 0)
    };

    private int currentQuizIndex = 0;
    private int correctAnswers = 0;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionTextView = findViewById(R.id.questionTextView);
        optionsButtonContainer = findViewById(R.id.optionsButtonContainer);
        Button submitButton = findViewById(R.id.submitButton);
        databaseHelper = new DatabaseHelper(this);

        submitButton.setOnClickListener(v -> {
            int selectedOptionIndex = getSelectedOptionIndex();

            if (selectedOptionIndex == -1) {
                Toast.makeText(MainActivity.this, "Please select an option", Toast.LENGTH_SHORT).show();
            } else {
                Quiz currentQuiz = quizzes[currentQuizIndex];
                boolean isCorrect = currentQuiz.checkAnswer(selectedOptionIndex);

                if (isCorrect) {
                    correctAnswers++;
                }

                String message;
                if (isCorrect) {
                    message = "정답이에요!";
                } else {
                    message = "틀렸어요. The correct answer is: " + currentQuiz.getOptions()[currentQuiz.getAnswerIndex()];
                }
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                if (currentQuizIndex < quizzes.length - 1) {
                    currentQuizIndex++;
                    loadQuiz();
                } else {
                    // Display quiz completion message or navigate to a new screen
                    String completionMessage = "Quiz completed. You got " + correctAnswers + " out of " + quizzes.length + " correct.";
                    Toast.makeText(MainActivity.this, completionMessage, Toast.LENGTH_SHORT).show();
                }
            }
            if (currentQuizIndex < quizzes.length - 1) {
                currentQuizIndex++;
                loadQuiz();
            } else {
                // Display quiz completion message or navigate to a new screen
                String completionMessage = "Quiz completed. You got " + correctAnswers + " out of " + quizzes.length + " correct.";
                Toast.makeText(MainActivity.this, completionMessage, Toast.LENGTH_SHORT).show();

                // Save ranking to the database
                databaseHelper.saveRanking("User", correctAnswers);

                // Retrieve ranking from the database
                Cursor cursor = databaseHelper.getRanking();
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME));
                        int score = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_SCORE));
                        // Process retrieved ranking data as needed
                    } while (cursor.moveToNext());
                    cursor.close();
        });

        loadQuiz();
    }

    private void loadQuiz() {
        Quiz currentQuiz = quizzes[currentQuizIndex];
        questionTextView.setText(currentQuiz.getQuestion());

        optionsButtonContainer.removeAllViews();
        String[] options = currentQuiz.getOptions();
        for (String option : options) {
            Button button = new Button(this);
            button.setText(option);
            button.setOnClickListener(v -> selectOption(button));
            optionsButtonContainer.addView(button);
        }
    }

    private void selectOption(Button button) {
        int childCount = optionsButtonContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Button childButton = (Button) optionsButtonContainer.getChildAt(i);
            boolean isSelected = childButton == button;
            childButton.setSelected(isSelected);
        }
    }

    private int getSelectedOptionIndex() {
        int childCount = optionsButtonContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Button childButton = (Button) optionsButtonContainer.getChildAt(i);
            if (childButton.isSelected()) {
                return i;
            }
        }
        return -1;
    }


}


}

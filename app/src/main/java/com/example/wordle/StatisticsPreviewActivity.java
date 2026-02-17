package com.example.wordle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StatisticsPreviewActivity extends AppCompatActivity {

    TextView currStreak, maxStreak, gamesPlayed, totalWins, winPercentage, funFact;
    Button backToHomeBtn;
    GuessDatabase database;
    GuessDAO guessDAO;
    Guess presentedGuess;
    SharedPreferences userPrefs;
    SharedPreferences.Editor userEditor;
    String username, currentStreakStr, maxStreakStr, gamesPlayedStr, totalWinsStr;
    int calcPercentages;
    StringBuilder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_preview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        funFact = findViewById(R.id.funFactTV);
        currStreak = findViewById(R.id.currentStreakTV);
        maxStreak = findViewById(R.id.maxStreakTV);
        gamesPlayed = findViewById(R.id.gamesPlayedTV);
        totalWins = findViewById(R.id.totalWinsTV);
        winPercentage = findViewById(R.id.winPercentageTV);
        backToHomeBtn = findViewById(R.id.statsToHomeBtn);

        userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userEditor = userPrefs.edit();
        username = userPrefs.getString("current_user", "");

        database = GuessDatabase.getInstance(this);
        guessDAO = database.guessDao();
        presentedGuess = guessDAO.getStats(username);

        if(presentedGuess == null){
            presentedGuess = new Guess(username,0,0,0,0);
        }

        int calcPercentages;
        if(presentedGuess.getGamesPlayed() == 0)
            calcPercentages = 0;
        else
            calcPercentages =
                    (presentedGuess.getTotalWins() * 100)
                            / presentedGuess.getGamesPlayed();

        currStreak.setText("Current Streak: " +
                presentedGuess.getCurrentStreak());

        maxStreak.setText("Max Streak: " +
                presentedGuess.getMaxStreak());

        gamesPlayed.setText("Games Played: " +
                presentedGuess.getGamesPlayed());

        totalWins.setText("Total Wins: " +
                presentedGuess.getTotalWins());

        winPercentage.setText("Win: " +
                calcPercentages + "%");


        backToHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StatisticsPreviewActivity.this, HomePageActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
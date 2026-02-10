package com.example.wordle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StatisticsPreviewActivity extends AppCompatActivity {

    TextView currStreak, maxStreak, gamesPlayed, totalWins, winPercentage;
    Button backToHomeBtn;
    GuessDatabase database;
    GuessDAO guessDAO;
    //Guess presentedGuess;
    SharedPreferences userPrefs;
    SharedPreferences.Editor userEditor;
    String username, currentStreakStr, maxStreakStr, gamesPlayedStr, totalWinsStr;
    int calcPercentages;
    StringBuilder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistics_preview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
        //presentedGuess = guessDAO.getStats(username);
        currentStreakStr = String.valueOf(guessDAO.getCurrentStreak(username));
        maxStreakStr = String.valueOf(guessDAO.getMaxStreak(username));
        gamesPlayedStr = String.valueOf(guessDAO.getGamesPlayed(username));
        totalWinsStr = String.valueOf(guessDAO.getTotalWins(username));


        if(gamesPlayedStr == "0") calcPercentages = 0;
         else
             calcPercentages = (Integer.valueOf(totalWinsStr) * 100) / Integer.valueOf(gamesPlayedStr);

        currStreak.setText(builder
                .append(currStreak
                        .getText()
                        .toString())
                .append(currentStreakStr)
                .toString());

        maxStreak.setText(builder
                .append(maxStreak
                        .getText()
                        .toString())
                .append(maxStreakStr)
                .toString());

        gamesPlayed.setText(builder
                .append(gamesPlayed
                        .getText()
                        .toString())
                .append(gamesPlayedStr)
                .toString());


        totalWins.setText(builder
                .append(totalWins
                        .getText()
                        .toString())
                .append(totalWinsStr)
                .toString());

        winPercentage.setText(builder
                .append(winPercentage
                        .getText()
                        .toString())
                .append(String.valueOf(calcPercentages))
                .append("%")
                .toString());

        backToHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StatisticsPreviewActivity.this, HomePageActivity.class);
                startActivity(i);
            }
        });
    }
}
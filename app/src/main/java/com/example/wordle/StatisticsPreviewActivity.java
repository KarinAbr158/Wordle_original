package com.example.wordle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class StatisticsPreviewActivity extends AppCompatActivity {
    private static final String TAG = "GeminiGeneration";

    TextView currStreak,
            maxStreak,
            gamesPlayed,
            totalWins,
            winPercentage,
            funFact;
    Button backToHomeBtn;
    GuessDatabase database;
    GuessDAO guessDAO;
    Guess presentedGuess;
    SharedPreferences userPrefs;
    SharedPreferences.Editor userEditor;
    String username, apiKey;

    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_preview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiKey = BuildConfig.GOOGLE_API_KEY;

        if (apiKey == null || apiKey.isEmpty()) {
            Log.w(TAG, "GOOGLE_API_KEY is empty – did you set it in local.properties?");
        } else {
            Log.d(TAG, "Loaded GOOGLE_API_KEY (length=" + apiKey.length() + ")");
        }
        client = Client.builder().apiKey(apiKey).build();
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

        funFact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Fub Fact text clicked");
                funFact.setText("Loading fact...");
                sendToGemini();
            }
        });

        backToHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StatisticsPreviewActivity.this, HomePageActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
    /**
     * פונה ל-Gemini API בשרשור נפרד כדי לקבל עובדה מעניינת על Wordle.
     * 
     * טענת כניסה: אין.
     * טענת יציאה: נשלחת בקשה ל-API, והתוצאה (עובדה או שגיאה) מוצגת ב-TextView המתאים בשרשור ה-UI.
     */
    private void sendToGemini() {

        final String prompt = "Give exactly one short interesting fact about Wordle.";
        final String model = "gemini-2.5-flash";

        new Thread(new Runnable() {
            @Override public void run() {
                try {

                    GenerateContentResponse response =
                            client.models.generateContent(model, prompt, null);

                    final String text = response.text();

                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            if (text != null && !text.isEmpty()) {
                                funFact.setText(text);
                            } else {
                                funFact.setText("No fact received.");
                            }
                        }
                    });

                } catch (Exception e) {

                    Log.e(TAG, "Gemini error: " + e.getMessage(), e);

                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            funFact.setText("Error fetching fact.");
                        }
                    });
                }
            }
        }).start();
    }

}

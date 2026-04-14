package com.example.wordle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;

public class HomePageActivity extends AppCompatActivity {

    Button startGame, settings, exit;
    Intent intent;
    /**
     * פעולה הנקראת בעת יצירת ה-Activity. מאתחלת את כפתורי התפריט,
     * בודקת את מצב המשחק (רגיל/יומי) ומגדירה את אירועי המעבר למסכים השונים.
     * 
     * טענת כניסה: מצב שמור של האפליקציה (savedInstanceState).
     * טענת יציאה: המסך מוכן, והכפתורים מקושרים לפעולות הניווט והגדרת מצבי המשחק.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startGame = findViewById(R.id.startBtn);
        settings = findViewById(R.id.settingsBtn);
        exit = findViewById(R.id.exitBtn);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(HomePageActivity.this, LogInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        //Check if an ongoing game exists
        //boolean hasOngoingGame = prefs.getString("secret_word", null) != null;
        //Check 24-hour mode status
        int gameMode = prefs.getInt("game_mode", 0); //0 = every new game, 1 = 24h
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault()).format(new java.util.Date());
        String lastPlayedDate = prefs.getString("last_played_date", "");
        boolean alreadyPlayedToday = currentDate.equals(lastPlayedDate);

        // START GAME BUTTON LOGIC
        //Disabled if: 24h mode AND played today
        if (gameMode == 0){
            startGame.setEnabled(true);
            startGame.setAlpha(1.0f);
            startGame.setText("Start New Game");
            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Only clear if isn't loading
                    //remove the word so GameActivity picks a new one
                    prefs.edit().remove("secret_word").apply();

                    intent = new Intent(HomePageActivity.this, GameActivity.class);
                    startActivity(intent);
                }
            });
        }

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.widget.PopupMenu popup = new android.widget.PopupMenu(HomePageActivity.this, settings);
                popup.getMenu().add("Every Game Mode");
                popup.getMenu().add("Daily (24h) Mode");
                popup.getMenu().add("See Personal Statistics");

                popup.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("Every Game Mode")) {
                            prefs.edit().putInt("game_mode", 0).apply();
                            Toast.makeText(HomePageActivity.this, "Mode: New word every game", Toast.LENGTH_SHORT).show();
                            startGame.setEnabled(true);
                            startGame.setAlpha(1.0f);
                            startGame.setText("Start New Game");
                            startGame.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //Only clear if isn't loading
                                    //remove the word so GameActivity picks a new one
                                    prefs.edit().remove("secret_word").apply();

                                    intent = new Intent(HomePageActivity.this, GameActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                        else if(item.getTitle().equals("Daily (24h) Mode")){
                            prefs.edit().putInt("game_mode", 1).apply();
                            Toast.makeText(HomePageActivity.this, "Mode: New word every 24 hours", Toast.LENGTH_SHORT).show();
                            if(alreadyPlayedToday) {
                                startGame.setEnabled(false);
                                startGame.setAlpha(0.3f);
                                startGame.setText("Next Word Tomorrow");
                            }
                        }
                        else if(item.getTitle().equals("See Personal Statistics")){
                            intent = new Intent(HomePageActivity.this, StatisticsPreviewActivity.class);
                            startActivity(intent);
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });


    }
}
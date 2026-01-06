package com.example.wordle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;

public class HomePageActivity extends AppCompatActivity {

    Button startGame, loadGame, settings, exit;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadGame = findViewById(R.id.loadBtn);
        startGame = findViewById(R.id.startBtn);
        settings = findViewById(R.id.settingsBtn);
        exit = findViewById(R.id.exitBtn);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SharedPreferences prefs = getSharedPreferences("GuessPrefs", MODE_PRIVATE);
        //Check if an ongoing game exists
        boolean hasOngoingGame = prefs.getString("secret_word", null) != null;
        //Check 24-hour mode status
        int gameMode = prefs.getInt("game_mode", 0); //0 = every new game, 1 = 24h
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault()).format(new java.util.Date());
        String lastPlayedDate = prefs.getString("last_played_date", "");
        boolean alreadyPlayedToday = currentDate.equals(lastPlayedDate);

        // LOAD GAME BUTTON LOGIC
        if (!hasOngoingGame) {
            loadGame.setEnabled(false);
            loadGame.setAlpha(0.3f);
        } else {
            loadGame.setEnabled(true);
            loadGame.setAlpha(1.0f);
            loadGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    i = new Intent(HomePageActivity.this, GameActivity.class);
                    startActivity(i);
                }
            });
        }

        // START GAME BUTTON LOGIC
        //Disabled if: 24h mode AND played today AND no game is currently in progress
        if (gameMode == 1 && alreadyPlayedToday && !hasOngoingGame) {
            startGame.setEnabled(false);
            startGame.setAlpha(0.3f);
            startGame.setText("Next Word Tomorrow");
        } else {
            startGame.setEnabled(true);
            startGame.setAlpha(1.0f);
            startGame.setText("Start New Game");
            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // IMPORTANT: Only clear if we aren't loading!
                    // We remove the word so GameActivity picks a new one
                    prefs.edit().remove("secret_word").apply();

                    i = new Intent(HomePageActivity.this, GameActivity.class);
                    startActivity(i);
                }
            });
        }



        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.widget.PopupMenu popup = new android.widget.PopupMenu(HomePageActivity.this, settings);
                popup.getMenu().add("Every Game Mode");
                popup.getMenu().add("Daily (24h) Mode");

                popup.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        if (item.getTitle().equals("Every Game Mode")) {
                            prefs.edit().putInt("game_mode", 0).apply();
                            android.widget.Toast.makeText(HomePageActivity.this, "Mode: New word every game", android.widget.Toast.LENGTH_SHORT).show();
                        } else {
                            prefs.edit().putInt("game_mode", 1).apply();
                            android.widget.Toast.makeText(HomePageActivity.this, "Mode: New word every 24 hours", android.widget.Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }
}
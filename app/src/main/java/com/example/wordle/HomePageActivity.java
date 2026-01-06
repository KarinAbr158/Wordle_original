package com.example.wordle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        // Initialize SharedPreferences to check for existing games
        android.content.SharedPreferences prefs = getSharedPreferences("GuessPrefs", MODE_PRIVATE);
        boolean hasSavedGame = prefs.getString("secret_word", null) != null;

        // LOAD GAME BUTTON
        // Disable the button visually if no game is found
        if (!hasSavedGame) {
            loadGame.setEnabled(false);
            loadGame.setAlpha(0.5f); // Make it look faded
        }

        loadGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Just start the activity; the GameActivity logic we wrote
                // earlier will handle loading the saved guesses.
                i = new Intent(HomePageActivity.this, GameActivity.class);
                startActivity(i);
            }
        });

        // START GAME BUTTON
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // To start a BRAND NEW game, we must clear the old data first
                prefs.edit().clear().apply();

                i = new Intent(HomePageActivity.this, GameActivity.class);
                startActivity(i);
            }
        });


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
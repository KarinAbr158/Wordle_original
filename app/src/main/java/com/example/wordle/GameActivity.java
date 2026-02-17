package com.example.wordle;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private LinearLayout row1, row2, row3;
    private TextView[][] cells = new TextView[6][5];
    GameLogic wordle;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String randomWord, currentDate, lastSavedDate, username;
    int gameMode;
    boolean shouldReset;
    GuessDAO guessDAO;
    GuessDatabase guessDatabase;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Log.v("GameActivity", "started onCreate");

        builder = new AlertDialog.Builder(this);
        guessDatabase = GuessDatabase.getInstance(this);
        guessDAO = guessDatabase.guessDao();

        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        editor = prefs.edit();

        username = prefs.getString("current_user", "");
        gameMode = prefs.getInt("game_mode", 0);
        currentDate = new SimpleDateFormat("dd-MM-yyyy",
                java.util.Locale.getDefault()).format(new java.util.Date());
        lastSavedDate = prefs.getString("last_played_date", "");

        randomWord = prefs.getString("secret_word", null);

        shouldReset = false;
        if (gameMode == 1) {// 24-Hour Mode
            if (!currentDate.equals(lastSavedDate)) {
                shouldReset = true; // Date has changed
            }
        }
        else{
            //In this mode it only generates a new word if the old game was finished
            //handled by the "Start Game" button clearing the prefs
            if (randomWord == null) {
                shouldReset = true;
            }
        }

        // Get the list of words
        String[] allWords = getResources().getStringArray(R.array.wordle_answers);

        // If the flag says we need a reset (either new day or new game)
        if (shouldReset || randomWord == null) { // always reset if no word
            // 1. Pick a brand new word
            randomWord = allWords[new Random().nextInt(allWords.length)];

            // 2. Save the new word and today's date to memory
            editor.putString("secret_word", randomWord);
            editor.putString("last_played_date", currentDate);

            editor.apply();
        }

        row1 = findViewById(R.id.row1);
        row2 = findViewById(R.id.row2);
        row3 = findViewById(R.id.row3);

        wordGrid();
        wordle = new GameLogic(GameActivity.this, cells, row1, row2, row3, randomWord, allWords);

        addKeys(row1, new String[]{"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"});
        addKeys(row2, new String[]{"A", "S", "D", "F", "G", "H", "J", "K", "L"});
        addKeys(row3, new String[]{"⏎", "Z", "X", "C", "V", "B", "N", "M", "⌫"});
    }

    private void wordGrid() {
        for(int r = 0; r < 6; r++) {
            for(int c = 0; c < 5; c++) {
                int id = getResources().getIdentifier(
                        "cell_" + (r + 1) + "_" + (c + 1),
                        "id",
                        getPackageName()
                );
                cells[r][c] = findViewById(id);
            }
        }
    }

    private void addKeys(LinearLayout row, String[] keys) {

        for(int i = 0; i < keys.length; i++){
            final String text = keys[i];

            Button b = new Button(this);
            b.setText(text);
            b.setAllCaps(true);
            b.setTextSize(18);
            b.setTypeface(null, Typeface.BOLD);

            LinearLayout.LayoutParams p =
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);

            if(text.equals("⏎") ) {
                p.weight = 4;
            }
            else if(text.equals("⌫")){
                p.weight = 4;
            }
            else{
                p.weight = 3;
            }

            if(text.equals("⏎") || text.equals("⌫")){
                p.setMargins(0, 4, 0, 4);
            }
            else{
                p.setMargins(1, 4, 1, 4);
            }
            b.setLayoutParams(p);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(text.equals("⏎")){
                        handleKeyPress("ENTER");
                    }
                    else if(text.equals("⌫")){
                        handleKeyPress("DEL");
                    }
                    else{
                        handleKeyPress(text);
                    }
                }
            });
            row.addView(b);
        }
    }

    private void handleKeyPress(String key) {
        if(key.equals("ENTER")){
            wordle.submitWord();
            if(wordle.isGameOver()){
                builder.setTitle("Game Over");
                builder.setMessage("Would you like to see your statistics or go back to the home page?");

                builder.setPositiveButton("See Statistics", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(GameActivity.this, StatisticsPreviewActivity.class);
                        startActivity(i);
                        finish();
                    }
                });

                builder.setNegativeButton("Home Page", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        else if(key.equals("DEL")){
            wordle.deleteLetter();
        }
        else{
            wordle.addLetter(key);
        }
    }
}
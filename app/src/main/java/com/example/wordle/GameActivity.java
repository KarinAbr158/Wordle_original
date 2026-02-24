package com.example.wordle;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Random;

/**
 * Main game screen. Handles all UI: grid display, keyboard,
 * animations, dialogs. Receives game events via GameListener.
 */
public class GameActivity extends AppCompatActivity implements GameListener {

    private static final long IDLE_TIMEOUT = 25000; // 25 seconds
    private final Handler idleHandler = new Handler(Looper.getMainLooper());
    private final Runnable idleRunnable = new Runnable() {
        @Override
        public void run() {
            onUserIdle();
        }
    };

    private LinearLayout row1, row2, row3;
    private TextView[][] cells = new TextView[6][5];

    final int rows = 6;
    final int cols = 5;

    GameLogic wordle;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String randomWord, currentDate, lastSavedDate, username;
    int gameMode;
    boolean shouldReset;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Log.v("GameActivity", "started onCreate");
        Log.v("GameActivity", "started idle timer");
        startIdleTimer();

        // Colors are now handled via drawable resources (tile_green, tile_yellow, etc.)

        builder = new AlertDialog.Builder(this);

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
        wordle = new GameLogic(GameActivity.this, this, randomWord, allWords);

        addKeys(row1, new String[]{"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"});
        addKeys(row2, new String[]{"A", "S", "D", "F", "G", "H", "J", "K", "L"});
        addKeys(row3, new String[]{"⏎", "Z", "X", "C", "V", "B", "N", "M", "⌫"});
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        resetIdleTimer();
    }
    @Override
    protected void onPause() { //Prevents memory leaks
        super.onPause();
        idleHandler.removeCallbacks(idleRunnable);
    }
    @Override
    protected void onResume() {
        super.onResume();
        startIdleTimer();
    }

    private void startIdleTimer() {
        idleHandler.postDelayed(idleRunnable, IDLE_TIMEOUT);
    }

    private void resetIdleTimer() {
        idleHandler.removeCallbacks(idleRunnable);
        idleHandler.postDelayed(idleRunnable, IDLE_TIMEOUT);
    }

    private void onUserIdle() {
        Toast.makeText(this, "You awake? Do something!", Toast.LENGTH_SHORT).show();
    }

    private void wordGrid() {
        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < cols; c++) {
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
            b.setTextSize(14);
            b.setTypeface(null, Typeface.BOLD);
            b.setBackgroundResource(R.drawable.key_bg);
            b.setTextColor(ContextCompat.getColor(this, R.color.dark_text));
            b.setStateListAnimator(null); // Remove default elevation shadow

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

            p.setMargins(2, 4, 2, 4);
            b.setLayoutParams(p);
            b.setPadding(0, 16, 0, 16);
            b.setMinWidth(0);
            b.setMinimumWidth(0);
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
            // Game over is now handled via onGameWon / onGameLost callbacks
        }
        else if(key.equals("DEL")){
            wordle.deleteLetter();
        }
        else{
            wordle.addLetter(key);
        }
    }

    // =============================================
    //          GameListener Callbacks
    // =============================================

    @Override
    public void onLetterAdded(int row, int col, String letter) {
        cells[row][col].setText(letter);
        cells[row][col].setBackgroundResource(R.drawable.tile_filled);
    }

    @Override
    public void onLetterDeleted(int row, int col) {
        cells[row][col].setText("");
        cells[row][col].setBackgroundResource(R.drawable.tile_empty);
    }

    @Override
    public void onTileResult(int row, int col, int colorType) {
        TextView tile = cells[row][col];
        tile.setBackgroundResource(mapColorDrawable(colorType));
        tile.setTextColor(ContextCompat.getColor(this, R.color.white));

        // Pop animation
        tile.setScaleX(0f);
        tile.setScaleY(0f);
        tile.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(150)
                .withEndAction(() -> tile.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                )
                .start();
    }

    @Override
    public void onKeyColorUpdate(char letter, int colorType) {
        int drawableRes = mapColorDrawable(colorType);
        LinearLayout[] keyboardRows = {row1, row2, row3};

        for(LinearLayout row : keyboardRows){
            for(int i = 0; i < row.getChildCount(); i++){
                Button b = (Button) row.getChildAt(i);
                if (b.getText().length() == 1 && b.getText().charAt(0) == letter){
                    b.setBackgroundResource(drawableRes);
                    b.setTextColor(ContextCompat.getColor(this, R.color.white));
                }
            }
        }
    }

    @Override
    public void onGameWon() {
        Toast.makeText(this, "Splendid!", Toast.LENGTH_SHORT).show();
        showGameOverDialog();
    }

    @Override
    public void onGameLost(String secretWord) {
        Toast.makeText(this, "Game Over! The word was: " + secretWord, Toast.LENGTH_LONG).show();
        showGameOverDialog();
    }

    @Override
    public void onInvalidWord() {
        Toast.makeText(this, "Not in word list", Toast.LENGTH_SHORT).show();
    }

    // =============================================
    //          Helper Methods
    // =============================================

    /** Maps a GameLogic color constant to a drawable resource ID */
    private int mapColorDrawable(int colorType) {
        switch (colorType) {
            case GameLogic.COLOR_GREEN:  return R.drawable.tile_green;
            case GameLogic.COLOR_YELLOW: return R.drawable.tile_yellow;
            case GameLogic.COLOR_GRAY:   return R.drawable.tile_gray;
            default:                     return R.drawable.tile_empty;
        }
    }

    /** Shows the end-of-game dialog with options to view stats or go home */
    private void showGameOverDialog() {
        idleHandler.removeCallbacks(idleRunnable);

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
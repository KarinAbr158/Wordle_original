package com.example.wordle;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.HashMap;

public class GameLogic {
    private Context context;
    private TextView[][] cells;
    private LinearLayout row1, row2, row3;
    private HashMap<Character, Integer> keyColors = new HashMap<>();
    private String[] savedGuess, allWordsPossible;

    private int currentRow = 0,
            currentCol = 0;
    private final int maxRow = 6,
            maxCol = 5;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private String secretWord;
    private int maxStreak,
            currStreak,
            gamesPlayed,
            totalWins;

    private final int GREEN, YELLOW, GRAY, WHITE;

    public GameLogic(Context context, TextView[][] cells,
                     LinearLayout row1, LinearLayout row2, LinearLayout row3,
                     String secretWord, String[] allWords) {
        this.context = context;
        this.cells = cells;
        this.row1 = row1;
        this.row2 = row2;
        this.row3 = row3;
        this.secretWord = secretWord;
        this.allWordsPossible = allWords;
        this.savedGuess = new String[6];
        for(int i = 0; i < this.savedGuess.length; i++){
            this.savedGuess[i] = "";
        }
        this.maxStreak = 0;
        this.currStreak = 0;
        this.gamesPlayed = 0;
        this.totalWins = 0;
        GREEN = ContextCompat.getColor(this.context, R.color.green);
        YELLOW = ContextCompat.getColor(this.context, R.color.yellow);
        GRAY = ContextCompat.getColor(this.context, R.color.gray);
        WHITE = ContextCompat.getColor(this.context, R.color.white);
    }

    public void addLetter(String letter){
        if(gameOver) return;
        if(currentCol < maxCol){
            cells[currentRow][currentCol].setText(letter.toUpperCase());
            currentCol++;
        }
    }

    public void deleteLetter(){
        if(gameOver) return;
        if(currentCol > 0){
            currentCol--;
            cells[currentRow][currentCol].setText("");
        }
    }

    public boolean isInArray(String guess){
        for(int i = 0; i < this.allWordsPossible.length; i++){
            if(this.allWordsPossible[i].equalsIgnoreCase(guess)){
                return true;
            }
        }
        return false;
    }

    public void submitWord(){
        if(gameOver) return;
        if(currentCol < maxCol) return;

        StringBuilder guessBuilder = new StringBuilder();
        for(int i = 0; i < 5; i++){
            guessBuilder.append(cells[currentRow][i].getText().toString());
        }
        String guess = guessBuilder.toString();

        guess = guess.toUpperCase();
        if(isInArray(guess)) {
            this.savedGuess[this.currentRow] = guess;
            checkGuess(guess);

            if(guess.equals(secretWord)) {
                gameOver = true;
                gameWon = true;
                Toast.makeText(context, "Splendid!", Toast.LENGTH_SHORT).show();
                handleGameEnd();
            } else if(currentRow == 5) {
                gameOver = true;
                gameWon = false;
                Toast.makeText(context, "Game Over! The word was: " + secretWord, Toast.LENGTH_LONG).show();
                handleGameEnd();
            } else {
                currentRow++;
                currentCol = 0;
            }
        }
        else{
            Toast.makeText(context, "Not in word list", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleGameEnd(){
        GuessDatabase guessDatabase = GuessDatabase.getInstance(context);
        GuessDAO guessDAO = guessDatabase.guessDao();
        //get the specific preferences file
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String username = prefs.getString("current_user", "");

        // Load existing stats so we accumulate instead of resetting
        Guess existing = guessDAO.getStats(username);
        if (existing != null) {
            this.gamesPlayed = existing.getGamesPlayed();
            this.totalWins = existing.getTotalWins();
            this.currStreak = existing.getCurrentStreak();
            this.maxStreak = existing.getMaxStreak();
        }

        // Apply the result of this game
        this.gamesPlayed++;
        if (gameWon) {
            this.totalWins++;
            this.currStreak++;
            if (this.maxStreak < this.currStreak) {
                this.maxStreak = this.currStreak;
            }
        } else {
            this.currStreak = 0;
        }

        Guess updatedStats = new Guess(username,
                this.currStreak,
                this.maxStreak,
                this.gamesPlayed,
                this.totalWins);

        if (existing != null) {
            guessDAO.update(updatedStats);
        } else {
            guessDAO.insert(updatedStats);
        }

        //remove the secret word so a new game can start
        editor.remove("secret_word");
        //doesn't clear everything, because it's needs to save "last_played_date"
        //for the 24-hour mode check in HomePageActivity.
        editor.apply();
    }


    private void checkGuess(String guess){
        boolean[] used = new boolean[maxCol];

        // Green letters
        for(int i = 0; i < used.length; i++){
            if(guess.charAt(i) == secretWord.charAt(i)){
                TextView tile = cells[currentRow][i];
                tile.setBackgroundColor(GREEN);
                colorKey(guess.charAt(i), GREEN); // keyboard update
                used[i] = true;

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
        }

        // Yellow / Gray letters
        for(int i = 0; i < used.length; i++){
            int currentColor = ((ColorDrawable)cells[currentRow][i].getBackground()).getColor();
            if(currentColor != GREEN){
                boolean found = false;
                for(int j = 0; j < used.length; j++){
                    if(!used[j] && guess.charAt(i) == secretWord.charAt(j)){
                        found = true;
                        used[j] = true;
                        j = used.length;
                    }
                }

                TextView tile = cells[currentRow][i];

                if(found){
                    tile.setBackgroundColor(YELLOW);
                    colorKey(guess.charAt(i), YELLOW);
                } else {
                    tile.setBackgroundColor(GRAY);
                    colorKey(guess.charAt(i), GRAY);
                }

                // Pop animation for yellow/gray tiles
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
        }
    }

    private void colorKey(char letter, int color) {
        letter = Character.toUpperCase(letter);
        //GREEN > YELLOW > GRAY
        if(!this.keyColors.containsKey(letter)){
            this.keyColors.put(letter, color);
        }
        else{
            int oldColor = this.keyColors.get(letter);
            if (oldColor == GREEN) return;
            if (oldColor == YELLOW && color == GRAY) return;
            this.keyColors.put(letter, color);
        }
        LinearLayout[] rows = {row1, row2, row3};

        for(LinearLayout row:rows){
            for(int i = 0; i < row.getChildCount(); i++){
                Button b = (Button)row.getChildAt(i);
                if (b.getText().length() == 1 && b.getText().charAt(0) == letter){
                    b.setBackgroundColor(color);
                    b.setTextColor(WHITE);//so you see it better against the dark background
                }
            }
        }
    }

    public int getCurrentRow() {
        return this.currentRow;
    }

    public boolean isGameOver(){
        return gameOver;
    }
}

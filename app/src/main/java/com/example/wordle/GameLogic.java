package com.example.wordle;

import android.content.SharedPreferences;
import android.content.Context;

import java.util.HashMap;

/**
 * Handles all Wordle game logic: letter input, guess validation,
 * color-checking algorithm, and statistics persistence.
 * Does NOT touch any UI elements — communicates via GameListener.
 */
public class GameLogic {

    public static final int COLOR_GREEN = 0;
    public static final int COLOR_YELLOW = 1;
    public static final int COLOR_GRAY = 2;

    private Context context;
    private GameListener listener;
    private HashMap<Character, Integer> keyColors = new HashMap<>();
    private String[][] grid;
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

    public GameLogic(Context context, GameListener listener,
                     String secretWord, String[] allWords) {
        this.context = context;
        this.listener = listener;
        this.secretWord = secretWord;
        this.allWordsPossible = allWords;

        this.grid = new String[maxRow][maxCol];
        for (int r = 0; r < maxRow; r++)
            for (int c = 0; c < maxCol; c++)
                grid[r][c] = "";

        this.savedGuess = new String[6];
        for(int i = 0; i < this.savedGuess.length; i++){
            this.savedGuess[i] = "";
        }
        this.maxStreak = 0;
        this.currStreak = 0;
        this.gamesPlayed = 0;
        this.totalWins = 0;
    }

    public void addLetter(String letter){
        if(gameOver) return;
        if(currentCol < maxCol){
            String upper = letter.toUpperCase();
            grid[currentRow][currentCol] = upper;
            listener.onLetterAdded(currentRow, currentCol, upper);
            currentCol++;
        }
    }

    public void deleteLetter(){
        if(gameOver) return;
        if(currentCol > 0){
            currentCol--;
            grid[currentRow][currentCol] = "";
            listener.onLetterDeleted(currentRow, currentCol);
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
        for(int i = 0; i < maxCol; i++){
            guessBuilder.append(grid[currentRow][i]);
        }
        String guess = guessBuilder.toString().toUpperCase();

        if(isInArray(guess)) {
            this.savedGuess[this.currentRow] = guess;
            checkGuess(guess);

            if(guess.equals(secretWord)) {
                gameOver = true;
                gameWon = true;
                handleGameEnd();
                listener.onGameWon();
            } else if(currentRow == 5) {
                gameOver = true;
                gameWon = false;
                handleGameEnd();
                listener.onGameLost(secretWord);
            } else {
                currentRow++;
                currentCol = 0;
            }
        }
        else{
            listener.onInvalidWord();
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
        editor.apply();
    }


    /**
     * Checks the player's guess against the secret word.
     * Determines GREEN (correct position), YELLOW (wrong position),
     * or GRAY (not in word) for each letter, then notifies the listener.
     */
    private void checkGuess(String guess){
        boolean[] used = new boolean[maxCol];
        int[] results = new int[maxCol];
        for (int i = 0; i < maxCol; i++) results[i] = -1;

        // Green letters — correct letter in correct position
        for(int i = 0; i < maxCol; i++){
            if(guess.charAt(i) == secretWord.charAt(i)){
                results[i] = COLOR_GREEN;
                updateKeyColor(guess.charAt(i), COLOR_GREEN);
                used[i] = true;
            }
        }

        // Yellow / Gray letters
        for(int i = 0; i < maxCol; i++){
            if(results[i] != COLOR_GREEN){
                boolean found = false;
                for(int j = 0; j < maxCol; j++){
                    if(!used[j] && guess.charAt(i) == secretWord.charAt(j)){
                        found = true;
                        used[j] = true;
                        break;
                    }
                }

                if(found){
                    results[i] = COLOR_YELLOW;
                    updateKeyColor(guess.charAt(i), COLOR_YELLOW);
                } else {
                    results[i] = COLOR_GRAY;
                    updateKeyColor(guess.charAt(i), COLOR_GRAY);
                }
            }
        }

        // Notify listener of all tile results
        for (int i = 0; i < maxCol; i++) {
            listener.onTileResult(currentRow, i, results[i]);
        }
    }

    /**
     * Updates the keyboard color for a letter, respecting priority:
     * GREEN > YELLOW > GRAY (a key never downgrades).
     */
    private void updateKeyColor(char letter, int colorType) {
        letter = Character.toUpperCase(letter);
        //GREEN > YELLOW > GRAY
        if(!this.keyColors.containsKey(letter)){
            this.keyColors.put(letter, colorType);
        }
        else{
            int oldColor = this.keyColors.get(letter);
            if (oldColor == COLOR_GREEN) return;
            if (oldColor == COLOR_YELLOW && colorType == COLOR_GRAY) return;
            this.keyColors.put(letter, colorType);
        }
        listener.onKeyColorUpdate(letter, colorType);
    }

    public int getCurrentRow() {
        return this.currentRow;
    }

    public boolean isGameOver(){
        return gameOver;
    }
}

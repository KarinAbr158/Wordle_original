package com.example.wordle;

import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import java.util.HashMap;

public class GameLogic {
    private Context context;
    private TextView[][] cells;
    private LinearLayout row1, row2, row3;
    private HashMap<Character, Integer> keyColors = new HashMap<>();

    private int currentRow = 0;
    private int currentCol = 0;
    private boolean gameOver = false;
    private String secretWord;
    private String[] allWordsPossible;
    /*private int maxStreak;
    private int currStreak;
    private int gamesPlayedCnt;
    private int winPercentages;*/

    private final int GREEN = 0xFF6AAA64;
    private final int YELLOW = 0xFFC9B458;
    private final int GRAY = 0xFF787C7E;

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
    }

    public void addLetter(String letter){
        if(gameOver) return;
        if(currentCol < 5){
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
            if(this.allWordsPossible[i].equals(guess)){
                return true;
            }
        }
        return false;
    }

    public void submitWord(){
        if(gameOver) return;
        if(currentCol < 5) return;

        String guess = "";
        for(int i = 0; i < 5; i++){
            guess += (cells[currentRow][i].getText().toString());
        }
        guess = guess.toUpperCase();
        if(isInArray(guess)) {
            checkGuess(guess);

            // בדיקת סיום משחק (עושים את זה רק אם המילה הייתה חוקית)
            if(guess.equals(secretWord)) {
                gameOver = true;
                Toast.makeText(context, "Splendid!", Toast.LENGTH_SHORT).show();
            } else if(currentRow == 5) { // שים לב: בדרך כלל 6 שורות זה אינדקס 0 עד 5
                gameOver = true;
                Toast.makeText(context, "Game Over! The word was: " + secretWord, Toast.LENGTH_LONG).show();
            } else {
                currentRow++;
                currentCol = 0;
            }
        }
        else{
            // כאן נכנס הקוד שלך לטיפול במילה לא חוקית
            Toast.makeText(context, "Not in word list", Toast.LENGTH_SHORT).show();
            // הערה: אנחנו לא מקדמים את השורה (currentRow++) כדי שהמשתמש יוכל לתקן
        }
    }

    private void checkGuess(String guess){
        boolean[] used = new boolean[5];
        //Green letters
        for(int i = 0; i < 5; i++){
            if(guess.charAt(i) == secretWord.charAt(i)){
                cells[currentRow][i].setBackgroundColor(GREEN);
                colorKey(guess.charAt(i), GREEN);
                used[i] = true;
            }
        }
        //Yellow/Gray letters
        for(int i = 0; i < 5; i++){
            int currentColor = ((ColorDrawable)cells[currentRow][i].getBackground()).getColor();
            if(currentColor == GREEN){
                //already green, skip
            }
            else{
                boolean found = false;
                for(int j = 0; j < 5; j++){
                    if(!used[j] && guess.charAt(i) == secretWord.charAt(j)){
                        found = true;
                        used[j] = true;
                        break;
                    }
                }
                if(found){
                    cells[currentRow][i].setBackgroundColor(YELLOW);
                    colorKey(guess.charAt(i), YELLOW);
                }
                else{
                    cells[currentRow][i].setBackgroundColor(GRAY);
                    colorKey(guess.charAt(i), GRAY);
                }
            }
        }
    }

    private void colorKey(char letter, int color) {
        letter = Character.toUpperCase(letter);
        //GREEN > YELLOW > GRAY
        if(!keyColors.containsKey(letter)){
            keyColors.put(letter, color);
        }
        else{
            int oldColor = keyColors.get(letter);
            if (oldColor == GREEN) return;
            if (oldColor == YELLOW && color == GRAY) return;
            keyColors.put(letter, color);
        }
        LinearLayout[] rows = {row1, row2, row3};

        for(LinearLayout row:rows){
            for(int i = 0; i < row.getChildCount(); i++){
                Button b = (Button)row.getChildAt(i);
                if (b.getText().length() == 1 && b.getText().charAt(0) == letter){
                    b.setBackgroundColor(color);
                    b.setTextColor(0xFFFFFFFF);//White so you see it better against the dark background
                }
            }
        }
    }

    public boolean isGameOver(){
        return gameOver;
    }
}

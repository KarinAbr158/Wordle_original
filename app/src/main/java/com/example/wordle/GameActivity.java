package com.example.wordle;

import android.graphics.Color;
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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameActivity extends AppCompatActivity {

    LinearLayout row1, row2, row3;
    TextView[][] cells = new TextView[6][5];
    int currentRow = 0;
    int currentCol = 0;
    String secretWord = "APPLE"; // hardcoded on purpose
    boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        Log.v("GameActivity", "started onCreate");

        row1 = findViewById(R.id.row1);
        row2 = findViewById(R.id.row2);
        row3 = findViewById(R.id.row3);

        wordGrid();

        addKeys(row1, new String[]{"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"});
        addKeys(row2, new String[]{"A", "S", "D", "F", "G", "H", "J", "K", "L"});
        addKeys(row3, new String[]{"⏎", "Z", "X", "C", "V", "B", "N", "M", "⌫"});
    }

    private void wordGrid() {
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 5; c++) {
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

        for (int i = 0; i < keys.length; i++) {
            final String text = keys[i];

            Button b = new Button(this);
            b.setText(text);
            b.setAllCaps(true);
            b.setTextSize(18);

            LinearLayout.LayoutParams p =
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);

            if (text.equals("⏎") || text.equals("⌫")) {
                p.weight = 4;
            } else {
                p.weight = 3;
            }

            if (text.equals("⏎") || text.equals("⌫")) {
                p.setMargins(0, 4, 0, 4);
            } else {
                p.setMargins(1, 4, 1, 4);
            }
            b.setLayoutParams(p);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (text.equals("⏎")) {
                        handleKeyPress("ENTER");
                    } else if (text.equals("⌫")) {
                        handleKeyPress("DEL");
                    } else {
                        handleKeyPress(text);
                    }
                }
            });

            row.addView(b);
        }
    }

    private void handleKeyPress(String key) {
        if (key.equals("ENTER")) {
            submitWord();
        } else if (key.equals("DEL")) {
            deleteLetter();
        } else {
            addLetter(key);
        }
    }

    private void addLetter(String letter) {
        if (gameOver) return;

        if (currentCol < 5) {
            cells[currentRow][currentCol].setText(letter);
            currentCol++;
        }
    }

    private void deleteLetter() {
        if (gameOver) return;

        if (currentCol > 0) {
            currentCol--;
            cells[currentRow][currentCol].setText("");
        }
    }

    private void submitWord() {
        if (gameOver) return;
        if (currentCol < 5) return;

        String guess = "";

        for (int i = 0; i < 5; i++) {
            guess += cells[currentRow][i].getText().toString();
        }

        checkGuess(guess);

        if (guess.equals(secretWord)) {
            gameOver = true;
            return;
        }

        currentRow++;
        currentCol = 0;

        if (currentRow == 6) {
            gameOver = true;
        }
    }

    private void checkGuess(String guess) {
        String[] brknGuess = new String[5];
        String[] brknAns = new String[5];
        boolean[] used = new boolean[5];
        int GREEN = 0xFF6AAA64, YELL = 0xFFC9B458, GRAY = 0xFF787C7E;
        for (int i = 0; i < brknGuess.length; i++) {
            brknGuess[i] = guess.substring(i, (i + 1));
            brknAns[i] = secretWord.substring(i, (i + 1));
        }
        //if the letter is correct, the next loop colours the letter green on the guessing grid and on the keyboard
        for (int i = 0; i < 5; i++) {
            boolean yesnt = false;
            if (brknGuess[i].equals(brknAns[i])) {
                cells[currentRow][i].setBackgroundColor(GREEN);
                colorKey(guess.charAt(i), GREEN);
                used[i] = true;
            }

            for (int j = 0; j < 5; j++) {
                int currentColor = ((ColorDrawable) cells[currentRow][i].getBackground()).getColor();

                if (currentColor == GREEN) {
                    // do nothing, already colored
                } else {
                    boolean found = false;
                    for (int j = 0; j < 5; j++) {
                        if (!used[j] && guess.charAt(i) == secretWord.charAt(j)) {
                            found = true;
                            used[j] = true; // mark as used
                            break;
                        }
                    }

                    if (found) {
                        cells[currentRow][i].setBackgroundColor(YELL);
                        colorKey(guess.charAt(i), YELL);
                    } else {
                        cells[currentRow][i].setBackgroundColor(GRAY);
                        colorKey(guess.charAt(i), GRAY);
                    }
                }
            }
        }
    }
}
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
    TextView[][] cells = new TextView[6][5]; // initialized with initGrid()
    GameLogic wordle;

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
        wordle = new GameLogic(cells, row1, row2, row3, "APPLE");

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
            wordle.submitWord();
        } else if (key.equals("DEL")) {
            wordle.deleteLetter();
        } else {
            wordle.addLetter(key);
        }
    }
}
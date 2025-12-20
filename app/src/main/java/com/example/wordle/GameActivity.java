package com.example.wordle;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameActivity extends AppCompatActivity {

    GridLayout keyboard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        Log.v("GameActivity", "started onCreate");

        keyboard = findViewById(R.id.keyboardlayout);
        createWordleKeyboard();
    }
    private void createWordleKeyboard() {

        String[] row1 = {"Q","W","E","R","T","Y","U","I","O","P"};
        String[] row2 = {"A","S","D","F","G","H","J","K","L"};
        String[] row3 = {"ENTER","Z","X","C","V","B","N","M","⌫"};

        addKeyboardRow(row1, 0);
        addKeyboardRow(row2, 1);
        addKeyboardRow(row3, 2);
    }

    private void addKeyboardRow(String[] keys, int row) {
        int col = 0;

        for (String text : keys) {
            Button b = new Button(this);
            b.setText(text);
            b.setAllCaps(true);
            b.setTextSize(14);

            int span = (text.equals("ENTER") || text.equals("⌫")) ? 2 : 1;

            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.rowSpec = GridLayout.spec(row);
            p.columnSpec = GridLayout.spec(col, span, 1f);
            p.width = 0;
            p.setMargins(4, 4, 4, 4);

            b.setLayoutParams(p);

            b.setOnClickListener(v ->
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            );

            keyboard.addView(b);
            col += span;

        /*for (int i = 0; i < letters.length; i++) {
            Button key = new Button(this);

            key.setText(letters[i]);
            key.setAllCaps(true);
            key.setTextSize(18);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec(rowIndex);
            params.columnSpec = GridLayout.spec(i);
            params.setMargins(4, 4, 4, 4); // Adjust if needed
            key.setLayoutParams(params);
            keyboard.addView(key);*/
        }
    }
}
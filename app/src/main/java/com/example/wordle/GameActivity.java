package com.example.wordle;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameActivity extends AppCompatActivity {

    TableLayout keyboard;
    TextView key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        key = new TextView(GameActivity.this);
        //keyboard = findViewById(R.id.keyboardLayout);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
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

    private void addKeyboardRow(String[] letters, int rowIndex) {

        for (int i = 0; i < letters.length; i++) {
            Button key = new Button(this);

            key.setText(letters[i]);
            key.setAllCaps(true);
            key.setTextSize(18);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams(); //parameters for the keyboard
            params.rowSpec = GridLayout.spec(rowIndex); //This tells the GridLayout which row the Button goes into.
            params.columnSpec = GridLayout.spec(i); //This tells the GridLayout which column the Button goes into. i is the zero-based column index within that row.
            params.setMargins(6, 6, 6, 6);

            key.setLayoutParams(params);
            key.setBackgroundResource(android.R.drawable.btn_default); // replace if needed

            keyboard.addView(key);
        }
    }
}
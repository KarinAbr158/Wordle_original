package com.example.wordle;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="guesses")
public class Guess {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int correctGuessNumCount;
    private int guessIndex;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCorrectGuessNumCount() {
        return this.correctGuessNumCount;
    }

    public void setCorrectGuessNumCount(int correctGuessNumCount) {
        this.correctGuessNumCount = correctGuessNumCount;
    }

    public int getGuessIndex() {
        return this.guessIndex;
    }

    public void setGuessIndex(int guessIndex) {
        this.guessIndex = guessIndex;
    }
}

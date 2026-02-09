package com.example.wordle;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "guesses")
public class Guess {

    @PrimaryKey(autoGenerate = true)
    private int id;

    // 1 = win, 0 = loss
    private int correctGuessNumCount;

    // 1–6 for win, 0 if lost
    private int guessIndex;

    // Username of the player
    private String username;

    // REQUIRED by Room
    public Guess() {
    }

    // Convenient constructor for inserts
    public Guess(int correctGuessNumCount, int guessIndex, String username) {
        this.correctGuessNumCount = correctGuessNumCount;
        this.guessIndex = guessIndex;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCorrectGuessNumCount() {
        return correctGuessNumCount;
    }

    public void setCorrectGuessNumCount(int correctGuessNumCount) {
        this.correctGuessNumCount = correctGuessNumCount;
    }

    public int getGuessIndex() {
        return guessIndex;
    }

    public void setGuessIndex(int guessIndex) {
        this.guessIndex = guessIndex;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}



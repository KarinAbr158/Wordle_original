package com.example.wordle;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "player_stats")
public class Guess {

    @PrimaryKey
    @NonNull private String username;

    private int currentStreak;
    private int maxStreak;
    private int gamesPlayed;
    private int totalWins;

    public Guess() {}

    public Guess(String username, int currentStreak, int maxStreak,
                 int gamesPlayed, int totalWins) {
        this.username = username;
        this.currentStreak = currentStreak;
        this.maxStreak = maxStreak;
        this.gamesPlayed = gamesPlayed;
        this.totalWins = totalWins;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getMaxStreak() { return maxStreak; }
    public void setMaxStreak(int maxStreak) { this.maxStreak = maxStreak; }

    public int getGamesPlayed() { return gamesPlayed; }
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }

    public int getTotalWins() { return totalWins; }
    public void setTotalWins(int totalWins) { this.totalWins = totalWins; }
}

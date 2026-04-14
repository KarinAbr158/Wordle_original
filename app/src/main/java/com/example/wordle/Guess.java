package com.example.wordle;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * מחלקה המייצגת שורה בטבלת הסטטיסטיקות של המשתמשים (Entity).
 */
@Entity(tableName = "player_stats")
public class Guess {

    @PrimaryKey
    @NonNull private String username;

    private int currentStreak;
    private int maxStreak;
    private int gamesPlayed;
    private int totalWins;

    /**
     * פעולה בונה ריקה עבור Room Database.
     */
    public Guess() {}

    /**
     * פעולה בונה המאתחלת את כל שדות הסטטיסטיקה עבור משתמש.
     * 
     * טענת כניסה: שם המשתמש, רצף נוכחי, רצף שיא, מספר משחקים ומספר ניצחונות.
     * טענת יציאה: נוצר אובייקט עם הנתונים המעודכנים.
     */
    public Guess(String username, int currentStreak, int maxStreak,
                 int gamesPlayed, int totalWins) {
        this.username = username;
        this.currentStreak = currentStreak;
        this.maxStreak = maxStreak;
        this.gamesPlayed = gamesPlayed;
        this.totalWins = totalWins;
    }

    /**
     * מחזירה את שם המשתמש.
     * טענת כניסה: אין.
     * טענת יציאה: שם המשתמש (String).
     */
    public String getUsername() { return username; }
    /**
     * מעדכנת את שם המשתמש.
     * טענת כניסה: שם משתמש (String).
     * טענת יציאה: המאפיין username מעודכן.
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * מחזירה את רצף הניצחונות הנוכחי.
     * טענת כניסה: אין.
     * טענת יציאה: רצף הניצחונות הנוכחי (int).
     */
    public int getCurrentStreak() { return currentStreak; }
    /**
     * מעדכנת את רצף הניצחונות הנוכחי.
     * טענת כניסה: רצף ניצחונות (int).
     * טענת יציאה: המאפיין currentStreak מעודכן.
     */
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    /**
     * מחזירה את רצף הניצחונות המקסימלי (שיא).
     * טענת כניסה: אין.
     * טענת יציאה: רצף השיא (int).
     */
    public int getMaxStreak() { return maxStreak; }
    /**
     * מעדכנת את רצף הניצחונות המקסימלי.
     * טענת כניסה: רצף שיא (int).
     * טענת יציאה: המאפיין maxStreak מעודכן.
     */
    public void setMaxStreak(int maxStreak) { this.maxStreak = maxStreak; }

    /**
     * מחזירה את סך המשחקים ששוחקו.
     * טענת כניסה: אין.
     * טענת יציאה: מספר המשחקים (int).
     */
    public int getGamesPlayed() { return gamesPlayed; }
    /**
     * מעדכנת את סך המשחקים ששוחקו.
     * טענת כניסה: מספר משחקים (int).
     * טענת יציאה: המאפיין gamesPlayed מעודכן.
     */
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }

    /**
     * מחזירה את סך הניצחונות.
     * טענת כניסה: אין.
     * טענת יציאה: מספר הניצחונות (int).
     */
    public int getTotalWins() { return totalWins; }
    /**
     * מעדכנת את סך הניצחונות.
     * טענת כניסה: מספר ניצחונות (int).
     * טענת יציאה: המאפיין totalWins מעודכן.
     */
    public void setTotalWins(int totalWins) { this.totalWins = totalWins; }
}

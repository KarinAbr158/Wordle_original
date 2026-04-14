package com.example.wordle;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;

/**
 * ממשק DAO המגדיר את השאילתות והפעולות מול טבלת הסטטיסטיקות ב-Room Database.
 */
@Dao
public interface GuessDAO {

    /**
     * מעדכנת שורת סטטיסטיקה קיימת בבסיס הנתונים.
     * טענת כניסה: אובייקט Guess המכיל את הנתונים המעודכנים.
     * טענת יציאה: השורה התואמת בבסיס הנתונים עודכנה.
     */
    @Update
    void update(Guess stats);

    /**
     * מוסיפה שורת סטטיסטיקה חדשה לבסיס הנתונים.
     * טענת כניסה: אובייקט Guess חדש.
     * טענת יציאה: שורה חדשה נוספה לטבלת player_stats.
     */
    @Insert
    void insert(Guess guess);

    /**
     * שולפת את אובייקט הסטטיסטיקה המלא עבור משתמש מסוים.
     * טענת כניסה: שם המשתמש (String).
     * טענת יציאה: אובייקט Guess הכולל את כל הנתונים, או null אם המשתמש לא נמצא.
     */
    @Query("SELECT * FROM player_stats WHERE username = :username LIMIT 1")
    Guess getStats(String username);

    /**
     * שולפת את מספר המשחקים ששוחקו עבור משתמש מסוים.
     * טענת כניסה: שם המשתמש (String).
     * טענת יציאה: מספר המשחקים (int).
     */
    @Query("SELECT gamesPlayed FROM player_stats WHERE username = :username")
    int getGamesPlayed(String username);

    /**
     * שולפת את מספר הניצחונות הכולל עבור משתמש מסוים.
     * טענת כניסה: שם המשתמש (String).
     * טענת יציאה: מספר הניצחונות (int).
     */
    @Query("SELECT totalWins FROM player_stats WHERE username = :username")
    int getTotalWins(String username);

    /**
     * שולפת את רצף הניצחונות הנוכחי עבור משתמש מסוים.
     * טענת כניסה: שם המשתמש (String).
     * טענת יציאה: רצף הניצחונות (int).
     */
    @Query("SELECT currentStreak FROM player_stats WHERE username = :username")
    int getCurrentStreak(String username);

    /**
     * שולפת את רצף הניצחונות המקסימלי (שיא) עבור משתמש מסוים.
     * טענת כניסה: שם המשתמש (String).
     * טענת יציאה: רצף השיא (int).
     */
    @Query("SELECT maxStreak FROM player_stats WHERE username = :username")
    int getMaxStreak(String username);
}

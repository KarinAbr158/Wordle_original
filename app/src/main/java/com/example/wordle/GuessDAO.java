package com.example.wordle;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import java.util.List;

@Dao
public interface GuessDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Guess stats);

    @Insert
    void insert(Guess guess);

    @Query("SELECT * FROM player_stats WHERE username = :username LIMIT 1")
    Guess getStats(String username);
    /*Finds the row with matching username
    Returns full stats object
    LIMIT 1 protects you from crashes if duplicates somehow exist.*/

    @Query("SELECT gamesPlayed FROM player_stats WHERE username = :username")
    int getGamesPlayed(String username);
    //Returns how many games the player has played.

    @Query("SELECT totalWins FROM player_stats WHERE username = :username")
    int getTotalWins(String username);
    //Returns numbers of wins

    @Query("SELECT currentStreak FROM player_stats WHERE username = :username")
    int getCurrentStreak(String username);
    //Returns player's current streak

    @Query("SELECT maxStreak FROM player_stats WHERE username = :username")
    int getMaxStreak(String username);
    //Returns player's max streak


}

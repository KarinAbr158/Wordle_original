package com.example.wordle;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GuessDAO {
    @Insert
    void insert(Guess guess);

    @Insert
    void insertAll(List<Guess> guess);

    @Delete
    void delete(Guess guess);

    @Query("SELECT * FROM guesses")
    List<Guess> getAllGuesses();

    @Query("SELECT * FROM guesses WHERE id = :id")
    Guess getGuessByID(int id);

    // Statistics queries (filtered by username)
    @Query("SELECT COUNT(*) FROM guesses WHERE username = :username")
    int getTotalGamesPlayed(String username);

    @Query("SELECT COUNT(*) FROM guesses WHERE username = :username AND correctGuessNumCount = 1")
    int getTotalWins(String username);

    @Query("SELECT guessIndex, COUNT(*) as count FROM guesses WHERE username = :username AND correctGuessNumCount = 1 GROUP BY guessIndex")
    List<GuessDistribution> getGuessDistribution(String username);
}

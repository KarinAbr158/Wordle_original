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
}

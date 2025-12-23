package com.example.wordle;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WordDAO {
    @Insert
    void insert(Word word);

    @Insert
    void insertAll(List<Word> word);

    @Delete
    void delete(Word word);

    @Query("SELECT * FROM words")
    List<Word> getAllWords();

    @Query("SELECT * FROM words WHERE id = :id")
    Word getWordByID(int id);
}

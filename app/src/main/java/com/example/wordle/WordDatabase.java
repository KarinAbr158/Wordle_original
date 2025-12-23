package com.example.wordle;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Word.class}, version = 1)
public abstract class WordDatabase extends RoomDatabase {
    private static WordDatabase instance;
    public abstract WordDAO wordDao();

    public static synchronized WordDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    WordDatabase.class,
                    "word_database")
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}

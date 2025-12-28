package com.example.wordle;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Guess.class}, version = 1)
public abstract class GuessDatabase extends RoomDatabase {
    private static GuessDatabase instance;
    public abstract GuessDAO wordDao();

    public static synchronized GuessDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                            GuessDatabase.class,
                            "word_database")
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}

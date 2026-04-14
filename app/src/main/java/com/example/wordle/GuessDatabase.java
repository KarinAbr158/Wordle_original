package com.example.wordle;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * מחלקת ה-Database המרכזית עבור Room. 
 * משתמשת בדפוס ה-Singleton כדי להבטיח קיום של מופע יחיד של מסד הנתונים.
 */
@Database(entities = {Guess.class}, version = 2)
public abstract class GuessDatabase extends RoomDatabase {
    private static GuessDatabase instance;

    /**
     * מחזירה את אובייקט ה-DAO לביצוע שאילתות.
     * טענת כניסה: אין.
     * טענת יציאה: אובייקט המימוש של GuessDAO.
     */
    public abstract GuessDAO guessDao();

    /**
     * מחזירה את המופע היחיד (Instance) של מסד הנתונים.
     * אם המופע לא קיים, הוא נוצר.
     * 
     * טענת כניסה: הקשר (Context).
     * טענת יציאה: מופע של GuessDatabase מוכן לשימוש.
     */
    public static synchronized GuessDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                            GuessDatabase.class,
                            "word_database")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}

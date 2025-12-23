package com.example.wordle;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="words")
public class Word {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String word;

    public int getID(){
        return this.id;
    }

    public void setID(int id){
        this.id = id;
    }

    public String getWord(){
        return this.word;
    }
}

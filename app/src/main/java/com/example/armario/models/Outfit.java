package com.example.armario.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "outfit")
public class Outfit {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "weather_temp")
    public double weatherTemp;

    @ColumnInfo(name = "weather_desc")
    public String weatherDesc;


    public Outfit(String date, double weatherTemp, String weatherDesc) {
        this.date = date;
        this.weatherTemp = weatherTemp;
        this.weatherDesc = weatherDesc;
    }

}
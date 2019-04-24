package com.example.armario.models;

import java.sql.Blob;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "clothing")
public class Clothing {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "main_color")
    public String mainColor;

    @ColumnInfo(name = "date_added")
    public String dateAdded;

    @ColumnInfo(name = "isWorn")
    public boolean isWorn;

    @ColumnInfo(name = "weather_type")
    public String weatherType;

    @ColumnInfo(name = "dress_type")
    public String dressType;

    @ColumnInfo(name = "is_favorite")
    public boolean isFavorite;

    @ColumnInfo(name = "image")
    public byte[] image;

    public Clothing(String name, String dateAdded, boolean isWorn, String weatherType, String dressType, String type, boolean isFavorite, byte[] image) {
        this.name = name;
        this.mainColor = "n/a";
        this.dateAdded = dateAdded;
        this.isWorn = isWorn;
        this.dressType = dressType;
        this.weatherType = weatherType;
        this.type = type;
        this.isFavorite = isFavorite;
        this.image = image;
    }

}
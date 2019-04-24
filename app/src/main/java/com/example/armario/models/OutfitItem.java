package com.example.armario.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "outfit_item")
public class OutfitItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "outfit")
    public int outfitId;

    @ColumnInfo(name = "clothing")
    public int clothingId;

    public OutfitItem(int outfitId, int clothingId) {
        this.outfitId = outfitId;
        this.clothingId = clothingId;
    }

}
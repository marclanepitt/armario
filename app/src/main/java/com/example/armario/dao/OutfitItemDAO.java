package com.example.armario.dao;

import com.example.armario.models.Clothing;
import com.example.armario.models.OutfitItem;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface OutfitItemDAO {
    @Query("SELECT * FROM outfit_item")
    List<OutfitItem> getOutfitItems();
    @Insert
    void insertAll(OutfitItem... items);

    @Query("SELECT * FROM outfit_item WHERE outfit = :id")
    List<OutfitItem> getOutfitItemByOutfitId(int id);

    @Delete
    void delete(OutfitItem item);

    @Query("DELETE FROM outfit_item WHERE outfit = :id")
    void deleteByOutfitId(int id);

}

package com.example.armario.dao;

import com.example.armario.models.Clothing;
import com.example.armario.models.Outfit;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface OutfitDAO {
    @Query("SELECT * FROM Outfit")
    List<Outfit> getOutfit();

    @Query("SELECT * FROM Outfit where id = :id")
    List<Outfit> getOutfitById(int id);

    @Query("SELECT * FROM OUTFIT where date= :date")
    List<Outfit> getOutfitByDate(String date);

    @Query("SELECT * FROM OUTFIT where id = (SELECT MAX(id) FROM outfit)")
    List<Outfit> getMostRecentOutfit();

    @Query("SELECT COUNT(*) FROM OUTFIT")
    int getOutfitCount();

    @Insert
    void insertAll(Outfit... outfits);

    @Delete
    void delete(Outfit outfit);

    @Query("DELETE FROM outfit WHERE date= :date")
    void deleteByDate(String date);
}

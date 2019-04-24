package com.example.armario.dao;

import com.example.armario.models.Clothing;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ClothingDAO {
    @Query("SELECT * FROM CLOTHING")
    List<Clothing> getClothing();
    @Insert
    void insertAll(Clothing... clothings);

    @Delete
    void delete(Clothing clothing);

    @Query("SELECT * FROM CLOTHING WHERE id = :id")
    List<Clothing> getClothingById(int id);

    @Query("UPDATE CLOTHING SET isWorn = :isWorn WHERE id = :id")
    void updateWorn(boolean isWorn, int id);

    @Query("UPDATE CLOTHING SET is_favorite = :isFavorite WHERE id = :id")
    void updateFavorite(boolean isFavorite, int id);

    @Query("UPDATE CLOTHING SET isWorn = 0")
    void emptyLaundry();

    @Query("SELECT avg(isWorn) FROM CLOTHING")
    double getWornClothingPercent();

    @Query("DELETE FROM CLOTHING")
    void deleteAll();

    @Query("SELECT * FROM CLOTHING WHERE type=:type AND isWorn=0")
    List<Clothing> getClothingByType(String type);

    @Query("SELECT * FROM CLOTHING WHERE type = :type AND weather_type = :weatherType AND dress_type = :dressType AND isWorn = 0")
    List<Clothing> getClothingWithTypes(String type, String weatherType, String dressType);

    @Query("SELECT * FROM CLOTHING WHERE name LIKE :queryString OR dress_type LIKE :queryString or type LIKE :queryString OR weather_type LIKE :queryString")
    List<Clothing> searchClothing(String queryString);
}

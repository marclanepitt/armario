package com.example.armario;

import android.content.Context;

import com.example.armario.dao.ClothingDAO;
import com.example.armario.dao.OutfitDAO;
import com.example.armario.dao.OutfitItemDAO;
import com.example.armario.models.Clothing;
import com.example.armario.models.Outfit;
import com.example.armario.models.OutfitItem;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Clothing.class, Outfit.class, OutfitItem.class}, version = 8)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ClothingDAO getClothingDao();
    public abstract OutfitDAO getOutfitDAO();
    public abstract OutfitItemDAO getOutfitItemDAO();

    private static volatile AppDatabase INSTANCE;

    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database").allowMainThreadQueries().addMigrations(M2_M3, M3_M4, M4_M5, M5_M6,M6_M7,M7_M8)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration M2_M3 = new Migration(2,3) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE clothing ADD COLUMN dress_type TEXT");
        }
    };

    static final Migration M3_M4 = new Migration(3,4) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE clothing ADD COLUMN image BLOB");
        }
    };

    static final Migration M4_M5 = new Migration(4,5) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE clothing ADD COLUMN name TEXT");
        }
    };

    static final Migration M5_M6 = new Migration(5,6) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE outfit(id INTEGER PRIMARY KEY NOT NULL, date TEXT, weather_temp REAL NOT NULL, weather_desc TEXT) ");
        }
    };

    static final Migration M6_M7 = new Migration(6,7) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE outfit_item(id INTEGER PRIMARY KEY NOT NULL, clothing INTEGER NOT NULL, outfit INTEGER NOT NULL) ");
        }
    };
    static final Migration M7_M8 = new Migration(7,8) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("DROP TABLE outfit_item");
            db.execSQL("CREATE TABLE outfit_item(id INTEGER PRIMARY KEY NOT NULL, clothing INTEGER NOT NULL, outfit INTEGER NOT NULL) ");
        }
    };


}


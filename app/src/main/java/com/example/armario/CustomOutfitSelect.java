package com.example.armario;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.armario.dao.ClothingDAO;
import com.example.armario.dao.OutfitDAO;
import com.example.armario.dao.OutfitItemDAO;
import com.example.armario.models.Clothing;
import com.example.armario.models.Outfit;
import com.example.armario.models.OutfitItem;

import java.util.List;

public class CustomOutfitSelect extends AppCompatActivity {
    ClothingDAO dao;
    OutfitDAO outfitDao;
    OutfitItemDAO outfitItemDao;
    Clothing topSelection = null;
    Clothing bottomSelection = null;
    Clothing shoeSelection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_outfit_select);
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        dao = db.getClothingDao();
        outfitDao = db.getOutfitDAO();
        outfitItemDao = db.getOutfitItemDAO();
        List<Clothing> tops = dao.getClothingByType("Top");
        List<Clothing> bottoms = dao.getClothingByType("Bottom");
        List<Clothing> shoes = dao.getClothingByType("Shoes");
        LinearLayout topContainer = findViewById(R.id.topcontainer);
        LinearLayout bottomContainer = findViewById(R.id.bottomcontainer);
        LinearLayout shoesContainer = findViewById(R.id.shoescontainer);
        final ImageView topI = findViewById(R.id.topselection);
        final ImageView bottomI = findViewById(R.id.bottomselection);
        final ImageView shoesI = findViewById(R.id.shoeselection);

        for(final Clothing c : tops) {
            ImageButton b = new ImageButton(getApplicationContext());
            final Bitmap bitmap = BitmapFactory.decodeByteArray(c.image, 0, c.image.length);
            b.setImageBitmap(bitmap);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(160,200);
            params.setMargins(10,0,0,0);
            b.setLayoutParams(params);
            b.setBackground(null);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    topSelection = c;
                    topI.setImageBitmap(bitmap);
                }
            });
            topContainer.addView(b);
        }
        for(final Clothing c : bottoms) {
            ImageButton b = new ImageButton(getApplicationContext());
            final Bitmap bitmap = BitmapFactory.decodeByteArray(c.image, 0, c.image.length);
            b.setImageBitmap(bitmap);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(160,200);
            params.setMargins(10,0,0,0);
            b.setLayoutParams(params);
            b.setBackground(null);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSelection = c;
                    bottomI.setImageBitmap(bitmap);
                }
            });
            bottomContainer.addView(b);
        }
        for(final Clothing c : shoes) {
            ImageButton b = new ImageButton(getApplicationContext());
            final Bitmap bitmap = BitmapFactory.decodeByteArray(c.image, 0, c.image.length);
            b.setImageBitmap(bitmap);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(160,200);
            params.setMargins(10,0,0,0);
            b.setLayoutParams(params);
            b.setBackground(null);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shoeSelection = c;
                    shoesI.setImageBitmap(bitmap);
                }
            });
            shoesContainer.addView(b);
        }

    }

    public void handleSubmit(View v) {
        if(topSelection != null && bottomSelection != null && shoeSelection != null) {
            Clothing[] outfit = {topSelection, bottomSelection, shoeSelection};
            Outfit o = outfitDao.getMostRecentOutfit().get(0);
            for(int i = 0; i < outfit.length; i++) {
                dao.updateWorn(true, outfit[i].id);
                OutfitItem oi = new OutfitItem(o.id,outfit[i].id);
                outfitItemDao.insertAll(oi);
            }
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}

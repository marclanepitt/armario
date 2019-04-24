package com.example.armario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;

import com.example.armario.dao.ClothingDAO;
import com.example.armario.dao.OutfitDAO;
import com.example.armario.dao.OutfitItemDAO;
import com.example.armario.models.Clothing;
import com.example.armario.models.Outfit;
import com.example.armario.models.OutfitItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecentOutfits extends AppCompatActivity {
    ClothingDAO dao;
    OutfitDAO outfitDao;
    OutfitItemDAO outfitItemDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_outfits);


        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        dao = db.getClothingDao();
        outfitDao = db.getOutfitDAO();
        outfitItemDao = db.getOutfitItemDAO();

        CalendarView cv = findViewById(R.id.calendarview);
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, dayOfMonth);
                String date = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
                List<Outfit> o = outfitDao.getOutfitByDate(date);
                List<OutfitItem> oi = null;
                if(o.size() > 0) {
                    oi = outfitItemDao.getOutfitItemByOutfitId(o.get(0).id);
                    if (oi.size() > 0) {
                        final Dialog dialog = new Dialog(RecentOutfits.this);
                        dialog.setContentView(R.layout.customdialog);
                        dialog.setTitle("Outfit from " + dayOfMonth + "/" + month + "/" + year);
                        List<OutfitItem> items = outfitItemDao.getOutfitItemByOutfitId(o.get(0).id);
                        ArrayList<Bitmap> clothings = new ArrayList<>();
                        for (OutfitItem item : items) {
                            Clothing c = dao.getClothingById(item.clothingId).get(0);
                            Bitmap b = BitmapFactory.decodeByteArray(c.image, 0, c.image.length);
                            clothings.add(b);
                        }
                        ImageView top = dialog.findViewById(R.id.calendartop);
                        top.setImageBitmap(clothings.get(0));
                        ImageView bottom = dialog.findViewById(R.id.calendarbottom);
                        bottom.setImageBitmap(clothings.get(1));
                        ImageView shoes = dialog.findViewById(R.id.calendarshoe);
                        shoes.setImageBitmap(clothings.get(2));

                        Button backBtn = dialog.findViewById(R.id.dialogok);
                        backBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                }

            }
        });
    }
}

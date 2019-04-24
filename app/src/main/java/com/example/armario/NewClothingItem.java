package com.example.armario;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.armario.dao.ClothingDAO;
import com.example.armario.models.Clothing;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewClothingItem extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_clothing_item);

        String[] types = {"Top", "Bottom", "Full-Body", "Shoes"};
        Spinner type = findViewById(R.id.typeinput);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        type.setAdapter(adapter);

        String[] weathertypes = {"Summer", "Fall", "Winter", "Spring"};
        Spinner weathertype = findViewById(R.id.weathertypeinput);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, weathertypes);
        weathertype.setAdapter(adapter);

        String[] dresstypes = {"Casual", "Business", "Fancy", "Athletic"};
        Spinner dresstype = findViewById(R.id.dresstypeinput);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dresstypes);
        dresstype.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("pic");
        ImageView v = new ImageView(getApplicationContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(400,500));
        v.setImageBitmap(imageBitmap);
        LinearLayout l = findViewById(R.id.picwrapper);
        l.addView(v);

    }

    public void cancelClothing(View v) {
        finish();
    }

    public void saveClothing(View v) {
        EditText name = findViewById(R.id.nameinput);
        Spinner type = findViewById(R.id.typeinput);
        Spinner weathertype = findViewById(R.id.weathertypeinput);
        Spinner dresstype = findViewById(R.id.dresstypeinput);

        Bundle extras = getIntent().getExtras();
        byte[] blob = (byte[]) extras.get("blob");

        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        Clothing a = new Clothing(name.getText().toString(), date, false, weathertype.getSelectedItem().toString(),dresstype.getSelectedItem().toString(), type.getSelectedItem().toString(),false, blob);
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        ClothingDAO dao = db.getClothingDao();
        dao.insertAll(a);
        finish();

    }
}

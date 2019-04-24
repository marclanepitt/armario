package com.example.armario;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.armario.dao.ClothingDAO;
import com.example.armario.models.Clothing;

import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ClosetActivity extends AppCompatActivity implements OnItemSelectedListener {

    private List<Clothing> allClothes;
    ClothingDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closet);

        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        dao = db.getClothingDao();
        allClothes = dao.getClothing();
        final LinearLayout l = findViewById(R.id.itemcontainer);

        EditText search = findViewById(R.id.searchinput);
        search.clearFocus();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                allClothes = dao.searchClothing("%"+s+"%");
                populateItems(l);
            }
        });
        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {hideKeyboard(v);}
            }
        });

        populateItems(l);
    }

    public void populateItems(LinearLayout l) {
        l.removeAllViews();
        for(final Clothing c : allClothes) {
            final CardView temp = new CardView(getApplicationContext());
            CardView.LayoutParams params = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, 500);
            params.setMargins(0,100,0,0);
            temp.setLayoutParams(params);
            temp.setMaxCardElevation(20);
            temp.setCardElevation(10);
            temp.setBackgroundResource(R.drawable.borderbottom);

            LinearLayout ll = new LinearLayout(getApplicationContext());
            ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            ll.setOrientation(LinearLayout.HORIZONTAL);

            ImageView v = new ImageView(getApplicationContext());
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(c.image, 0, c.image.length);
            v.setImageBitmap(imageBitmap);
            LinearLayout.LayoutParams paramsImage = new LinearLayout.LayoutParams(200, 300);
            paramsImage.setMargins(30,40,0,0);
            v.setLayoutParams(paramsImage);
            ll.addView(v);

            LinearLayout info = new LinearLayout(getApplicationContext());
            LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.MATCH_PARENT);
            infoParams.setMargins(20, 20, 0, 0);
            info.setLayoutParams(infoParams);
            info.setOrientation(LinearLayout.VERTICAL);

            TextView name = new TextView(getApplicationContext());
            name.setTextColor(Color.BLACK);
            name.setText(c.name);
            name.setTextSize(15);
            name.setTypeface(Typeface.create("cursive", Typeface.BOLD));
            TextView t = new TextView(getApplicationContext());
            t.setTextColor(Color.BLACK);
            t.setText(c.type);
            t.setTextSize(10);
            TextView wt = new TextView(getApplicationContext());
            wt.setTextColor(Color.BLACK);
            wt.setText(c.weatherType);
            wt.setTextSize(10);
            TextView dt = new TextView(getApplicationContext());
            dt.setTextColor(Color.BLACK);
            dt.setText(c.dressType);
            dt.setTextSize(10);

            info.addView(name);
            info.addView(t);
            info.addView(wt);
            info.addView(dt);


            LinearLayout actions = new LinearLayout(getApplicationContext());
            LinearLayout.LayoutParams actionParams = new LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.MATCH_PARENT);
            actionParams.setMargins(40, 0, 0, 0);
            actions.setLayoutParams(actionParams);
            actions.setOrientation(LinearLayout.VERTICAL);

            final ImageView star = new ImageView(getApplicationContext());
            if(c.isFavorite) { star.setImageResource(R.drawable.starsolid);star.setTag("solid"); } else { star.setImageResource(R.drawable.starempty); star.setTag("empty"); }
            LinearLayout.LayoutParams starparams = new LinearLayout.LayoutParams(100,100);
            starparams.setMargins(220, 5,0,50);
            star.setLayoutParams(starparams);
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dao.updateFavorite(!c.isFavorite, c.id);
                    if(star.getTag().toString() == "solid") {
                        star.setTag("empty");
                        star.setImageResource(R.drawable.starempty);
                    } else {
                        star.setTag("solid");
                        star.setImageResource(R.drawable.starsolid);
                    }
                }
            });

            final Button worn = new Button(getApplicationContext());
            String worntext;
            int wornresource;
            if(c.isWorn) { worntext = "Set Clean"; wornresource = R.drawable.greenborderbottombutton; } else { worntext = "Set Dirty";wornresource = R.drawable.redborderbottombutton; }
            worn.setText(worntext);
            worn.setTextColor(Color.WHITE);
            worn.setTextSize(12);
            worn.setBackgroundResource(wornresource);
            worn.setLayoutParams(new LinearLayout.LayoutParams(250,100));
            worn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dao.updateWorn(!c.isWorn, c.id);
                    if(worn.getText() == "Set Dirty") {
                        worn.setText("Set Clean");
                        worn.setBackgroundResource(R.drawable.greenborderbottombutton);
                    } else {
                        worn.setText("Set Dirty");
                        worn.setBackgroundResource(R.drawable.redborderbottombutton);
                    }
                }
            });
            final Button delete = new Button(getApplicationContext());
            delete.setText("Delete");
            delete.setTextColor(Color.WHITE);
            delete.setTextSize(12);
            delete.setBackgroundResource(R.drawable.redborderbottombutton);
            delete.setLayoutParams(new LinearLayout.LayoutParams(250,100));
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(ClosetActivity.this)
                            .setTitle("Delete clothing item")
                            .setMessage("Are you sure you want to delete this clothing item?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dao.delete(c);
                                    temp.setVisibility(View.GONE);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
            actions.addView(star);
            actions.addView(worn);
            actions.addView(delete);
            ll.addView(info);
            ll.addView(actions);
            temp.addView(ll);
            l.addView(temp);
        }
    }

    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(ClosetActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

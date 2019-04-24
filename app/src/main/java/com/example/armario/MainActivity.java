package com.example.armario;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.armario.dao.ClothingDAO;
import com.example.armario.dao.OutfitDAO;
import com.example.armario.dao.OutfitItemDAO;
import com.example.armario.models.Clothing;
import com.example.armario.models.Outfit;
import com.example.armario.models.OutfitItem;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements RequestResponse {

    ClothingDAO dao;
    OutfitDAO outfitDao;
    OutfitItemDAO outfitItemDao;
    boolean showBin = true;
    double temp;
    String tempDesc;
    ArrayList<Clothing> recommendedOutfit = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        dao = db.getClothingDao();
        outfitDao = db.getOutfitDAO();
        outfitItemDao = db.getOutfitItemDAO();
//        deleteData(dao);
        LocationManager lm = (LocationManager)getSystemService(getApplicationContext().LOCATION_SERVICE);
        try {
             Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
             double longitude = location.getLongitude();
             double latitude = location.getLatitude();
             RequestTask rt = new RequestTask();
             rt.delegate = this;
             rt.execute("http://api.openweathermap.org/data/2.5/find?lat=" + latitude + "&lon=" + longitude + "&cnt=1&appid=c268b3e0194df708e724d7c18a0040db");
        } catch (SecurityException e) {
            Log.v("error",e.toString());
        }

        setupLaundryPanel(dao);
        setupClosetPanel(dao);
    }

    private void populateOutfitData() {
        List<Clothing> shoes = dao.getClothingByType("Shoes");
        List<Clothing> tops = dao.getClothingByType("Top");
        List<Clothing> bottoms = dao.getClothingByType("Bottom");
        for(int i = 1; i < 23; i++) {
            Date d = new Date();
            d.setDate(d.getDate() - i);
            String date = new SimpleDateFormat("dd-MM-yyyy").format(d);
            Outfit o = new Outfit(date, temp, tempDesc);
            outfitDao.insertAll(o);
            int recentId = outfitDao.getMostRecentOutfit().get(0).id;
            OutfitItem shoe = new OutfitItem(recentId, shoes.get((int)Math.floor(Math.random()*shoes.size())).id);
            OutfitItem top = new OutfitItem(recentId, tops.get((int)Math.floor(Math.random()*tops.size())).id);
            OutfitItem bottom = new OutfitItem(recentId, bottoms.get((int)Math.floor(Math.random()*bottoms.size())).id);
            outfitItemDao.insertAll(shoe,top,bottom);
        }
    }

    private ArrayList<Clothing> makeRecommendationBlind() {
        Clothing top = null;
        Clothing bottom = null;
        Clothing fullBody = null;
        Clothing shoes = null;
        boolean error = false;
        String[] dTypes = {"Casual", "Athletic", "Business"};
        String dress = dTypes[(int)Math.floor(Math.random()*dTypes.length)];

        if(temp < 50) {
            List<Clothing> tops =  dao.getClothingWithTypes("Top", "Winter", dress);
            if(tops.size() > 0) {
                top = tops.get((int)Math.floor(Math.random()*tops.size()));
            } else {
                error = true;
            }
            List<Clothing> bottoms =  dao.getClothingWithTypes("Bottom", "Winter", dress);
            if(bottoms.size() > 0) {
                bottom = bottoms.get((int)Math.floor(Math.random()*tops.size()));
            } else {
                error = true;
            }
            List<Clothing> shoesL =  dao.getClothingWithTypes("Shoes", "Winter", dress);
            if(shoesL.size() > 0) {
                shoes = shoesL.get((int)Math.floor(Math.random()*tops.size()));
            } else {
                error = true;
            }
        } else if(temp >= 50 && temp < 70) {
            List<Clothing> tops =  dao.getClothingWithTypes("Top", "Spring", dress);
            if(tops.size() > 0) {
                top = tops.get((int)Math.floor(Math.random()*tops.size()));
            } else {
                error = true;
            }
            List<Clothing> bottoms =  dao.getClothingWithTypes("Bottom", "Spring", dress);
            if(bottoms.size() > 0) {
                bottom = bottoms.get((int)Math.floor(Math.random()*tops.size()));
            } else {
                error = true;
            }
            List<Clothing> shoesL =  dao.getClothingWithTypes("Shoes", "Spring", dress);
            if(shoesL.size() > 0) {
                shoes = shoesL.get((int)Math.floor(Math.random()*tops.size()));
            } else {
                error = true;
            }
        } else {
            List<Clothing> tops =  dao.getClothingWithTypes("Top", "Summer", dress);
            if(tops.size() > 0) {
                top = tops.get((int)Math.floor(Math.random()*tops.size()));
            } else {
                error = true;
            }
            List<Clothing> bottoms =  dao.getClothingWithTypes("Bottom", "Summer", dress);
            if(bottoms.size() > 0) {
                bottom = bottoms.get((int)Math.floor(Math.random()*tops.size()));
            } else {
                error = true;
            }
            List<Clothing> shoesL =  dao.getClothingWithTypes("Shoes", "Summer", dress);
            if(shoesL.size() > 0) {
                shoes = shoesL.get((int)Math.floor(Math.random()*tops.size()));
            } else {
                error = true;
            }
        }
        if(error) {
            return null;
        } else {
            ArrayList<Clothing> c = new ArrayList<>();
            c.add(top);
            c.add(bottom);
            c.add(shoes);
            return c;
        }
    }

    private ArrayList<Clothing> makeRecommendationLearned() {
        ArrayList<DenseInstance> trainRowsShoes = new ArrayList<>();

        ArrayList<DenseInstance> trainRowsBottoms = new ArrayList<>();
        ArrayList<double[]> bottomsData = new ArrayList<>();

        ArrayList<DenseInstance> trainRowsTops = new ArrayList<>();
        ArrayList<double[]> topsData = new ArrayList<>();

        List<Outfit> outfits = outfitDao.getOutfit();
        ArrayList<Clothing> shoes = new ArrayList<>();


        for(Outfit o : outfits) {
            if(outfitItemDao.getOutfitItemByOutfitId(o.id).size() > 0) {
                int topId = outfitItemDao.getOutfitItemByOutfitId(o.id).get(0).clothingId;
                int bottomId = outfitItemDao.getOutfitItemByOutfitId(o.id).get(1).clothingId;
                int shoeId = outfitItemDao.getOutfitItemByOutfitId(o.id).get(2).clothingId;

                Clothing shoe = dao.getClothingById(shoeId).get(0);
                double[] features = {o.weatherTemp, o.weatherDesc.hashCode()};
                DenseInstance d = new DenseInstance(features, shoe.id);
                trainRowsShoes.add(d);

                Clothing bottom = dao.getClothingById(bottomId).get(0);
                double[] data = {o.weatherTemp, o.weatherDesc.hashCode(), shoeId, bottom.id};
                bottomsData.add(data);

                Clothing top = dao.getClothingById(topId).get(0);
                double[] topData = {o.weatherTemp, o.weatherDesc.hashCode(), shoeId, bottom.id, top.id};
                topsData.add(topData);
            }
        }
        Dataset dset = new DefaultDataset();
        for(DenseInstance d : trainRowsShoes) {
            dset.add(d);
        }
        Classifier knn = new KNearestNeighbors(7);
        knn.buildClassifier(dset);
        double[] testFeatures = {temp, tempDesc.hashCode()};
        DenseInstance test = new DenseInstance(testFeatures);
        Object result = knn.classify(test);

        for(double[] featureList : bottomsData) {
            double[] features = {featureList[0], featureList[1], featureList[2]};
            DenseInstance d = new DenseInstance(features, featureList[3]);
            trainRowsBottoms.add(d);
        }
        dset = new DefaultDataset();
        for(DenseInstance d : trainRowsBottoms) {
            dset.add(d);
        }
        knn.buildClassifier(dset);
        double[] bottomTestFeatures = {temp, tempDesc.hashCode(), Double.parseDouble(result.toString())};
        test = new DenseInstance(bottomTestFeatures);
        Object bottomResult = knn.classify(test);

        //top learn
        for(double[] featureList : topsData) {
            double[] features = {featureList[0], featureList[1], featureList[2], featureList[3]};
            DenseInstance d = new DenseInstance(features, featureList[4]);
            trainRowsTops.add(d);
        }
        dset = new DefaultDataset();
        for(DenseInstance d : trainRowsTops) {
            dset.add(d);
        }
        knn.buildClassifier(dset);
        double[] topTestFeatures = {temp, tempDesc.hashCode(), Double.parseDouble(result.toString()),  Double.parseDouble(bottomResult.toString())};
        test = new DenseInstance(topTestFeatures);
        Object topResult = knn.classify(test);
        ArrayList<Clothing> outfitResult = new ArrayList<>();
        outfitResult.add(dao.getClothingById((int)Double.parseDouble(bottomResult.toString())).get(0));
        outfitResult.add(dao.getClothingById((int)Double.parseDouble(result.toString())).get(0));
        outfitResult.add(dao.getClothingById((int)Double.parseDouble(topResult.toString())).get(0));
        return outfitResult;
    }

    private int encodeTypeCombinations(String weatherType, String dressType) {
        return Integer.parseInt(weatherType+dressType);
    }

    private void deleteData(ClothingDAO dao) {
        dao.deleteAll();
    }

    private void setupLaundryPanel(ClothingDAO dao) {
        int d;
        double p = dao.getWornClothingPercent();
        if(p == 0) {
            d = R.drawable.a;
        } else if(p < .4) {
            d = R.drawable.b;
        } else if(p < .7) {
            d = R.drawable.c;
        } else {
            d = R.drawable.d;
        }
        ImageView i = findViewById(R.id.laundryimage);
        i.setImageResource(d);
    }

    public void toggleLaundryView(View v) {
        if(showBin) {
            findViewById(R.id.laundryimage).setVisibility(View.INVISIBLE);
            findViewById(R.id.laundrybutton).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.laundryimage).setVisibility(View.VISIBLE);
            findViewById(R.id.laundrybutton).setVisibility(View.INVISIBLE);
        }
        showBin = !showBin;
    }

    public void emptyLaundry(View v) {
        findViewById(R.id.laundryimage).setVisibility(View.VISIBLE);
        findViewById(R.id.laundrybutton).setVisibility(View.INVISIBLE);
        dao.emptyLaundry();
        setupLaundryPanel(dao);
    }

    public void toCustomSelect(View v) {
        Intent i = new Intent(MainActivity.this, CustomOutfitSelect.class);
        startActivityForResult(i, 3);
    }

    private void setupClosetPanel(ClothingDAO dao) {
    }

    public void openCloset(View v) {
        Intent i = new Intent(MainActivity.this, ClosetActivity.class);
        startActivityForResult(i, 2);
    }

    public void openRecentOutfits(View v) {
        Intent i = new Intent(MainActivity.this, RecentOutfits.class);
        startActivity(i);
    }

    public void openSettings(View v) {
        Intent i = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(i);
    }

    public void handleNewPhoto(View v) {
        Intent x = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(x, 1);
    }

    public void handleRecommendSelect(View v) {
        Outfit o = outfitDao.getMostRecentOutfit().get(0);
        for(Clothing c : recommendedOutfit) {
            dao.updateWorn(true, c.id);
            OutfitItem oi = new OutfitItem(o.id,c.id);
            outfitItemDao.insertAll(oi);
        }
        TextView error = findViewById(R.id.recommendError);
        error.setText("Outfit already selected today");
        error.setVisibility(View.VISIBLE);
        TextView rText = findViewById(R.id.recommendtext);
        rText.setVisibility(View.INVISIBLE);
        Button b1 = findViewById(R.id.recommendbutton);
        Button b2 = findViewById(R.id.custombutton);
        b1.setVisibility(View.INVISIBLE);
        b2.setVisibility(View.INVISIBLE);
        LinearLayout oImages = findViewById(R.id.outfitimages);
        oImages.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK ) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG,100, b);
            byte[] blob = b.toByteArray();
            Intent i = new Intent(MainActivity.this, NewClothingItem.class);
            i.putExtra("pic", imageBitmap);
            i.putExtra("blob", blob);
            MainActivity.this.startActivity(i);
        }
        if(requestCode == 2) {
            setupLaundryPanel(dao);
        }
        if(requestCode == 3 && resultCode == RESULT_OK) {
            TextView error = findViewById(R.id.recommendError);
            error.setText("Outfit already selected today");
            error.setVisibility(View.VISIBLE);
            TextView rText = findViewById(R.id.recommendtext);
            rText.setVisibility(View.INVISIBLE);
            Button b1 = findViewById(R.id.recommendbutton);
            Button b2 = findViewById(R.id.custombutton);
            b1.setVisibility(View.INVISIBLE);
            b2.setVisibility(View.INVISIBLE);
            LinearLayout oImages = findViewById(R.id.outfitimages);
            oImages.setVisibility(View.GONE);
        }
    }

    @Override
    public void processFinish(String output) {
        try {
            JSONObject data = new JSONObject(output);
            JSONArray list = data.getJSONArray("list");
            String city = list.getJSONObject(0).getString("name");
            temp = list.getJSONObject(0).getJSONObject("main").getDouble("temp");
            temp = ((temp-273.15) * (9d/5)) + 32;
            tempDesc = list.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("main");
            TextView t = findViewById(R.id.weathertext);
            t.setText(Math.round(temp)+"°F  "+tempDesc+"\n in " + city);
            //populateOutfitData();

            ArrayList<Clothing> outfit;
            if(outfitDao.getOutfitCount() < 20) {
                outfit = makeRecommendationBlind();
            } else {
                outfit = makeRecommendationLearned();
            }

            if(outfit == null) {
                TextView recommendationError = findViewById(R.id.recommendError);
                recommendationError.setVisibility(View.VISIBLE);
                Button b = findViewById(R.id.recommendbutton);
                b.setVisibility(View.GONE);
            } else {
                TextView tv = findViewById(R.id.recommendtext);
                tv.setText("Today's Outfit Recommendation:\n"+"A "+outfit.get(0).dressType + ", "+outfit.get(0).weatherType +" outfit");
                LinearLayout l = findViewById(R.id.outfitimages);
                ArrayList<Clothing> clothings = new ArrayList<>();
                for(Clothing c : outfit) {
                    clothings.add(c);
                    ImageView v = new ImageView(getApplicationContext());
                    Bitmap b = BitmapFactory.decodeByteArray(c.image, 0, c.image.length);
                    v.setImageBitmap(b);
                    LinearLayout.LayoutParams paramsImage = new LinearLayout.LayoutParams(200, 250);
                    paramsImage.setMargins(30,0,0,0);
                    v.setLayoutParams(paramsImage);
                    l.addView(v);
                }
                recommendedOutfit = clothings;
            }

            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            List<Outfit> outfits = outfitDao.getOutfitByDate(date);
            if(outfits.size() == 0) {
                Outfit newOutfit = new Outfit(date, temp, tempDesc);
                outfitDao.insertAll(newOutfit);
            } else {
                Outfit o = outfitDao.getMostRecentOutfit().get(0);
//                 outfitItemDao.deleteByOutfitId(o.id); // used for demo purposes
                List<OutfitItem> items = outfitItemDao.getOutfitItemByOutfitId(o.id);
                if(items.size() > 0) {
                    //outfit has been selected...hide everything
                    TextView error = findViewById(R.id.recommendError);
                    error.setText("Outfit already selected today");
                    error.setVisibility(View.VISIBLE);
                    TextView rText = findViewById(R.id.recommendtext);
                    rText.setVisibility(View.INVISIBLE);
                    Button b1 = findViewById(R.id.recommendbutton);
                    Button b2 = findViewById(R.id.custombutton);
                    b1.setVisibility(View.INVISIBLE);
                    b2.setVisibility(View.INVISIBLE);
                    LinearLayout oImages = findViewById(R.id.outfitimages);
                    oImages.setVisibility(View.GONE);
                }
            }

        } catch(JSONException e) {
            Log.v("error", e.toString());
        }

    }
}


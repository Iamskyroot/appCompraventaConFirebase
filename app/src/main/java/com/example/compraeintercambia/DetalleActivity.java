package com.example.compraeintercambia;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.compraeintercambia.adapters.DetailAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetalleActivity extends AppCompatActivity {
    private TextView tvUser, tvPrice, tvChange, tvDescription;
    private ImageButton iBtnCall,iBtnMassage, iBtnWhatssap;
    private ImageView backButton;
    private TextView actionBarTitle;
    private ViewPager vpImages;
    private LinearLayout indicatorLayout;
    private TextView dots[];

    //firebase
    private FirebaseDatabase database;
    private DatabaseReference dbRef;
//adapter
    private DetailAdapter detailAdapter;

//variables para obtener los datos
    private String key="";
    private String user="";
    private String price="";
    private String img1="";
    private String img2="";
    private String changeble="";
    private String tel="";
    private String description="";
    private List<String> images;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        backButton=findViewById(R.id.actionGoBack);
        actionBarTitle=findViewById(R.id.actionBarTitle);
        vpImages=findViewById(R.id.rvProductImages);
        tvUser=findViewById(R.id.tvUserName);
        tvPrice=findViewById(R.id.tvPriceProduct);
        tvChange=findViewById(R.id.tvChangebleStatus);
        tvDescription=findViewById(R.id.tvDetailDescription);
        indicatorLayout=findViewById(R.id.indicator_layout);
        iBtnCall=findViewById(R.id.iBtnCall);
        iBtnMassage=findViewById(R.id.iBtnMessage);
        iBtnWhatssap=findViewById(R.id.iBtnWhatssap);

        //set action bar title
        actionBarTitle.setText("Detalle");
        //set display home button
        getSupportActionBar().hide();

        //firebase variables initialization
        database=FirebaseDatabase.getInstance();
        dbRef=database.getReference("Products");
        images=new ArrayList<>();

        //obtener los datos enviados
        Bundle datos = getIntent().getExtras();

        //get extras
        key=datos.getString("key");

        //Toast.makeText(DetalleActivity.this, "key = "+key, Toast.LENGTH_SHORT).show();


        //call products
        getProduct();
        //call indicator dots
        setUpIndicators(0);
        //adapter
        detailAdapter = new DetailAdapter(this, images);
        //set listener to viewPager slide
        vpImages.addOnPageChangeListener(onPageChangeListener);

    }

    public void getProduct(){

        dbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                img1 = snapshot.child(key).child("img").getValue(String.class);
                img2 = snapshot.child(key).child("img2").getValue(String.class);
                price = snapshot.child(key).child("price").getValue(String.class);
                user = snapshot.child(key).child("userName").getValue(String.class);
                tel = snapshot.child(key).child("userTel").getValue(String.class);
                changeble = snapshot.child(key).child("changeble").getValue(String.class);
                description = snapshot.child(key).child("description").getValue(String.class);
                images.add(img1);
                images.add(img2);
                //Toast.makeText(DetalleActivity.this, "Two "+img2, Toast.LENGTH_LONG).show();

                vpImages.setAdapter(detailAdapter);
                //seting data in views
                tvUser.setText(user);
                tvPrice.setText(price+" XAF");
                if (changeble.equalsIgnoreCase("si")){
                    tvPrice.setText("Este articulo es intercambiable, consulte al propietario para mas informacion");
                    tvChange.setText("V");
                }else{
                    tvChange.setText("X");
                    tvChange.setTextColor(Color.RED);
                }
                tvDescription.setText("Descripcion:\n"+description);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //pbComprar.setVisibility(View.INVISIBLE);
                Toast.makeText(DetalleActivity.this, "Algo ha ido mal", Toast.LENGTH_SHORT).show();
                Log.e("COMPRA E INTERCAMBIA",error.getMessage());
            }
        });

    }

    //add dots indicator into indicator layout
    public void setUpIndicators(int position){
        dots = new TextView[2];
        indicatorLayout.removeAllViews();
        for (int i=0;i<dots.length;i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.black));
            indicatorLayout.addView(dots[i]);
        }
        dots[position].setTextColor(getResources().getColor(R.color.blue_1));
    }

    //add even on ViewPager
    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setUpIndicators(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    //-----------------contact----------
    //call
    public void call(View v){
        Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+tel));
        startActivity(call);
    }

    //send message
    public void sendMessage(View v){
        Intent message = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"+tel));
        startActivity(message);
    }

    //send whatsapp
    public void sendWhatsapp(View v){
        //check if is installed
        if (isWhatsappInstalled()){
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://api.whatsapp.com/send?phone="+tel));
            startActivity(intent);
        }else{
            Toast.makeText(DetalleActivity.this, "Whatsapp no esta instalado", Toast.LENGTH_SHORT).show();
        }
    }
    //check if whatsapp is installed in the user device
    public boolean isWhatsappInstalled(){
        PackageManager packageManager = getPackageManager();
        PackageManager packageManager2 = getPackageManager();
        boolean whatsappInstalled;
        try {
            Object whatsapp = packageManager.getPackageInfo("com.gbwhatsapp",PackageManager.GET_ACTIVITIES);
            Object GBwhatsapp = packageManager2.getPackageInfo("com.whatsapp",PackageManager.GET_ACTIVITIES);
            if (whatsapp !=null || GBwhatsapp !=null){

                whatsappInstalled = true;
            }
        }catch (Exception e){
            whatsappInstalled = false;
        }
        return true;
    }

    public void goBack(View v){
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}


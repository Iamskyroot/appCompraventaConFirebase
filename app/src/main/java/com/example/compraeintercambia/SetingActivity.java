package com.example.compraeintercambia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SetingActivity extends AppCompatActivity {

    private TextView actionbarTitle, etName,etEmail;
    private ImageView perfilImg;
    private EditText UserName,UserEmail,UserTel;
    private AppCompatButton btnEdit,btnSave;
    private LinearLayout hiddenLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seting);

        actionbarTitle = findViewById(R.id.actionBarTitle);
        etName=findViewById(R.id.setingUser);
        etEmail=findViewById(R.id.setingEmail);
        perfilImg=findViewById(R.id.setingImg);
        UserName=findViewById(R.id.etUserName);
        UserEmail=findViewById(R.id.etUserEmail);
        UserTel=findViewById(R.id.etUserTel);
        btnEdit=findViewById(R.id.btnEdit);
        btnSave=findViewById(R.id.btnSave);
        hiddenLayout=findViewById(R.id.perfilInputs);

        actionbarTitle.setText("Ajustes");

        getSupportActionBar().hide();

    }


    public void editPerfil(View v){
        if (hiddenLayout.getVisibility()==View.INVISIBLE){

        }else{
            hiddenLayout.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);
            btnEdit.setEnabled(false);
        }
    }

    public void goBack(View v){
        finish();
    }

}
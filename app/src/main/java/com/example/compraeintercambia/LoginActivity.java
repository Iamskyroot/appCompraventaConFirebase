package com.example.compraeintercambia;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.compraeintercambia.model.User;
import com.example.compraeintercambia.otrhers.CheckInternetConection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    ProgressBar pbLogin;
    EditText etUserName, etUserPassw;
    //firebase
    private FirebaseAuth mAuth;
    //views dialog_layout
    private ImageView ivDialogo, backButton;
    private TextView tvDialogo, tvRegister, actionBarTitle;

    private ProgressDialog progressDialog;

    private String user,passw;
    Handler handler = new Handler();
    CheckInternetConection internetConection;

    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private String urlWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //referencias visuales
        backButton=findViewById(R.id.actionGoBack);
        actionBarTitle=findViewById(R.id.actionBarTitle);
        tvRegister=findViewById(R.id.tvRegister);
        /*pbLogin=findViewById(R.id.pbLogin);*/
        etUserName=findViewById(R.id.etUserNameLogin);
        etUserPassw=findViewById(R.id.etUserPasswLogin);

        //set action bar title
        actionBarTitle.setText("Iniciar sesión");

        getSupportActionBar().hide();

        internetConection = new CheckInternetConection(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Procesando...");

        //firebase
        mAuth=FirebaseAuth.getInstance();

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SingupActivity.class);
                startActivity(intent);
            }
        });

    }

    //on start


    @Override
    protected void onStart() {
        super.onStart();

    }

    /*public void cleanInputs(){
        pbLogin.setVisibility(View.GONE);
        etUserName.setText(null);
        etUserPassw.setText(null);
    }*/

    public void iniciarSesion(View v){

        user=etUserName.getText().toString().trim();
        passw=etUserPassw.getText().toString().trim();

        if (user.isEmpty() || passw.isEmpty()){
            Toast.makeText(this, "Por favor rellene los campos vacios", Toast.LENGTH_SHORT).show();
        }else{
            if (!Patterns.EMAIL_ADDRESS.matcher(user).matches()){
                etUserName.setError("Por favor escriba un coreo valido!");
            }else{

                if (internetConection.isNetworkAvailable()) {
                    progressDialog.show();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            login();
                            //searchWebService();
                        }
                    });
                }else {
                    Toast.makeText(this, "Por favor revisa la conexion a internet e intenta de nuevo", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    /*private void searchWebService() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Inicio de sesion");
        progressDialog.setMessage("Procesando...");
        progressDialog.show();
        urlWebService = "http://192.168.56.1/compraeintercambia/SearchUser.php?email="+etUserName.getText().toString()+"" +
                "&password="+etUserPassw.getText().toString()+"";
        urlWebService = urlWebService.replace(" ","%20");
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,urlWebService,null,this,this);
        requestQueue.add(jsonObjectRequest);

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "Acceso denegad, por favor compruebe su usuario y contrasena", Toast.LENGTH_SHORT).show();
        Log.i("COMPRA E INTERCAMBIA",error.getMessage());
        progressDialog.dismiss();
    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialog.dismiss();

        User myUser = new User();

        try {
            JSONArray jsonArray = response.getJSONArray("user");
            JSONObject jsonObject =null, jsonObject1 =null,jsonObject2=null;

            jsonObject = jsonArray.getJSONObject(1);
            jsonObject1 = jsonArray.getJSONObject(2);
            jsonObject2 = jsonArray.getJSONObject(3);
            myUser.setName(jsonObject.optString("username"));
            myUser.setEmail(jsonObject1.optString("email"));
            myUser.setPassword(jsonObject2.optString("password"));

            Toast.makeText(getApplicationContext(), "Bienvenido "+myUser.getName(), Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            Log.i("error",e.getMessage());
        }


    }*/

    public void login(){
        mAuth.signInWithEmailAndPassword(user,passw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    progressDialog.dismiss();

                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Error al iniciar sesion", Toast.LENGTH_SHORT).show();

            }
        });
    }



    public void goBack(View v){
        finish();
    }


}
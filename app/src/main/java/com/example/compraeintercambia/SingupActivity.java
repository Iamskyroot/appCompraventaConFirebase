package com.example.compraeintercambia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.compraeintercambia.model.User;
import com.example.compraeintercambia.otrhers.CheckInternetConection;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import org.json.JSONObject;

public class SingupActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView  actionBarTitle;
    private EditText etUserName,etUserTel,etUserEmail,etUserPassw,etUserPassw2, lastInput;
    private CheckBox checkUserType1, checkUserType2;
    private ProgressBar pbSingUp;
    private CountryCodePicker ccPicker;
    private String userName, userTel,userEmail,userPassw,userPassw2,userType1,userType2="", userEspecification;
    //google autentication
    private FirebaseAuth mAuth;
    User user = new User();
    CheckInternetConection internetConection;
    private ProgressDialog progressDialog;
    Handler handler = new Handler();

    private RequestQueue requestQueue;
    private JsonObjectRequest objectRequest;
    private String urlWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        //obteniendo referencias de los campos
        backButton=findViewById(R.id.actionGoBack);
        actionBarTitle=findViewById(R.id.actionBarTitle);
        etUserName=findViewById(R.id.etUserName);
        etUserTel=findViewById(R.id.etUserTel);
        etUserEmail=findViewById(R.id.etUserEmail);
        etUserPassw=findViewById(R.id.etUserPassw);
        etUserPassw2=findViewById(R.id.etUserPassw2);
        checkUserType1=findViewById(R.id.checkUserType1);
        checkUserType2=findViewById(R.id.checkUserType2);
        lastInput=findViewById(R.id.lastInput);
        /*pbSingUp=findViewById(R.id.pbSingUp);*/
        ccPicker=findViewById(R.id.cCodePicker);
        /*codeNumber=ccPicker.getSelectedCountryCode();*/
        /*ccPicker.setFullNumber(ccPicker.getSelectedCountryCode()+userTel);*/
        ccPicker.registerCarrierNumberEditText(etUserTel);
        //tipo de usuario por defecto
        checkUserType1.setChecked(true);
        checkUserType1.setEnabled(false);

        //set action bar title
        actionBarTitle.setText("Registrarse");
        getSupportActionBar().hide();

        internetConection = new CheckInternetConection(getApplicationContext());
        //--------------------------------google autentication--------------------------
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        requestQueue = Volley.newRequestQueue(this);

        showInvisibleInput();

    }

    public void cleanInputs(){
        etUserName.setText(null);
        etUserTel.setText(null);
        etUserEmail.setText(null);
        etUserPassw.setText(null);
        etUserPassw2.setText(null);
        checkUserType2.setChecked(false);
    }

    public void getDataFromInput(){
        userName=etUserName.getText().toString().trim();
        userTel=etUserTel.getText().toString().trim();
        userEmail=etUserEmail.getText().toString().trim();
        userPassw=etUserPassw.getText().toString().trim();
        userPassw2=etUserPassw2.getText().toString().trim();
        userType1=checkUserType1.getText().toString();
        userType2=checkUserType2.getText().toString();
        userEspecification = lastInput.getText().toString();
    }

    //check data entry and register user to firebase
    public void registrarUsuario(View v){
        getDataFromInput();

        if (userName.isEmpty() || userTel.isEmpty() || userEmail.isEmpty() || userPassw.isEmpty() || userPassw2.isEmpty()) {
            Toast.makeText(this, "Este campo es requerido!", Toast.LENGTH_SHORT).show();
            //email and password validation
        }else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                etUserEmail.setError("Por favor escriba un coreo valido");
        }else if (userPassw.length() < 6){
                etUserPassw.setError("Por favor escriba una contrasena de 6 digitos como minimo");
        }else if(!checkPhoneNumber()){
            etUserTel.setError("Numero de telefono no valido");

        }else if(!userPassw.equals(userPassw2)){
            etUserPassw.setError("Las contraseñas no coinciden");
            etUserPassw2.setError("Las contraseñas no coinciden");

        }else if(lastInput.getVisibility()==View.VISIBLE && userEspecification.isEmpty()){
            lastInput.setError("Este campo es requerido para los usuarios tecnicos");
        }
        else{
            if (internetConection.isNetworkAvailable()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        createUser();
//                        searchWebService();
                    }
                });
            }
        }

    }
//enviar datos al webService
   /* private void searchWebService() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Registro de usuario");
        progressDialog.setMessage("Procesando...");
        progressDialog.show();
        urlWebService = "http://192.168.56.1/compraeintercambia/InsertUser.php?username="+userName+"&email="+userEmail+"&password="+userPassw+"&phone="+userTel
                +"&type="+userType1+"";
        urlWebService = urlWebService.replace(" ","%20");
        objectRequest = new JsonObjectRequest(Request.Method.GET,urlWebService,null,this,this);
        requestQueue.add(objectRequest);
    }

    @Override
    public void onResponse(JSONObject response) {
        cleanInputs();
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), "Usuario registrado con exito!", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), "Error al registrar usuario ", Toast.LENGTH_LONG).show();
        Log.i("COMPRA E INTERCAMBIA",error.getMessage());
    }*/

    //create a new user with email and password
    public void createUser(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Registro de usuario");
        progressDialog.setMessage("Procesando...");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(userEmail,userPassw)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //comprobar si se ha creado el usuario
                        if (task.isSuccessful()){

                            //store aditional data for de user
                            user.setName(userName);
                            user.setTel(ccPicker.getFullNumberWithPlus());
                            user.setEmail(userEmail);
                            user.setPassword(userPassw);

                            user.setType(userType1);
                            user.setType2(userType2);
                            user.setEspecification(userEspecification);

                            setUserData();
                            Toast.makeText(SingupActivity.this, "Registrado con éxito!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }else{
                            Toast.makeText(SingupActivity.this, "Error al crear usuario", Toast.LENGTH_SHORT).show();
                            /*pbSingUp.setVisibility(View.INVISIBLE);*/
                            progressDialog.dismiss();
                        }

                    }
                });

    }

    //create node users, set data and id
    public void setUserData(){
        //create node users, set data and id
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    /*pbSingUp.setVisibility(View.INVISIBLE);*/
                    cleanInputs();
                }
            }
        });
    }

    public boolean checkPhoneNumber(){

        if (ccPicker.isValidFullNumber()){
            return true;
        }else{
            return false;
        }

    }

    public boolean showInvisibleInput(){

        checkUserType2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkUserType2.isChecked()){
                    userType2 +="Tecnico";
                    lastInput.setVisibility(View.VISIBLE);
                }
                if(!checkUserType2.isChecked()){
                    lastInput.setVisibility(View.GONE);

                }
            }
        });
        return true;
    }

    public void goBack(View v){
        finish();
    }


}
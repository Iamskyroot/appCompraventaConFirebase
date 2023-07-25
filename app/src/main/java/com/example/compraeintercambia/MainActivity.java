package com.example.compraeintercambia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.compraeintercambia.otrhers.CheckInternetConection;
import com.example.compraeintercambia.otrhers.DialogoMensaje;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    //Declaracion de los objetos de la vista del menu lateral
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView tvUserEmail, tvUserName,tvWelcome,tvQuestion;
    private ImageView ivUser;

    FragmentHome fHome;
    FragmentManager fManager = getSupportFragmentManager();
    FragmentTransaction fTransaction = fManager.beginTransaction();

    //declaracion de activities
    AboutActivity activityAbout;
    LoginActivity activityLogin;
    SingupActivity activitySingin;
    ComprarActivity activityComprar;
    VenderActivity activityVender;
    IntercambiarActivity activityIntercambiar;


    /*//connect to firebase
    FirebaseDatabase database;
    DatabaseReference myRef;*/
    private FirebaseUser firebaseUser;
    private DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Users");


    private String userName,userEmail,userTel,userType, prefUser,prefEmail,prefTel;

    CheckInternetConection internetConection;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //------------------------------------MENU----------------------
        //obteniendo las referencias
        drawerLayout=findViewById(R.id.drawerLayout);
        navigationView=findViewById(R.id.navigationView);
        tvUserName=findViewById(R.id.tvUserName);
        tvWelcome=findViewById(R.id.tvWelcome);
        tvQuestion=findViewById(R.id.tvQuestion);
        //header menu
        View header = navigationView.getHeaderView(0);
        tvUserEmail=header.findViewById(R.id.tvUserEmail);
        tvUserName=header.findViewById(R.id.tvUserName);
        ivUser=header.findViewById(R.id.ivUserImage);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open_menu,R.string.close_menu);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        //añade un icono de regreso cuando se despliega el menu lateral
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //añadir eventos para cuando se pulse un item del menu lateral
        navigationView.setNavigationItemSelectedListener(this);

        internetConection = new CheckInternetConection(this);

        //-----------------------

/*// Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");

        myRef.setValue("Conectado!");*/


        /*handler.post(new Runnable() {
            @Override
            public void run() {
                    // Read from the database
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            String value = dataSnapshot.getValue(String.class);
                            Log.d("COMPRA E INTERCAMBIA", "Value is: " + value);

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w("COMPRA E INTERCAMBIA", "Failed to read value.", error.toException());
                        }
                    });

            }
        });*/
        getUserFromPreferences();


    }

    @Override
    public void onStart() {
        super.onStart();

        if (internetConection.isNetworkAvailable()) {

            handler.post(new Runnable() {
                @Override
                public void run() {

                    getUserFromFirebase();

                }
            });

        }else{
            Toast.makeText(this, "Por favor revisa la conexion a internet e intenta de nuevo", Toast.LENGTH_LONG).show();
        }

        animateText();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //controlando el item seleccionado con un Switch
        switch(item.getItemId()){

            case R.id.nav_home:
                Log.i("INTERCAMBIO","Se ha clickeado Principal");
                drawerLayout.closeDrawer(GravityCompat.START);
                fHome =new FragmentHome();
                fTransaction.addToBackStack("Home");
//                        fTransaction.add(R.id.fragmentContainer,fHome);
                fTransaction.commit();
                break;

            case R.id.nav_about:
                Log.i("INTERCAMBIO","Se ha clickeado Acerca de");
                drawerLayout.closeDrawer(GravityCompat.START);
                activityAbout= new AboutActivity();
                Intent iAbout=new Intent(MainActivity.this,AboutActivity.class);
                startActivity(iAbout);
                break;

            case R.id.nav_share:
                Log.i("INTERCAMBIO","Se ha clickeado Compartir");
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_exit:
                Log.i("INTERCAMBIO","Se ha clickeado Salir");
                drawerLayout.closeDrawer(GravityCompat.START);
                //mostrar una ventana emergente para cerrar la aplicacion
                DialogoMensaje dialogo = new DialogoMensaje("Seguro que desea salir de la aplicacion?");
                dialogo.show(getSupportFragmentManager(),"salir");
                dialogo.procesarRespuesta(new DialogoMensaje.Respuesta() {
                    @Override
                    public void confirmar(DialogFragment dialogo) {
                        finish();
                    }
                    @Override
                    public void cancelar(DialogFragment dialogo) {

                    }
                });
                break;

            case R.id.nav_login:
                Log.i("INTERCAMBIO","Se ha clickeado Iniciar sesion");
                drawerLayout.closeDrawer(GravityCompat.START);
                activityLogin= new LoginActivity();
                Intent iLogin=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(iLogin);
                break;

            case R.id.nav_singin:
                Log.i("INTERCAMBIO","Se ha clickeado Registrarse");
                drawerLayout.closeDrawer(GravityCompat.START);
                activitySingin= new SingupActivity();
                Intent iSingin=new Intent(MainActivity.this,SingupActivity.class);
                startActivity(iSingin);
                break;

            case R.id.nav_logout:
                Log.i("INTERCAMBIO","Se ha clickeado Cerrar sesion");
                FirebaseAuth.getInstance().signOut();
                ivUser.setImageResource(R.drawable.no_logged);
                tvUserName.setText("Demo User");
                tvUserEmail.setText("demoemail@gmail.com");
                Toast.makeText(MainActivity.this, "Se ha cerrado la sesion", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;


        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.seting_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        if (id == R.id.menuLogin){
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
        }

        if (id == R.id.menuSetting){
            Intent intent = new Intent(MainActivity.this,SetingActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    //cambio de ventanas

    public void comprar(View v){

        //activityComprar= new ComprarActivity();
        Intent intent=new Intent(this,ComprarActivity.class);
        intent.putExtra("user",userName);
        startActivity(intent);

    }

    public void vender(View v){

            if((FirebaseAuth.getInstance().getCurrentUser() != null)) {
                //activityVender = new VenderActivity();
                Intent intent = new Intent(this, VenderActivity.class);
                intent.putExtra("user",prefUser);
                intent.putExtra("tel",prefTel);
                startActivity(intent);
            }else{
                Toast.makeText(MainActivity.this, "Error, primero debe iniciar sesion!", Toast.LENGTH_SHORT).show();
            }

    }

    public void intercambiar(View v){

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            //activityVender = new VenderActivity();
            Intent intent=new Intent(this,IntercambiarActivity.class);
            intent.putExtra("user",prefUser);
            intent.putExtra("tel",prefTel);
            startActivity(intent);
        }else{
            Toast.makeText(MainActivity.this, "Error, primero debe iniciar sesion!", Toast.LENGTH_SHORT).show();
        }


    }

    public void reparar(View v){
        Intent intent=new Intent(this,RepararActivity.class);
        startActivity(intent);
    }

    public void getUserFromFirebase(){

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            //get current user
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userName = snapshot.child(firebaseUser.getUid()).child("name").getValue(String.class);
                    userTel = snapshot.child(firebaseUser.getUid()).child("tel").getValue(String.class);
                    userEmail = snapshot.child(firebaseUser.getUid()).child("email").getValue(String.class);
                    userType = snapshot.child(firebaseUser.getUid()).child("type").getValue(String.class);

                    saveUserToPreference(userName, userEmail, userTel, userType);

                    Toast.makeText(getApplicationContext(), "Bienvenido " + userName, Toast.LENGTH_SHORT).show();
                    ivUser.setImageResource(R.drawable.logged);
                    tvUserName.setText(userName);
                    tvUserEmail.setText(userEmail);
                    tvWelcome.setText("BIENVENIDO "+userName+"!");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i("COMPRA E INTERCAMBIA", error.getMessage());
                }
            });

        } else {
            ivUser.setImageResource(R.drawable.no_logged);
            tvUserName.setText("Demo User");
            tvUserEmail.setText("demoemail@gmail.com");
            //tvWelcome.setText("BIENVENIDO Demo!");
        }

    }

    public void saveUserToPreference(String user, String email, String tel, String type){

        SharedPreferences preferences = getSharedPreferences("userData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Name", user);
        editor.putString("Email", email);
        editor.putString("Telefono", tel);
        editor.putString("User type", type);
        editor.commit();
        /*Toast.makeText(MainActivity.this, "Datos de usuario guardados", Toast.LENGTH_SHORT).show();*/

    }

    public void getUserFromPreferences(){

        SharedPreferences preferences = getSharedPreferences("userData", Context.MODE_PRIVATE);

        prefUser = preferences.getString("Name",null);
        prefEmail = preferences.getString("Email",null);
        prefTel = preferences.getString("Telefono",null);

        if (prefUser==null){

            prefUser = "Demo";

        }else if(prefEmail==null){

            prefEmail = "demo@gmail.com";

        }else{
            ivUser.setImageResource(R.drawable.logged);
            tvUserName.setText(prefUser);
            tvUserEmail.setText(prefEmail);
            tvWelcome.setText("BIENVENIDO "+prefUser+"!");
        }


    }


    public void animateText(){

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_main);
        animation.setDuration(2000);
        tvWelcome.startAnimation(animation);
        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_main2);
        animation2.setDuration(2000);
        tvQuestion.startAnimation(animation2);

    }




}
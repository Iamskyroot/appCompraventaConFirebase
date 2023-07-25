package com.example.compraeintercambia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.compraeintercambia.adapters.AdaptadorProductos;
import com.example.compraeintercambia.otrhers.InterfaceItem;
import com.example.compraeintercambia.model.Products;
import com.example.compraeintercambia.otrhers.Notifications;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class ComprarActivity extends AppCompatActivity implements InterfaceItem {

    //objetos visuales
    private TextView actionBarTitle;
    private ImageView backButton;
    private RelativeLayout relativeLayout;
    private Spinner spCategoria;
    private GridView gridProducts;
    private ProgressBar pbComprar;
    private int filtro;

    private List<Products> productsList;
    private Products products;
    //categorias de productos
    private String categoria [] = {"Todos los productos","En venta","Intercambiables","Telefonos","Ordenadores","Televisores","Vestidos", "Otros"};
    //firebase
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    /*private Task<DataSnapshot> snapshotTask;*/
    Handler handler = new Handler();
    AdaptadorProductos adaptadorProductos;
    Notifications notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprar);
        //referencias visuales
        //searchProduct=findViewById(R.id.searchProduct);
        /*backButton=findViewById(R.id.actionGoBack);
        actionBarTitle=findViewById(R.id.actionBarTitle);*/
        relativeLayout=findViewById(R.id.container_comprar);
        spCategoria=findViewById(R.id.spCategory);
        gridProducts=findViewById(R.id.gridProducts);
        pbComprar=findViewById(R.id.pbComprar);
        gridProducts=findViewById(R.id.gridProducts);

        //set progressBar visible
        pbComprar.setVisibility(View.VISIBLE);


        //poner datos en el spinner
        ArrayAdapter<String> adaptador=new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,categoria);
        spCategoria.setAdapter(adaptador);

        //set action bar title
        /*actionBarTitle.setText("Comprar");*/

        getSupportActionBar().setTitle("Comprar");

        notifications = new Notifications(this);

        productsList = new ArrayList<>();
        products = new Products();
        //firebase variables initialization
        storage=FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();
        dbRef=database.getReference("Products");
        adaptadorProductos= new AdaptadorProductos(productsList,ComprarActivity.this);
        gridProducts.setAdapter(adaptadorProductos);
        adaptadorProductos.setInterfaceItem(ComprarActivity.this);


        filtro= spCategoria.getSelectedItemPosition();
        //filter category
        spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position < categoria.length) {
                    //get list of products
                    getList(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }


//add product to the list
    private List<Products> getList(int position) {

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //
                productsList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    products = postSnapshot.getValue(Products.class);
                    products.setId(postSnapshot.getKey());
                    /*productsList.add(products);*/
                    switch (position){
                        case 0:
                            productsList.add(products);
                            adaptadorProductos.notifyDataSetChanged();
                            break;
                        case 1:

                            if (products.getChangeble().equalsIgnoreCase("no")){
                                productsList.add(products);
                                adaptadorProductos.notifyDataSetChanged();
                            }

                            break;
                        case 2:

                            if (products.getChangeble().equalsIgnoreCase("si")){
                                productsList.add(products);
                                adaptadorProductos.notifyDataSetChanged();
                            }

                            break;
                        case 3:

                            if (products.getType().equalsIgnoreCase("telefono")){
                                productsList.add(products);
                                adaptadorProductos.notifyDataSetChanged();
                            }

                            break;
                        case 4:

                            if (products.getType().equalsIgnoreCase("ordenador")){
                                productsList.add(products);
                                adaptadorProductos.notifyDataSetChanged();
                            }

                            break;
                        case 5:

                            if (products.getType().equalsIgnoreCase("televisor")){
                                productsList.add(products);
                                adaptadorProductos.notifyDataSetChanged();
                            }

                            break;
                        case 6:

                            if (products.getType().equalsIgnoreCase("vestido")){
                                productsList.add(products);
                                adaptadorProductos.notifyDataSetChanged();
                            }

                            break;
                        case 7:
                            if (products.getType().equalsIgnoreCase("otro")){
                                productsList.add(products);
                                adaptadorProductos.notifyDataSetChanged();
                            }

                            break;
                        default:

                            adaptadorProductos.notifyDataSetChanged();
                    }
                }

                pbComprar.setVisibility(View.INVISIBLE);
                adaptadorProductos.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pbComprar.setVisibility(View.INVISIBLE);
                Toast.makeText(ComprarActivity.this, "Algo ha ido mal", Toast.LENGTH_SHORT).show();
                Log.e("COMPRA E INTERCAMBIA",error.getMessage());
            }
        });


        return productsList;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchproduct_menu, menu);
        MenuItem item = menu.findItem(R.id.search_item);
        //SearchView searchView = (SearchView) item.getActionView();
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Buscar...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchProduct(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchProduct(s);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemSelected=item.getItemId();
        if (itemSelected==R.id.search_item){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //search in firebase
    public List<Products> searchProduct(String query){
        pbComprar.setVisibility(View.VISIBLE);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsList.clear();
                for (DataSnapshot data: snapshot.getChildren()) {
                    products = data.getValue(Products.class);
                    products.setId(data.getKey());
                    if (products.getDescription().toLowerCase().contains(query) || products.getType().toLowerCase().contains(query)){
                        productsList.add(products);
                    }
                }
                pbComprar.setVisibility(View.INVISIBLE);
                adaptadorProductos.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pbComprar.setVisibility(View.INVISIBLE);
                Toast.makeText(ComprarActivity.this, "Algo ha ido mal en la busqueda", Toast.LENGTH_SHORT).show();
                Log.e("COMPRA E INTERCAMBIA",error.getMessage());
            }
        });

        return productsList;

    }


    @Override
    protected void onStart() {
        super.onStart();
//        dbListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

//metodos de la interface itemIterface
    @Override
    public void showDetailProduct(int position) {
        products = (Products) adaptadorProductos.getItem(position);
        Intent detail = new Intent(ComprarActivity.this, DetalleActivity.class);

        detail.putExtra("key", products.getId());
        //start activity
        startActivity(detail);
        Toast.makeText(ComprarActivity.this, "Clickeaste en "+products.getDescription(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateProduct(int position) {
        Toast.makeText(ComprarActivity.this, "ACTUALIZAR. Este servicio aún no está disponible", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void deleteProduct(int position) {
        Products productSelected = (Products) adaptadorProductos.getItem(position);
        String selectedKey = productSelected.getId();
        //get reference
        StorageReference image1 = storage.getReferenceFromUrl(productSelected.getImg());
        StorageReference image2 = storage.getReferenceFromUrl(productSelected.getImg2());

        if (getUser().equalsIgnoreCase(productSelected.getUserName())) {

                image1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ComprarActivity.this, "Eliminando..", Toast.LENGTH_SHORT).show();
                            }
                        },1000);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("COMPRA E INTERCAMBIA", e.getMessage());
                    }
                });
            image2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onSuccess(Void unused) {
                    dbRef.child(selectedKey).removeValue();
                    Snackbar snackbar = Snackbar.make(relativeLayout,"Artículo eliminado!",Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.BLUE);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                    //Toast.makeText(ComprarActivity.this, "Artículo eliminado!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ComprarActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    Log.e("COMPRA E INTERCAMBIA",e.getMessage());
                }
            });

        }else{
            Snackbar snackbar = Snackbar.make(relativeLayout,"No está autorizado para eliminar este artículo",Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.RED);
            snackbar.setTextColor(Color.WHITE);
            snackbar.show();
        }

    }

    @Override
    public void doNothing(int position) {

    }

    public String getUser(){
        Bundle data = getIntent().getExtras();
        String currentUserName = data.getString("user");
        return currentUserName;
    }


    public void goBack(View v){
        finish();
    }


}
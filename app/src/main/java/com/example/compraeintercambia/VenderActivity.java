package com.example.compraeintercambia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.compraeintercambia.model.Products;
import com.example.compraeintercambia.otrhers.CheckInternetConection;
import com.example.compraeintercambia.otrhers.Notifications;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class VenderActivity extends AppCompatActivity {
    //constants
    private static final int PICTURE1=10;
    private static final int PICTURE2=20;

    private ImageView ivVenderFoto1,ivVenderFoto2,ivDialogo;
    private TextView tvDialogo, actionBarTitle;
    private Spinner spTipoProducto;
    private EditText etVenderPrecio,etVenderDescripcion;
    private ProgressBar pbVender;
    private ImageView backButton;
    //tipos de productos
    private String categoria [] = {"Tipo de artículo","Telefono","Ordenador","Televisor","Vestido", "Otro"};
    //image
    private Uri imageUri1,imageUri2;
    //firebase
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("Products");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("Products");
    private StorageTask uploadTask;
    //set product id
    String productId = database.push().getKey();

    private String userName;
    private String usertel;
    Products products = new Products();
    Notifications notification;
    CheckInternetConection internetConection;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vender);
        //referencias visuales
        backButton=findViewById(R.id.actionGoBack);
        actionBarTitle=findViewById(R.id.actionBarTitle);
        ivVenderFoto1=findViewById(R.id.ivVenderFoto1);
        ivVenderFoto2=findViewById(R.id.ivVenderFoto2);
        spTipoProducto=findViewById(R.id.spTipoProducto);
        etVenderPrecio=findViewById(R.id.etVenderPrecio);
        etVenderDescripcion=findViewById(R.id.etVenderDescription);
        pbVender=findViewById(R.id.pbVender);
        //ocultar el progressBar
        pbVender.setVisibility(View.GONE);
        //set action bar title
        actionBarTitle.setText("Vender");
        //set display home button
        getSupportActionBar().hide();
        //poner datos en el spinner
        ArrayAdapter<String> adaptador=new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,categoria);
        spTipoProducto.setAdapter(adaptador);

        notification = new Notifications(this);
        internetConection = new CheckInternetConection(this);
        /*//seleccionar las imagenes
        //Foto 1
        ivVenderFoto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        //Foto 2
        /*ivVenderFoto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galeria = new Intent();
                galeria.setAction(Intent.ACTION_GET_CONTENT);
                galeria.setType("image/*");
                startActivityIfNeeded(galeria,20);
            }
        });*/

        /*//
        spTipoProducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                validateCategory(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        //get userName and tel
        Bundle data = getIntent().getExtras();
        userName = data.getString("user");
        usertel = data.getString("tel");
        Toast.makeText(VenderActivity.this, "Bienvenido: "+userName, Toast.LENGTH_SHORT).show();

    }

    public void selectFirstImg(View v){
        Intent galeria = new Intent();
        galeria.setAction(Intent.ACTION_GET_CONTENT);
        galeria.setType("image/*");
        startActivityIfNeeded(galeria,PICTURE1);

    }

    public void selectSecondImg(View v){
        Intent galeria = new Intent();
        galeria.setAction(Intent.ACTION_GET_CONTENT);
        galeria.setType("image/*");
        startActivityIfNeeded(galeria,PICTURE2);

    }

    //publicar
    public void publicarProducto(View v){

        String precio = etVenderPrecio.getText().toString().trim();
        String descripcion = etVenderDescripcion.getText().toString().trim();

        if (spTipoProducto.getSelectedItemPosition()==0){
            Toast.makeText(this, "Seleccione la categoria de tu articulo", Toast.LENGTH_SHORT).show();
            spTipoProducto.setFocusable(true);
        }else if (precio.isEmpty()){
            etVenderPrecio.setError("Por favor escriba el precio del articulo");
        }else if (descripcion.isEmpty()){
            etVenderDescripcion.setError("Por favor escriba una descripcion");
        }else if (imageUri1 != null && imageUri2 != null){
            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(VenderActivity.this, "Ya se esta publicando", Toast.LENGTH_SHORT).show();
            }else{

               // publicar(imageUri1, imageUri2);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (internetConection.isNetworkAvailable()) {
                            uploadFirstImg(imageUri1);
                            uploadSecondImg(imageUri2);
                        }else{
                            Toast.makeText(VenderActivity.this, "Por favor revise su conexion a internet e intenta de nuevo", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        }else{
            Toast.makeText(this, "Seleccione dos fotos del articulo!", Toast.LENGTH_SHORT).show();
        }



    }

    public void uploadFirstImg(Uri img1){

        StorageReference fileRef = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(img1));

        uploadTask = fileRef.putFile(img1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        products.setImg(String.valueOf(task.getResult()));
                        //products.setImg(String.valueOf(task.getResult()));
                        products.setType(spTipoProducto.getSelectedItem().toString());
                        products.setPrice(etVenderPrecio.getText().toString());
                        products.setDescription(etVenderDescripcion.getText().toString());
                        products.setChangeble("no");
                        products.setUserName(userName);
                        products.setUserTel(usertel);
                        database.child(productId).setValue(products);
                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                pbVender.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("COMPRA E INTERCAMBIA", e.getMessage());
                Toast.makeText(VenderActivity.this, "No se pudo publicar la primera imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadSecondImg(Uri img2){

        StorageReference fileRef2 = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(img2));

        uploadTask = fileRef2.putFile(img2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileRef2.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        //products.setImg(String.valueOf(task.getResult()));
                        products.setImg2(String.valueOf(task.getResult()));
                        //seting value on firebase database
                        database.child(productId).setValue(products);

                    }
                });

                //Toast.makeText(VenderActivity.this, "Publicado con éxito!", Toast.LENGTH_SHORT).show();
                pbVender.setVisibility(View.INVISIBLE);
                showDialogIfOk();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                pbVender.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pbVender.setVisibility(View.INVISIBLE);
                Toast.makeText(VenderActivity.this, "No se pudo publicar la segunda imagen", Toast.LENGTH_SHORT).show();
            }
        });

    }




/*//publicar
    private void publicar(Uri img1, Uri img2) {

            StorageReference fileRef = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(img1));
            StorageReference fileRef2 = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(img2));
            //set product id
            String productId = database.push().getKey();
            //first image
        uploadTask = fileRef.putFile(img1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pbVender.setVisibility(View.INVISIBLE);
                    }
                }, 1000);

                fileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        products.setImg(String.valueOf(task.getResult()));
                    }
                });


            }
        });
//upload the second image
        uploadTask = fileRef2.putFile(img2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pbVender.setVisibility(View.INVISIBLE);
                    }
                }, 1000);

                fileRef2.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        //products.setImg(String.valueOf(task.getResult()));
                        products.setImg2(String.valueOf(task.getResult()));
                        products.setType(spTipoProducto.getSelectedItem().toString());
                        products.setPrice(etVenderPrecio.getText().toString());
                        products.setDescription(etVenderDescripcion.getText().toString());
                        products.setChangeble(checkVenderNo.getText().toString());
                        products.setUserName(userName);
                        products.setUserTel(usertel);
                        //seting value on firebase database
                        database.child(productId).setValue(products);

                        showDialogIfOk();
                        //Toast.makeText(VenderActivity.this, "Publicado con éxito!", Toast.LENGTH_SHORT).show();
                        pbVender.setVisibility(View.INVISIBLE);
                        *//*showNotification();*//*
                        notification.showNotification();

                    }
                });


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                pbVender.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VenderActivity.this, "Error,"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }*/
//obtener la extension del archivo
    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mtmp = MimeTypeMap.getSingleton();

        return mtmp.getExtensionFromMimeType(cr.getType(uri));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PICTURE1 && resultCode== RESULT_OK){
            imageUri1=data.getData();
            ivVenderFoto1.setImageURI(imageUri1);
        }
        if (requestCode==PICTURE2 && resultCode== RESULT_OK){
            imageUri2=data.getData();
            ivVenderFoto2.setImageURI(imageUri2);
        }

    }


    public void showDialogIfOk(){
        //crear ventana emergente
        AlertDialog.Builder dialog = new AlertDialog.Builder(VenderActivity.this);
        View ventana = getLayoutInflater().inflate(R.layout.dialog_layout,null);
        //ventana de dialogo
        ivDialogo=ventana.findViewById(R.id.dialogIcon);
        tvDialogo=ventana.findViewById(R.id.tvDialogMessage);
        ivDialogo.setImageResource(R.drawable.check_ok_v);
        tvDialogo.setText("Publicado con éxito!");
        dialog.setView(ventana);
        dialog.setNegativeButton("Aceptar", null);
        dialog.setPositiveButton("Ver productos", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        Intent intent = new Intent(VenderActivity.this, ComprarActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
        dialog.create().show();
    }

    public void goBack(View v){
        finish();
    }

}
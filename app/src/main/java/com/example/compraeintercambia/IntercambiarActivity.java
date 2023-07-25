package com.example.compraeintercambia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class IntercambiarActivity extends AppCompatActivity {

    private static final int PICTURE1=10;
    private static final int PICTURE2=20;

    private Spinner spCategotia;
    private EditText etDescripcion;
    private ImageView ivFoto1, ivFoto2;
    private ProgressBar pbIntercambiar;
    private ImageView backButton;
    private TextView actionBarTitle;
    //ventana de dialogo
    private ImageView ivDialogo;
    private TextView tvDialogo;
    //uri
    private Uri imageUri1, imageUri2;
    //task
    private StorageTask uploadTask;
    //firebase
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("Products");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("Products");
    //set product id
    String productId = database.push().getKey();
    //vars
    private String descripcion,categoria="";
    //user data logged
    private String userName;
    private String usertel;
    //tipos de productos
    private String tipoArticulo [] = {"Tipo de artículo","Telefono","Ordenador","Televisor","Vestido", "Otro"};
    //
    Notifications notification;
    Products products = new Products();
    CheckInternetConection internetConection;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intercambiar);
        //referencias visuales
        backButton=findViewById(R.id.actionGoBack);
        actionBarTitle=findViewById(R.id.actionBarTitle);
        spCategotia=findViewById(R.id.spIntercambiarCategotia);
        etDescripcion=findViewById(R.id.etIntercambiarDescripcion);
        ivFoto1=findViewById(R.id.ivIntercambiar1);
        ivFoto2=findViewById(R.id.ivIntercambiar2);
        pbIntercambiar=findViewById(R.id.pbIntercambiar);

        //set action bar title
        actionBarTitle.setText("Intercambiar");
        //set display home button
        getSupportActionBar().hide();

        //poner datos en el spinner
        ArrayAdapter<String> adaptador=new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,tipoArticulo);
        spCategotia.setAdapter(adaptador);

        notification = new Notifications(this);
        internetConection = new CheckInternetConection(this);

        actionBarTitle.setText("Intercambiar");
        getSupportActionBar().hide();
        //get username and usertell
        getUser();

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


    //abrir galeria
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PICTURE1 && resultCode== RESULT_OK){
            imageUri1=data.getData();
            ivFoto1.setImageURI(imageUri1);
        }
        if (requestCode==PICTURE2 && resultCode== RESULT_OK){
            imageUri2=data.getData();
            ivFoto2.setImageURI(imageUri2);
        }

    }

    //publicar
    public void publicarProducto(View v){

        descripcion = etDescripcion.getText().toString().trim();
        categoria = spCategotia.getSelectedItem().toString().trim();

        if (spCategotia.getSelectedItemPosition()==0){
            Toast.makeText(this, "Seleccione la categoria de tu articulo", Toast.LENGTH_SHORT).show();
        }else if (descripcion.isEmpty()){
            etDescripcion.setError("Por favor escriba una descripcion");
        }else if (imageUri1 != null && imageUri2 != null){
            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(IntercambiarActivity.this, "Ya se esta publicando", Toast.LENGTH_SHORT).show();
            }else{
                /*publicar(imageUri1, imageUri2);*/
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (internetConection.isNetworkAvailable()) {
                            uploadFirtImg(imageUri1);
                            uploadSecondImg(imageUri2);
                        }else{
                            Toast.makeText(IntercambiarActivity.this, "Por favor revise su conexion a internet e intenta de nuevo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }else{
            Toast.makeText(this, "Seleccione 2 fotos del articulo!", Toast.LENGTH_SHORT).show();
        }

    }

    public void uploadFirtImg(Uri img1){
        StorageReference fileRef = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(img1));

        //first image
        uploadTask = fileRef.putFile(img1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        products.setImg(String.valueOf(task.getResult()));
                        //products.setImg2(String.valueOf(task.getResult()));
                        products.setType(categoria);
                        products.setChangeble("si");
                        products.setDescription(descripcion);
                        products.setUserName(userName);
                        products.setUserTel(usertel);
                        //seting value on firebase database
                        database.child(productId).setValue(products);
                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                pbIntercambiar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(IntercambiarActivity.this, "No se pudo publicar la primera imagen", Toast.LENGTH_SHORT).show();
                Log.i("COMPRA E INTERCAMBIA",e.getMessage());
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showDialogIfOk();
                    }
                });

                pbIntercambiar.setVisibility(View.INVISIBLE);


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                pbIntercambiar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pbIntercambiar.setVisibility(View.INVISIBLE);
                Log.i("COMPRA E INTERCAMBIA", e.getMessage());
                Toast.makeText(IntercambiarActivity.this, "No se pudo publicar la segunda imagen", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /*private void publicar(Uri img1, Uri img2) {

        StorageReference fileRef = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(img1));
        StorageReference fileRef2 = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(img2));



        //first image
        uploadTask = fileRef.putFile(img1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pbIntercambiar.setVisibility(View.INVISIBLE);
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
                        pbIntercambiar.setVisibility(View.INVISIBLE);
                    }
                }, 1000);

                fileRef2.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        //products.setImg(String.valueOf(task.getResult()));
                        products.setImg2(String.valueOf(task.getResult()));
                        products.setType(categoria);
                        products.setChangeble("si");
                        products.setDescription(descripcion);
                        products.setUserName(userName);
                        products.setUserTel(usertel);
                        //seting value on firebase database
                        database.child(productId).setValue(products);
                        //dialod
                        abrirVentana();
                        //Toast.makeText(VenderActivity.this, "Publicado con éxito!", Toast.LENGTH_SHORT).show();
                        pbIntercambiar.setVisibility(View.INVISIBLE);
                        //
                        notification.showNotification();
                    }
                });


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                pbIntercambiar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(IntercambiarActivity.this, "Error,"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }*/

    //obtener la extension del archivo
    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mtmp = MimeTypeMap.getSingleton();

        return mtmp.getExtensionFromMimeType(cr.getType(uri));
    }

    public void showDialogIfOk(){
        //crear ventana emergente
        AlertDialog.Builder dialog = new AlertDialog.Builder(IntercambiarActivity.this);
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
                        Intent intent = new Intent(IntercambiarActivity.this, ComprarActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
        dialog.create().show();
    }

    public void getUser(){
        //get userName and tel
        Bundle data = getIntent().getExtras();
        userName = data.getString("user");
        usertel = data.getString("tel");
        Toast.makeText(IntercambiarActivity.this, "Bienvenido: "+userName, Toast.LENGTH_SHORT).show();
    }

    public void goBack(View v){
        finish();
    }

}


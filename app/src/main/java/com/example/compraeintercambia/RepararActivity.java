package com.example.compraeintercambia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.compraeintercambia.adapters.RepairAdapter;
import com.example.compraeintercambia.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RepararActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference("Users");

    RepairAdapter repairAdapter;

    List<User> userList;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reparar);

        recyclerView=findViewById(R.id.rvRepair);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        userList = new ArrayList<>();
        user = new User();

        repairAdapter = new RepairAdapter(getUserList());

        //actionbar
        getSupportActionBar().hide();


    }

    public List<User> getUserList(){

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    user = dataSnapshot.getValue(User.class);
                    if (!user.getType2().equals(null)) {
                        userList.add(user);
                    }
                }
                recyclerView.setAdapter(repairAdapter);
                repairAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("COMPRAEINTERCAMBIA", error.getMessage());
            }
        });

        return userList;
    }

    public void goBack(View v){
        finish();
    }

}
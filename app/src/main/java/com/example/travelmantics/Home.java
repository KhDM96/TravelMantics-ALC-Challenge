package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private FloatingActionButton fabAdd;
    private FirebaseFirestore fsDatabase;
    private CollectionReference dealsRef;
    List<TravelDeals> travelDealsList;
    private String collectionRefString = "travelDeals";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Travelmantics");
        toolbar.setTitleTextColor(getResources().getColor(R.color.airForceBlue));
        fabAdd = findViewById(R.id.fab_addButton);
        fabAdd.setOnClickListener(this);

        FirebaseUtil.openFirebaseReference(collectionRefString,this);
        fsDatabase = FirebaseUtil.mFirestore;
        dealsRef = FirebaseUtil.mCollectionReference;

        RecyclerView recyclerView = findViewById(R.id.dealsRecyclerView);
        DealsAdapter adapter = new DealsAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.attachListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_addButton:
                startActivity(new Intent(this, InsertDealActivity.class));
                break;

//            case R.id. :
//
//                break;
        }
    }
}

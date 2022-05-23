package com.example.lovelybnb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.lovelybnb.Fragment.ExploreFragment;
import com.example.lovelybnb.Fragment.FavoriteFragment;
import com.example.lovelybnb.Fragment.MessageFragment;
import com.example.lovelybnb.Fragment.ProfileFragment;
import com.example.lovelybnb.Fragment.TripFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    String idIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idIntent = getIntent().getStringExtra("Fragment");

        bottomNavigationView = findViewById(R.id.bottomNav);

        if (idIntent==null){
            display(R.id.mnuexplore);
        }else {
            int ID = Integer.parseInt(idIntent);
            display(ID);
            bottomNavigationView.setSelectedItemId(ID);
            idIntent = null;
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                display(item.getItemId());
                return true;
            }
        });



    }

    void display(int id) {
        Fragment fragment = null;
        switch (id) {
            case R.id.mnuexplore:
                fragment = new ExploreFragment();
                break;
            case R.id.mnuFavorite:
                fragment = new FavoriteFragment();
                break;
            case R.id.mnuTrip:
                fragment = new TripFragment();
                break;
            case R.id.mnuMessage:
                fragment = new MessageFragment();
                break;
            case R.id.mnuProfile:
                fragment = new ProfileFragment();
                break;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment);
        ft.commit();
    }


}
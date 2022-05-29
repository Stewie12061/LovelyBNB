package com.example.lovelybnb;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.lovelybnb.Fragment.ExploreFragment;
import com.example.lovelybnb.Fragment.FavoriteFragment;
import com.example.lovelybnb.Fragment.MessageFragment;
import com.example.lovelybnb.Fragment.ProfileFragment;
import com.example.lovelybnb.Fragment.TripFragment;


public class MainActivity extends AppCompatActivity {

    MeowBottomNavigation meowBottomNavigation;
    String idIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idIntent = getIntent().getStringExtra("Fragment");

        meowBottomNavigation = findViewById(R.id.meowBottomNav);



        meowBottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.ic_favorite));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.ic_airplane));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.ic_explore));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(4,R.drawable.ic_chat));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(5,R.drawable.ic_account));


        meowBottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                display(item.getId());
            }
        });

        if (idIntent==null){
            meowBottomNavigation.show(3,true);
        }else {
            int ID = Integer.parseInt(idIntent);
            meowBottomNavigation.show(ID,true);
            idIntent = null;
        }

        meowBottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {

            }
        });
        meowBottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });

    }

    void display(int id) {
        Fragment fragment = null;
        switch (id){
            case 1:
                meowBottomNavigation.setBackgroundResource(R.color.white);
                fragment = new FavoriteFragment();
                break;
            case 2:
                meowBottomNavigation.setBackgroundResource(R.color.white);
                fragment = new TripFragment();
                break;
            case 3:
                meowBottomNavigation.setBackgroundResource(R.color.pink_less);
                fragment = new ExploreFragment();
                break;
            case 4:
                meowBottomNavigation.setBackgroundResource(R.color.white);
                fragment = new MessageFragment();
                break;
            case 5:
                meowBottomNavigation.setBackgroundResource(R.color.pink_less);
                fragment = new ProfileFragment();
                break;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment);
        ft.commit();
    }


}
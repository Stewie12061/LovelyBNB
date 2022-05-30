package com.example.lovelybnb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.lovelybnb.FragmentAdmin.CategoryAdminFragment;
import com.example.lovelybnb.FragmentAdmin.SearchAdminFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainAdminActivity extends AppCompatActivity {

    Fragment fragment = null;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    ChipNavigationBar chipNavigationBar;
    ImageView expanedBtn;
    LinearLayout containMenu;

    ChangeBounds changeBounds = new ChangeBounds();
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        containMenu = findViewById(R.id.containMenu);
        expanedBtn = findViewById(R.id.expandBtn);
        chipNavigationBar = findViewById(R.id.left_menu);


        chipNavigationBar.setItemSelected(R.id.mnuCategory,true);
        if (fragment == null){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.contentAdmin, new CategoryAdminFragment());
            fragmentTransaction.commit();
        }

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i) {
                    case R.id.mnuCategory:
                        fragment = new CategoryAdminFragment();
                        break;
                    case R.id.mnuSearch:
                        fragment = new SearchAdminFragment();
                        break;
                    case R.id.mnuSignoutMenu:
                        fragment = new CategoryAdminFragment();
                        signout();
                        break;
                }
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentAdmin, fragment);
                fragmentTransaction.commit();
            }

        });

        expanedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chipNavigationBar.isExpanded()){
                    TransitionManager.beginDelayedTransition(containMenu,changeBounds);
                    chipNavigationBar.collapse();
                    expanedBtn.setImageResource(R.drawable.arrow_expand);
                }
                else {
                    TransitionManager.beginDelayedTransition(containMenu,changeBounds);
                    chipNavigationBar.expand();
                    expanedBtn.setImageResource(R.drawable.arrow);
                }
            }
        });

    }

    private void signout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainAdminActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
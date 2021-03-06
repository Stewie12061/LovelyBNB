package com.example.lovelybnb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lovelybnb.FragmentAdmin.CategoryAdminFragment;
import com.example.lovelybnb.FragmentAdmin.InspireAdminFragment;
import com.example.lovelybnb.FragmentAdmin.MessageAdminFragment;
import com.example.lovelybnb.FragmentAdmin.SearchAdminFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainAdminActivity extends AppCompatActivity {

    Fragment fragment = null;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    ChipNavigationBar chipNavigationBar;
    ImageView expanedBtn;
    LinearLayout containMenu;

    TextView adminName, adminMail;
    ChangeBounds changeBounds = new ChangeBounds();

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userRef;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        userRef = firebaseDatabase.getReference("Registered users");

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        containMenu = findViewById(R.id.containMenu);
        expanedBtn = findViewById(R.id.expandBtn);
        chipNavigationBar = findViewById(R.id.left_menu);

        adminName = findViewById(R.id.adminName);
        adminMail = findViewById(R.id.adminMail);

        userRef.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adminName.setText(snapshot.child("FullName").getValue().toString());
                adminMail.setText(snapshot.child("Email").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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
                    case R.id.mnuInspire:
                        fragment = new InspireAdminFragment();
                        break;
                    case R.id.mnuSearch:
                        fragment = new SearchAdminFragment();
                        break;
                    case R.id.mnuProfileAdmin:
                        fragment = new MessageAdminFragment();
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

        adminMail.setVisibility(View.INVISIBLE);
        adminName.setVisibility(View.INVISIBLE);
        expanedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (chipNavigationBar.isExpanded()){
                    adminMail.setVisibility(View.INVISIBLE);
                    adminName.setVisibility(View.INVISIBLE);
                    TransitionManager.beginDelayedTransition(containMenu,changeBounds);
                    chipNavigationBar.collapse();
                    expanedBtn.setImageResource(R.drawable.arrow_expand);

                }
                else {
                    adminMail.setVisibility(View.VISIBLE);
                    adminName.setVisibility(View.VISIBLE);
                    TransitionManager.beginDelayedTransition(containMenu,changeBounds);
                    chipNavigationBar.expand();
                    expanedBtn.setImageResource(R.drawable.arrow);
                }
            }
        });

    }


    private void signout() {

        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (MainAdminActivity.this);
        View view = LayoutInflater.from(MainAdminActivity.this).inflate(
                R.layout.dialog_alert,
                (ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle))
                .setText("Signing out?");
        ((TextView) view.findViewById(R.id.textMessage))
                .setText("Are you sure you want to sign out?");
        ((Button) view.findViewById(R.id.buttonYes))
                .setText("Yes");
        ((Button) view.findViewById(R.id.buttonNo))
                .setText("No");
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainAdminActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                chipNavigationBar.setItemSelected(R.id.mnuCategory,true);
            }
        });
        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}
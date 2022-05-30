package com.example.lovelybnb;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    private Animation topAnim, bottomAnim;
    private ImageView logo, travelImg;
    private TextView appName1, appName2, slogan;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        logo = findViewById(R.id.logo);
        travelImg = findViewById(R.id.travelImg);
        appName1 = findViewById(R.id.appName1);
        appName2 = findViewById(R.id.appName2);
        slogan = findViewById(R.id.slogan);
        progressBar = findViewById(R.id.progressBar);

        logo.setAnimation(topAnim);
        travelImg.setAnimation(topAnim);
        progressBar.setAnimation(topAnim);
        appName1.setAnimation(bottomAnim);
        appName2.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
                }
            }, 5000
        );

    }

    private void nextActivity(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            //not login
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);

            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View,String>(logo, "logo");
            pairs[1] = new Pair<View,String>(slogan, "slogan");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(WelcomeActivity.this, pairs);
            startActivity(intent, options.toBundle());

        }else{
            //login
            if (user.getUid().equals("V7nKCjU6nIhXZsTc5z1CYWkkOSh2")){
                Intent intent = new Intent(WelcomeActivity.this, MainAdminActivity.class);

                startActivity(intent);
            }
            else {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
            }


        }
    }
}
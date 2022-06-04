package com.example.lovelybnb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class ItemAdminDetailActivity extends AppCompatActivity {
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_admin_detail);

        id = getIntent().getStringExtra("itemAdId");
        Toast.makeText(getApplicationContext(),id,Toast.LENGTH_SHORT).show();
    }
}
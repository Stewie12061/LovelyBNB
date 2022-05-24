package com.example.lovelybnb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SearchActivity extends AppCompatActivity {
    TextView goback;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchView = findViewById(R.id.searchView);
    }
}
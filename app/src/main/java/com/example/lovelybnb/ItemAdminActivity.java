package com.example.lovelybnb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lovelybnb.Adapter.PropertyItemsViewHolder;
import com.example.lovelybnb.Data.PropertyItems;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ItemAdminActivity extends AppCompatActivity {
    TextView countItem, itemCateName;
    RecyclerView rvItemAd;
    FloatingActionButton openCreateItem;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference itemRef,cateRef;

    String cateId;
    FirebaseRecyclerAdapter<PropertyItems, PropertyItemsViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_admin);

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        itemRef = firebaseDatabase.getReference("Items");

        rvItemAd = findViewById(R.id.rvItemAdmin);
        rvItemAd.setLayoutManager(new LinearLayoutManager(ItemAdminActivity.this,LinearLayoutManager.VERTICAL,false));

        cateId = getIntent().getStringExtra("categoryId");


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (cateId!=null){
            getItemData();
        }
    }

    private void getItemData() {
        Query query = itemRef.child(cateId);
        FirebaseRecyclerOptions<PropertyItems> options = new FirebaseRecyclerOptions.Builder<PropertyItems>().setQuery(query,PropertyItems.class).build();

        adapter = new FirebaseRecyclerAdapter<PropertyItems, PropertyItemsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PropertyItemsViewHolder holder, int position, @NonNull PropertyItems model) {

            }

            @NonNull
            @Override
            public PropertyItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_property_items,parent,false);
                PropertyItemsViewHolder viewHolder = new PropertyItemsViewHolder(view);
                return viewHolder;
            }
        };
    }
}
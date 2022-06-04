package com.example.lovelybnb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lovelybnb.Adapter.ImageDetailViewHolder;
import com.example.lovelybnb.Adapter.InspireViewHolder;
import com.example.lovelybnb.Data.ImageDetail;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ImageDetailActivity extends AppCompatActivity {
    RecyclerView rvImage;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference sliderRef;
    String itemId;
    TextView goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        sliderRef = firebaseDatabase.getReference("Slider");
        itemId = getIntent().getStringExtra("itemId");

        rvImage = findViewById(R.id.rvImageDetail);
        rvImage.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Query query = sliderRef.child(itemId);

        FirebaseRecyclerOptions<ImageDetail> options = new FirebaseRecyclerOptions.Builder<ImageDetail>().setQuery(query,ImageDetail.class).build();

        FirebaseRecyclerAdapter<ImageDetail, ImageDetailViewHolder> adapter = new FirebaseRecyclerAdapter<ImageDetail, ImageDetailViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ImageDetailViewHolder holder, int position, @NonNull ImageDetail model) {

                String id = getRef(position).getKey();
                sliderRef.child(itemId).child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String img = snapshot.child("url").getValue().toString();
                        Picasso.get().load(img).into(holder.imageView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ImageDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_detail, parent,false);
                ImageDetailViewHolder viewHolder = new ImageDetailViewHolder(view);
                return viewHolder;
            }
        };
        rvImage.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_bottom,R.anim.slide_to_top);
    }
}
package com.example.lovelybnb.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.lovelybnb.Adapter.InspireViewHolder;
import com.example.lovelybnb.Adapter.PropertyTypeViewHolder;
import com.example.lovelybnb.Data.Inspire;
import com.example.lovelybnb.Data.PropertyItems;
import com.example.lovelybnb.Data.PropertyType;
import com.example.lovelybnb.ItemClickListener;
import com.example.lovelybnb.PropertyItemsActivity;
import com.example.lovelybnb.R;
import com.example.lovelybnb.SignupActivit;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ExploreFragment extends Fragment{


//    private PropertyTypeAdapter propertyTypeAdapter;
    private RecyclerView rvInspire, rvProperty;
    private ArrayList<Inspire> arrayListInspire;
    private ArrayList<PropertyType> arrayListProperty;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference referenceInspire, referenceProperty;
    private ChildEventListener childEventListener;
    FirebaseRecyclerAdapter<PropertyType, PropertyTypeViewHolder> adapterProperty;
    FirebaseRecyclerAdapter<Inspire, InspireViewHolder> adapterInspire;

    private View ExploreView;
    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ExploreView = inflater.inflate(R.layout.fragment_explore, container, false);

        arrayListInspire = new ArrayList<>();
        arrayListProperty = new ArrayList<>();


        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        referenceInspire = firebaseDatabase.getReference("Inspire Data");
        referenceProperty = firebaseDatabase.getReference("Categories");

        rvInspire = (RecyclerView) ExploreView.findViewById(R.id.rvInspire);
        rvInspire.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvInspire.addItemDecoration(new HorizontalSpaceItemDecoration(24));

        rvProperty = (RecyclerView) ExploreView.findViewById(R.id.rvPropertyType);
        rvProperty.setLayoutManager(new GridLayoutManager(getContext(), 2));

        return ExploreView;
    }

    public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int horizontalSpaceWidth;

        public HorizontalSpaceItemDecoration(int horizontalSpaceWidth) {
            this.horizontalSpaceWidth = horizontalSpaceWidth;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
                outRect.right = horizontalSpaceWidth;
        }

    }

    private void getDataInsprie(){
        Query query = referenceInspire;

        FirebaseRecyclerOptions<Inspire> options = new FirebaseRecyclerOptions.Builder<Inspire>().setQuery(query,Inspire.class).build();

        adapterInspire = new FirebaseRecyclerAdapter<Inspire, InspireViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull InspireViewHolder holder, int position, @NonNull Inspire model) {
                String id = getRef(position).getKey();

                referenceInspire.child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String inspireDes = dataSnapshot.child("description").getValue().toString();
                        String inspirePlace = dataSnapshot.child("place").getValue().toString();
                        String inspireImg = dataSnapshot.child("image").getValue().toString();


                        holder.Inspiredes.setText(inspireDes);
                        holder.Inspireplace.setText(inspirePlace);
                        Picasso.get().load(inspireImg).into(holder.Inspireimg);

                        holder.setItemClickListener(new ItemClickListener() {

                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(getContext(),PropertyItemsActivity.class);
                                intent.putExtra("PropertyPlace",inspirePlace);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public InspireViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inspire, parent,false);
                InspireViewHolder viewHolder = new InspireViewHolder(view);
                return viewHolder;
            }
        };

        rvInspire.setAdapter(adapterInspire);
        adapterInspire.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();

        getDataInsprie();

        Query query = referenceProperty;

        FirebaseRecyclerOptions<PropertyType> options = new FirebaseRecyclerOptions.Builder<PropertyType>().setQuery(query  , PropertyType.class).build();

        adapterProperty = new FirebaseRecyclerAdapter<PropertyType, PropertyTypeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PropertyTypeViewHolder propertyTypeViewHolder, int position, @NonNull PropertyType propertyType) {

                String id = getRef(position).getKey();

                referenceProperty.child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String propertyname = dataSnapshot.child("Name").getValue().toString();
                        String propertyImg = dataSnapshot.child("Image").getValue().toString();

                        propertyTypeViewHolder.propertyName.setText(propertyname);
                        Picasso.get().load(propertyImg).into(propertyTypeViewHolder.propertyImg);

                        propertyTypeViewHolder.setItemClickListener(new ItemClickListener() {

                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(getContext(),PropertyItemsActivity.class);
                                intent.putExtra("IdPropertyType",adapterProperty.getRef(position).getKey());
                                intent.putExtra("PropertyName",propertyname);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public PropertyTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_property_type, parent,false);
                PropertyTypeViewHolder viewHolder = new PropertyTypeViewHolder(view);
                return viewHolder;
            }
        };

        rvProperty.setAdapter(adapterProperty);
        adapterProperty.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        adapterProperty.stopListening();
        adapterInspire.stopListening();
    }
}
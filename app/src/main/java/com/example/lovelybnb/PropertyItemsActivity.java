package com.example.lovelybnb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lovelybnb.Adapter.PropertyItemsViewHolder;
import com.example.lovelybnb.Data.Favorite;
import com.example.lovelybnb.Data.PropertyItems;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;

import java.util.ArrayList;

public class PropertyItemsActivity extends AppCompatActivity {
    private RecyclerView rvPropertyItems;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, favoriteRef;

    String categoryId;
    String propertyName;
    String propertyPlace;
    private TextView goback, propertyNameItem;

    Boolean isInMyFavorite = false;
    private FirebaseAuth firebaseAuth;
    String itemId;
    SparkButton sparkButton;
    Favorite favorite;
    String key = "4";
    ArrayList<String> arrayList = null;

    FirebaseRecyclerAdapter<PropertyItems, PropertyItemsViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_items);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        databaseReference = firebaseDatabase.getReference("Items");
        favoriteRef = firebaseDatabase.getReference("Favorite");

        //get key from receipt

        getReceiptKey();

        rvPropertyItems = findViewById(R.id.rvPropertyItems);
        rvPropertyItems.setHasFixedSize(true);
        rvPropertyItems.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


        propertyNameItem = findViewById(R.id.propertyNameInItem);
        favorite = new Favorite();

        categoryId = getIntent().getStringExtra("IdPropertyType");


        propertyPlace = getIntent().getStringExtra("PropertyPlace");

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sparkButton = findViewById(R.id.Fav);

    }

    @Override
    protected void onStart() {
        super.onStart();
        arrayList = new ArrayList<String>();
        getReceiptKey();
        //fetch data property items equal to id

        if (categoryId != null){
            getDataPropertyItems();
            propertyName = getIntent().getStringExtra("PropertyName");
            propertyNameItem.setText(propertyName);
        }
        //fetch data property items equal to place name
        if (propertyPlace != null){
            getDataPropertyInspire();
            propertyName = getIntent().getStringExtra("PropertyPlace");

            propertyNameItem.setText(propertyName);
        }
    }


    private void getReceiptKey() {
        DatabaseReference userRef = firebaseDatabase.getReference("Registered users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        userRef.child(currentUserId).child("Trip").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList = new ArrayList<String>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    arrayList.add(dataSnapshot.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getDataPropertyInspire() {
        Query query = databaseReference.orderByChild("place").equalTo(propertyPlace);

        FirebaseRecyclerOptions<PropertyItems> options = new FirebaseRecyclerOptions.Builder<PropertyItems>().setQuery(query,PropertyItems.class).build();

        adapter = new FirebaseRecyclerAdapter<PropertyItems, PropertyItemsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PropertyItemsViewHolder holder, int position, @NonNull PropertyItems model) {
                String postKey = adapter.getRef(position).getKey();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserId = user.getUid();

                //if an item has been booked, removed it
                if (arrayList.contains(postKey)){
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }

                databaseReference.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        String Name = snapshot.child("name").getValue().toString();
                        String Price = snapshot.child("price").getValue().toString();
                        String Place = snapshot.child("place").getValue().toString();
                        String Rating = snapshot.child("rating").getValue().toString();
                        String Image = snapshot.child("image").getValue().toString();

                        holder.PropertyItemName.setText(Name);
                        holder.PropertyItemPlace.setText(Place);
                        holder.PropertyItemPrice.setText(Price);
                        holder.PropertyItemRating.setText(Rating);
                        Picasso.get().load(Image).into(holder.PropertyItemImg);

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(PropertyItemsActivity.this, ItemDetailActivity.class);
                                intent.putExtra("itemId", adapter.getRef(position).getKey());
                                Pair[] pairs = new Pair[6];
                                pairs[0] = new Pair<View,String>(holder.PropertyItemName, "name");
                                pairs[1] = new Pair<View,String>(holder.PropertyItemImg, "img");
                                pairs[2] = new Pair<View,String>(holder.PropertyItemPrice, "price");
                                pairs[3] = new Pair<View,String>(holder.PropertyItemPlace, "place");
                                pairs[4] = new Pair<View,String>(holder.PropertyItemRating, "rating");
                                pairs[5] = new Pair<View,String>(holder.sparkButton,"fav");

                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(PropertyItemsActivity.this, pairs);
                                startActivity(intent, options.toBundle());

                            }
                        });

                        holder.favoriteCheck(postKey);

                        holder.sparkButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.sparkButton.playAnimation();
                                if (holder.isInMyFavorite){
                                    favoriteRef.child(currentUserId).child(postKey).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    //make Toast text center
                                                    String text = "Remove"+" "+Name+" "+"from favorite list";
                                                    Spannable centeredText = new SpannableString(text);
                                                    centeredText.setSpan(
                                                            new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                                            0, text.length() - 1,
                                                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                                                    );

                                                    Toast.makeText(PropertyItemsActivity.this, centeredText, Toast.LENGTH_SHORT).show();
//                                                    Toast.makeText(PropertyItemsActivity.this, "Remove"+" "+Name+" "+"from favorite list", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }else {
                                    favorite.setfavoriteName(Name);
                                    favorite.setfavoritePrice(Price);
                                    favorite.setfavoriteRating(Rating);
                                    favorite.setfavoritePlace(Place);
                                    favorite.setfavoriteImage(Image);
                                    favoriteRef.child(currentUserId).child(postKey).setValue(favorite)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    String text = "Add"+" "+Name+" "+"to favorite list";
                                                    Spannable centeredText = new SpannableString(text);
                                                    centeredText.setSpan(
                                                            new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                                            0, text.length() - 1,
                                                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                                                    );

                                                    Toast.makeText(PropertyItemsActivity.this, centeredText, Toast.LENGTH_SHORT).show();
//                                                    Toast.makeText(PropertyItemsActivity.this, "Add"+" "+Name+" "+"to favorite list", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
            @NonNull
            @Override
            public PropertyItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_property_items, parent,false);
                PropertyItemsViewHolder viewHolder = new PropertyItemsViewHolder(view);
                return viewHolder;
            }
        };

        rvPropertyItems.setAdapter(adapter);
        adapter.startListening();
    }

    private void getDataPropertyItems() {
        Query query = databaseReference.orderByChild("propertyTypeId").equalTo(categoryId);

        FirebaseRecyclerOptions<PropertyItems> options = new FirebaseRecyclerOptions.Builder<PropertyItems>().setQuery(query,PropertyItems.class).build();

            adapter = new FirebaseRecyclerAdapter<PropertyItems, PropertyItemsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PropertyItemsViewHolder holder, int position, @NonNull PropertyItems model) {
                String postKey = getRef(position).getKey();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserId = user.getUid();

                if (arrayList.contains(postKey)){
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }

                databaseReference.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String Name = snapshot.child("name").getValue().toString();
                        String Price = snapshot.child("price").getValue().toString();
                        String Place = snapshot.child("place").getValue().toString();
                        String Rating = snapshot.child("rating").getValue().toString();
                        String Image = snapshot.child("image").getValue().toString();

                        holder.PropertyItemName.setText(Name);
                        holder.PropertyItemPlace.setText(Place);
                        holder.PropertyItemPrice.setText(Price);
                        holder.PropertyItemRating.setText(Rating);
                        Picasso.get().load(Image).into(holder.PropertyItemImg);

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(PropertyItemsActivity.this, ItemDetailActivity.class);
                                intent.putExtra("itemId", adapter.getRef(position).getKey());

                                Pair[] pairs = new Pair[6];
                                pairs[0] = new Pair<View,String>(holder.PropertyItemName, "name");
                                pairs[1] = new Pair<View,String>(holder.PropertyItemImg, "img");
                                pairs[2] = new Pair<View,String>(holder.PropertyItemPrice, "price");
                                pairs[3] = new Pair<View,String>(holder.PropertyItemPlace, "place");
                                pairs[4] = new Pair<View,String>(holder.PropertyItemRating, "rating");
                                pairs[5] = new Pair<View,String>(holder.sparkButton,"fav");

                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(PropertyItemsActivity.this, pairs);
                                startActivity(intent, options.toBundle());
                            }
                        });

                        holder.favoriteCheck(postKey);

                        holder.sparkButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.sparkButton.playAnimation();
                                if (holder.isInMyFavorite){
                                    favoriteRef.child(currentUserId).child(postKey).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    String text = "Remove"+" "+Name+" "+"from favorite list";
                                                    Spannable centeredText = new SpannableString(text);
                                                    centeredText.setSpan(
                                                            new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                                            0, text.length() - 1,
                                                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                                                    );

                                                    Toast.makeText(PropertyItemsActivity.this, centeredText, Toast.LENGTH_LONG).show();
//                                                    Toast.makeText(PropertyItemsActivity.this, "Remove"+" "+Name+" "+"from favorite list", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }else {
                                    favorite.setfavoriteName(Name);
                                    favorite.setfavoritePrice(Price);
                                    favorite.setfavoriteRating(Rating);
                                    favorite.setfavoritePlace(Place);
                                    favorite.setfavoriteImage(Image);

                                    favoriteRef.child(currentUserId).child(postKey).setValue(favorite)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    String text = "Add"+" "+Name+" "+"to favorite list";
                                                    Spannable centeredText = new SpannableString(text);
                                                    centeredText.setSpan(
                                                            new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                                            0, text.length() - 1,
                                                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                                                    );

                                                    Toast.makeText(PropertyItemsActivity.this, centeredText, Toast.LENGTH_SHORT).show();
//                                                    Toast.makeText(PropertyItemsActivity.this, "Add"+" "+Name+" "+"to favorite list", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            @NonNull
            @Override
            public PropertyItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_property_items, parent,false);
                PropertyItemsViewHolder viewHolder = new PropertyItemsViewHolder(view);
                return viewHolder;
            }
        };

        rvPropertyItems.setAdapter(adapter);
        adapter.startListening();

    }
    private class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {
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


}
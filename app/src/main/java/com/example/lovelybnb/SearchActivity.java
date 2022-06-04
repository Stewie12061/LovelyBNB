package com.example.lovelybnb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    TextView goback, appname1,appname2;
    ImageView logo;
    SearchView searchView;
    RecyclerView rvSearch;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference itemRef,favoriteRef,receiptRef;
    Toolbar toolbar;
    FirebaseRecyclerOptions<PropertyItems> options;
    FirebaseRecyclerAdapter<PropertyItems,PropertyItemsViewHolder> adapter;
    ArrayList<String> arrayList = null;
    Favorite favorite;
    LottieAnimationView searchWaiting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        appname1 = findViewById(R.id.appnameSearch1);
        appname2 = findViewById(R.id.appnameSearch2);
        logo = findViewById(R.id.logoSearch);

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        itemRef = firebaseDatabase.getReference("Items");
        favoriteRef = firebaseDatabase.getReference("Favorite");
        receiptRef = firebaseDatabase.getReference("Receipt");
        favorite = new Favorite();

        toolbar = findViewById(R.id.toolbarSearch);
        goback = toolbar.findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rvSearch = findViewById(R.id.rvSearch);
        rvSearch.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));

        searchWaiting = findViewById(R.id.searchWaiting);

        searchView = toolbar.findViewById(R.id.searchView);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchWaiting.setVisibility(View.INVISIBLE);
                rvSearch.setVisibility(View.VISIBLE);
                searchView.setMinimumWidth(300);
                loadDataSearch("");
                appname1.setVisibility(View.GONE);
                appname2.setVisibility(View.GONE);
                logo.setVisibility(View.GONE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchWaiting.setVisibility(View.VISIBLE);
                rvSearch.setVisibility(View.INVISIBLE);
                searchView.setMinimumWidth(50);
                appname1.setVisibility(View.VISIBLE);
                appname2.setVisibility(View.VISIBLE);
                logo.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }
    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    @Override
    protected void onStart() {
        super.onStart();
        arrayList = new ArrayList<String>();
        getReceiptKey();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadDataSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadDataSearch(newText);
                return false;
            }
        });
    }

    private void getReceiptKey() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        receiptRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void loadDataSearch(String searchText) {
        Query query;
        if(searchText==""){
            query = itemRef.orderByChild("itemName");
        }
        else {
            query = itemRef.orderByChild("itemName").startAt(searchText).endAt(searchText+"\uf8ff");
        }


        options = new FirebaseRecyclerOptions.Builder<PropertyItems>().setQuery(query,PropertyItems.class).build();
        adapter = new FirebaseRecyclerAdapter<PropertyItems, PropertyItemsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PropertyItemsViewHolder holder, int position, @NonNull PropertyItems model) {
                String postKey = adapter.getRef(position).getKey();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserId = user.getUid();

                if (arrayList.contains(postKey)){
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }

                itemRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String Name = snapshot.child("itemName").getValue().toString();
                        String Price = snapshot.child("itemPrice").getValue().toString();
                        String Place = snapshot.child("itemPlace").getValue().toString();
                        String Rating = snapshot.child("itemRating").getValue().toString();
                        String Image = snapshot.child("itemImage").getValue().toString();

                        holder.PropertyItemName.setText(Name);
                        holder.PropertyItemPlace.setText(Place);
                        holder.PropertyItemPrice.setText(Price);
                        holder.PropertyItemRating.setText(Rating);
                        Picasso.get().load(Image).into(holder.PropertyItemImg);

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(SearchActivity.this, ItemDetailActivity.class);
                                intent.putExtra("itemId", adapter.getRef(position).getKey());

                                Pair[] pairs = new Pair[6];
                                pairs[0] = new Pair<View,String>(holder.PropertyItemName, "name");
                                pairs[1] = new Pair<View,String>(holder.PropertyItemImg, "img");
                                pairs[2] = new Pair<View,String>(holder.PropertyItemPrice, "price");
                                pairs[3] = new Pair<View,String>(holder.PropertyItemPlace, "place");
                                pairs[4] = new Pair<View,String>(holder.PropertyItemRating, "rating");
                                pairs[5] = new Pair<View,String>(holder.sparkButton,"fav");

                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SearchActivity.this, pairs);
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

                                                    Toast.makeText(SearchActivity.this, centeredText, Toast.LENGTH_LONG).show();
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

                                                    Toast.makeText(SearchActivity.this, centeredText, Toast.LENGTH_SHORT).show();
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
        rvSearch.setAdapter(adapter);
        adapter.startListening();
    }

}
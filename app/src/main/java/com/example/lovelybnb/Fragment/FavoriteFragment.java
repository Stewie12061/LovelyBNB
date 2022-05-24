package com.example.lovelybnb.Fragment;

import android.app.ActivityOptions;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelybnb.Adapter.FavoriteViewHolder;
import com.example.lovelybnb.Adapter.PropertyItemsViewHolder;
import com.example.lovelybnb.Data.Favorite;
import com.example.lovelybnb.Data.PropertyItems;
import com.example.lovelybnb.ItemClickListener;
import com.example.lovelybnb.ItemDetailActivity;
import com.example.lovelybnb.MainActivity;
import com.example.lovelybnb.PropertyItemsActivity;
import com.example.lovelybnb.R;

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


public class FavoriteFragment extends Fragment {
    RecyclerView rvFavorite;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userRef;
    Boolean favoriteChecker = false;
    FirebaseRecyclerAdapter<Favorite, FavoriteViewHolder> adapter;
    String currentUserId;
    ArrayList<String> arrayList = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFavorite = view.findViewById(R.id.rvFavorite);
        rvFavorite.setHasFixedSize(true);
        rvFavorite.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        userRef = firebaseDatabase.getReference("Registered users");

        getReceiptKey();
        getDataFavorite();
    }

    @Override
    public void onStart() {
        super.onStart();
        arrayList = new ArrayList<String>();
        getReceiptKey();
        getDataFavorite();
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

    private void getDataFavorite() {
        Query query = userRef.child(currentUserId).child("Favorites");

        FirebaseRecyclerOptions<Favorite> options = new FirebaseRecyclerOptions.Builder<Favorite>().setQuery(query,Favorite.class).build();

        adapter = new FirebaseRecyclerAdapter<Favorite, FavoriteViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position, @NonNull Favorite model) {
                String postKey = getRef(position).getKey();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserId = user.getUid();

                if (arrayList.contains(postKey)){
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }

                userRef.child(currentUserId).child("Favorites").child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String Name = snapshot.child("favoriteName").getValue().toString();
                        String Price = snapshot.child("favoritePrice").getValue().toString();
                        String Place = snapshot.child("favoritePlace").getValue().toString();
                        String Rating = snapshot.child("favoriteRating").getValue().toString();
                        String Image = snapshot.child("favoriteImage").getValue().toString();

                        holder.FavoriteName.setText(Name);
                        holder.FavoritePlace.setText(Place);
                        holder.FavoritePrice.setText(Price);
                        holder.FavoriteRating.setText(Rating);
                        Picasso.get().load(Image).into(holder.FavoriteImg);

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(getContext(), ItemDetailActivity.class);
                                intent.putExtra("itemId", adapter.getRef(position).getKey());
                                Pair[] pairs = new Pair[6];
                                pairs[0] = new Pair<View,String>(holder.FavoriteName, "name");
                                pairs[1] = new Pair<View,String>(holder.FavoriteImg, "img");
                                pairs[2] = new Pair<View,String>(holder.FavoritePrice, "price");
                                pairs[3] = new Pair<View,String>(holder.FavoritePlace, "place");
                                pairs[4] = new Pair<View,String>(holder.FavoriteRating, "rating");
                                pairs[5] = new Pair<View,String>(holder.sparkButton,"fav");
                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pairs);
                                startActivity(intent, options.toBundle());
                            }
                        });

                        holder.favoriteCheck(postKey);
                        holder.sparkButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (holder.isInMyFavorite){
                                    userRef.child(currentUserId).child("Favorites").child(postKey).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getContext(), "Removed from favorite list", Toast.LENGTH_SHORT).show();
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
            public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent,false);
                FavoriteViewHolder viewHolder = new FavoriteViewHolder(view);
                return viewHolder;
            }
        };
        rvFavorite.setAdapter(adapter);
        adapter.startListening();
    }

}
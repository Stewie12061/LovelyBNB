package com.example.lovelybnb.Adapter;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelybnb.ItemClickListener;
import com.example.lovelybnb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView FavoriteName, FavoritePlace, FavoritePrice, FavoriteRating ;
    public ImageView FavoriteImg;
    public Button fav;
    public DatabaseReference userRef;
    public FirebaseDatabase firebaseDatabase;
    public boolean isInMyFavorite = false;
    private ItemClickListener itemClickListener;


    public FavoriteViewHolder(@NonNull View itemView) {
        super(itemView);
        FavoriteName = itemView.findViewById(R.id.favoriteName);
        FavoritePlace = itemView.findViewById(R.id.favoritePlace);
        FavoriteRating = itemView.findViewById(R.id.favoriteRating);
        FavoritePrice = itemView.findViewById(R.id.favoriteRating);
        FavoriteImg = itemView.findViewById(R.id.favoriteImg);
        fav = itemView.findViewById(R.id.favFav);
        itemView.setOnClickListener(this);
    }

    public void favoriteCheck(String postKey) {
        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        userRef = firebaseDatabase.getReference("Registered users");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        userRef.child(userId).child("Favorites").child(postKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isInMyFavorite = snapshot.exists();
                        if (isInMyFavorite){
                            fav.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_favorite_red,0,0);
                        }else {
                            fav.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_favorite_white,0,0);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public void onClick(View v) {
        itemClickListener.onClick(v, getBindingAdapterPosition(),false);
    }

}

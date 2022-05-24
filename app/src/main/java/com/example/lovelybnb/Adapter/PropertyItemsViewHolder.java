package com.example.lovelybnb.Adapter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
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
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;

public class PropertyItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView PropertyItemName, PropertyItemPlace, PropertyItemPrice, PropertyItemRating ;
    public ImageView PropertyItemImg;
    public SparkButton sparkButton;
    public DatabaseReference userRef;
    public FirebaseDatabase firebaseDatabase;
    public Boolean isInMyFavorite = false;

    private ItemClickListener itemClickListener;

    public PropertyItemsViewHolder(@NonNull View itemView) {
        super(itemView);

        PropertyItemName = itemView.findViewById(R.id.itemName);
        PropertyItemPlace = itemView.findViewById(R.id.itemPlace);
        PropertyItemRating = itemView.findViewById(R.id.itemRating);
        PropertyItemPrice = itemView.findViewById(R.id.itemPrice);
        PropertyItemImg = itemView.findViewById(R.id.propertyItemImg);
        sparkButton = itemView.findViewById(R.id.Fav);
        itemView.setOnClickListener(this);


    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getBindingAdapterPosition(),false);
    }

    public void favoriteCheck(String postKey) {
        sparkButton = itemView.findViewById(R.id.Fav);
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
                            sparkButton.setChecked(true);
                            sparkButton.playAnimation();
                        }else {
                            sparkButton.setChecked(false);
                            sparkButton.playAnimation();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}

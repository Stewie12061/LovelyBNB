package com.example.lovelybnb.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelybnb.ItemClickListener;
import com.example.lovelybnb.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.varunest.sparkbutton.SparkButton;

public class ItemAdminViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView ItemAdName, ItemAdPlace, ItemAdPrice, ItemAdRating ;
    public ImageView ItemAdImg;
    public LinearLayout btnDeleteItem, btnModifyItem;

    private ItemClickListener itemClickListener;

    public ItemAdminViewHolder(@NonNull View itemView) {
        super(itemView);

        ItemAdName = itemView.findViewById(R.id.itemAdName);
        ItemAdPlace = itemView.findViewById(R.id.itemAdPlace);
        ItemAdPrice = itemView.findViewById(R.id.itemAdPrice);
        ItemAdRating = itemView.findViewById(R.id.itemAdRating);
        ItemAdImg = itemView.findViewById(R.id.itemAdImg);

        btnDeleteItem = itemView.findViewById(R.id.btnDeleteItem);
        btnModifyItem =  itemView.findViewById(R.id.btnModifyItem);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getBindingAdapterPosition(),false);
    }

}

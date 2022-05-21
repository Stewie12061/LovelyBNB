package com.example.lovelybnb.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelybnb.R;

public class TripViewHolder extends RecyclerView.ViewHolder {

    public TextView TripName, TripAddress, TripCheckIn, TripCheckOut;
    public ImageView TripImg;
    public TripViewHolder(@NonNull View itemView) {
        super(itemView);

        TripName = itemView.findViewById(R.id.tripName);
        TripImg = itemView.findViewById(R.id.tripImg);
        TripAddress = itemView.findViewById(R.id.tripAddress);
        TripCheckIn = itemView.findViewById(R.id.tripCheckInDay);
        TripCheckOut = itemView.findViewById(R.id.tripCheckOutDay);
    }
}

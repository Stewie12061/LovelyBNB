package com.example.lovelybnb.Adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelybnb.R;
import com.makeramen.roundedimageview.RoundedImageView;

public class ImageDetailViewHolder extends RecyclerView.ViewHolder {
    public RoundedImageView imageView;
    public ImageDetailViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageDetail);
    }
}

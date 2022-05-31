package com.example.lovelybnb.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelybnb.R;
import com.makeramen.roundedimageview.RoundedImageView;

public class CateAdminViewHolder extends RecyclerView.ViewHolder {
    public TextView cateadName;
    public RoundedImageView cateadImg;
    public CateAdminViewHolder(@NonNull View itemView) {
        super(itemView);

        cateadImg = itemView.findViewById(R.id.cateAdImg);
        cateadName = itemView.findViewById(R.id.cateAdName);
    }
}

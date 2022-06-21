package com.example.lovelybnb.Adapter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelybnb.ItemClickListener;
import com.example.lovelybnb.R;
import com.makeramen.roundedimageview.RoundedImageView;

public class InspireAdminViewHolder extends RecyclerView.ViewHolder{
    public TextView inspireAdPlace, inspireAdDes;
    public RoundedImageView inspireAdImg;
    public LinearLayout btnmodify, btndelete;

    public InspireAdminViewHolder(@NonNull View itemView) {
        super(itemView);

        inspireAdPlace = itemView.findViewById(R.id.inspireAdPlace);
        inspireAdImg = itemView.findViewById(R.id.inspireAdImg);
//        inspireAdDes = itemView.findViewById(R.id.inspireAdDes);
        btnmodify = itemView.findViewById(R.id.btnModify);
        btndelete = itemView.findViewById(R.id.btnDelete);

    }

}

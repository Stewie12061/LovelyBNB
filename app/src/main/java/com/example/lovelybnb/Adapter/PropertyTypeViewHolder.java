package com.example.lovelybnb.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelybnb.ItemClickListener;
import com.example.lovelybnb.R;

public class PropertyTypeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView propertyName;
    public ImageView propertyImg;

    private ItemClickListener itemClickListener;

    public PropertyTypeViewHolder(@NonNull View itemView) {
        super(itemView);

        propertyName = (TextView) itemView.findViewById(R.id.propertyName);
        propertyImg = (ImageView) itemView.findViewById(R.id.propertyImg);

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

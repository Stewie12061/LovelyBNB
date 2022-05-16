package com.example.lovelybnb.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelybnb.ItemClickListener;
import com.example.lovelybnb.R;

public class InspireViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView Inspireplace, Inspiredes;
    public ImageView Inspireimg;
    private ItemClickListener itemClickListener;

        public InspireViewHolder(@NonNull View itemView) {
            super(itemView);

            Inspireplace = itemView.findViewById(R.id.inspirePlace);
            Inspiredes = itemView.findViewById(R.id.inspireDes);
            Inspireimg = itemView.findViewById(R.id.inspireImg);
            itemView.setOnClickListener(this);

        }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public void onClick(View v) {
        itemClickListener.onClick(v, getBindingAdapterPosition(),false);
    }

}

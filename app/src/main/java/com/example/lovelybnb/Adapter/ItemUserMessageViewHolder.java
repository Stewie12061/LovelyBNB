package com.example.lovelybnb.Adapter;

import com.example.lovelybnb.ItemClickListener;
import com.example.lovelybnb.R;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemUserMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
    public TextView adminName, adminMail, adminPhone, adminGender;
    public CircleImageView adminAvatar;
    private ItemClickListener itemClickListener;

    public ItemUserMessageViewHolder(@NonNull View itemView) {
        super(itemView);

        adminName = itemView.findViewById(R.id.itemAdNameMessage);
        adminGender = itemView.findViewById(R.id.itemAdGenderMessage);
        adminMail = itemView.findViewById(R.id.itemAdEmailMessage);
        adminPhone = itemView.findViewById(R.id.itemAdPhoneMessage);
        adminAvatar = itemView.findViewById(R.id.itemAdAvatarMessage);

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

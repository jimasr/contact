package com.example.contact.util;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profile.R;


public class ContactViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView nameView;
    TextView phoneView;
    ImageView pinView;

    public ContactViewHolder(@NonNull View itemView) {

        super(itemView);

        imageView = itemView.findViewById(R.id.contactImage);
        nameView = itemView.findViewById(R.id.name);
        phoneView = itemView.findViewById(R.id.phoneNumber);
        pinView = itemView.findViewById(R.id.pinned);

    }

}

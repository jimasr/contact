package com.example.contact.util;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contact.entity.Contact;
import com.example.profile.R;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> implements Filterable {

    public Context context;
    public List<Contact> contactList;
    public List<Contact> contactListComplete;
    public boolean displayFavorite;

    public ContactAdapter(Context context, List<Contact> contactList, boolean displayFavorite) {
        this.context = context;
        this.contactList = contactList;
        this.contactListComplete = new ArrayList<>(contactList);
        this.displayFavorite = displayFavorite;
    }

    public void setDisplayFavorite(boolean displayFavorite) {
        this.displayFavorite = displayFavorite;
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
        this.contactListComplete = new ArrayList<>(contactList);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(context).inflate(R.layout.contact_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.nameView.setText(contactList.get(position).getFirstName() + " " + contactList.get(position).getLastName());
        holder.phoneView.setText(contactList.get(position).getPhone());

        Uri imageUri = contactList.get(position).getImage();
        String gender = contactList.get(position).getGender();

        if(imageUri != null) {
            holder.imageView.setImageURI(imageUri);
        } else {
            if(gender.equals("Male")) {
                holder.imageView.setImageResource(R.drawable.male);
            } else {
                holder.imageView.setImageResource(R.drawable.female);
            }
        }

        if(contactList.get(position).isPinned()) {
            holder.pinView.setImageResource(R.drawable.pin);
        } else {
            holder.pinView.setImageResource(0);
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Contact> filteredList = new ArrayList<>();

            if((charSequence == null || charSequence.length() ==0) && !displayFavorite) {
                filteredList.addAll(contactListComplete);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(Contact contact : contactListComplete) {
                    if(contact.getFirstName().toLowerCase().contains(filterPattern) || contact.getPhone().toLowerCase().contains(filterPattern)) {
                        if(!displayFavorite) {
                            filteredList.add(contact);
                        } else {
                            if(contact.isFavorite()) {
                                filteredList.add(contact);
                            }
                        }
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            contactList.clear();
            contactList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
}

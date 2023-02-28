package com.example.contact;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Switch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contact.entity.Contact;
import com.example.contact.util.ContactAdapter;
import com.example.contact.util.ContactItemClickListener;
import com.example.contact.util.StorageManager;
import com.example.profile.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static Context context;
    public static final String MODIFY_CONTACT = "com.example.contact.MODIFY_CONTACT";
    private StorageManager storageManager;
    private List<Contact> contacts;
    private static int contactPosition;
    private ActivityResultLauncher<Intent> addContactLauncher;
    private ActivityResultLauncher<Intent> modifyContactLauncher;

    private RecyclerView contactRecyclerView;
    private ContactAdapter contactAdapter;
    private ImageButton addContactButton;
    private Switch favoriteSwitch;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addContactButton = findViewById(R.id.startButton);
        contactRecyclerView = findViewById(R.id.contactRecyclerView);
        favoriteSwitch = findViewById(R.id.favoriteSwitch);
        searchView = findViewById(R.id.searchView);
        favoriteSwitch = findViewById(R.id.favoriteSwitch);

        storageManager = StorageManager.getInstance();
        context = getApplicationContext();

        contacts = storageManager.load();

//        Contact hazim = new Contact(false, true, "Hazim", "Asri", "17/11/2002", "hazimasri2002@yahoo.com", "0615442165", "Male", null);
//        Contact sophie = new Contact(false, true, "Sophie", "Becka", "21/07/2002", "sophiebecka@yahoo.com", "0514425120", "Female", null);
//        Contact albert = new Contact(false, false, "Albert", "Vaillon", "05/04/2002", "vaillon.albert@gmail.com", "0677283883", "Male", null);
//        Contact lilian = new Contact(false, false, "Lilian", "Baudry", "06/07/2002", "lilian.baudry@gmail.com", "0612456789", "Male", null);
//        Contact steve = new Contact(false, false, "Steve", "Pennec", "20/05/2002", "steve@gmail.com", "0978654345", "Male", null);
//        Contact yoga = new Contact(false, false, "Yoga", "Boy", "19/02/2002", "yorujhinn@gmail.com", "0678987654", "Male", null);
//
//        contacts.add(hazim);
//        contacts.add(sophie);
//        contacts.add(albert);
//        contacts.add(lilian);
//        contacts.add(steve);
//        contacts.add(yoga);

        contactRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactAdapter = new ContactAdapter(getApplicationContext(), contacts, favoriteSwitch.isChecked());
        contactRecyclerView.setAdapter(contactAdapter);

        filterContact();

        addContactButton.setOnClickListener(view -> addContactActivity());

        addContactLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK) {
                        Intent resultIntent = result.getData();
                        if(resultIntent != null) {
                            Contact contact = (Contact)(resultIntent.getSerializableExtra(ContactActivity.CONTACTS));
                            if(contact != null) {
                                contacts.add(contact);
                                contactAdapter.setContactList(contacts);
                                contactRecyclerView.setAdapter(contactAdapter);
                            }
                        }
                    }
                }
        );

        modifyContactLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK) {
                        Intent resultIntent = result.getData();
                        if(resultIntent != null) {
                            Contact contact = (Contact)(resultIntent.getSerializableExtra(ContactActivity.CONTACTS));
                            if(contact != null) {
                                contacts.set(contactPosition, contact);
                                contactAdapter.setContactList(contacts);
                                contactRecyclerView.setAdapter(contactAdapter);
                            }
                        }
                    }
                }
        );

        contactRecyclerView.addOnItemTouchListener(
                new ContactItemClickListener(getApplicationContext(), contactRecyclerView, new ContactItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Contact contact = contacts.get(position);
                        modifyContactActivity(contact, position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Log.i("Click", Integer.toString(position));

                        String pinText = contacts.get(position).isPinned() ? "Unpin" : "Pin";

                        String[] choice = {"Delete", "Call", pinText, "Cancel"};

                        AlertDialog.Builder contactOption = new AlertDialog.Builder(MainActivity.this);
                        contactOption.setTitle("Option")
                                .setItems(choice, (dialogInterface, i) -> {
                                    switch(i) {
                                        case 0:
                                            contacts.remove(position);
                                            contactAdapter.setContactList(contacts);
                                            contactRecyclerView.setAdapter(contactAdapter);
                                            break;
                                        case 1:
                                            Intent intent = new Intent(Intent.ACTION_DIAL);
                                            intent.setData(Uri.parse("tel:" + contacts.get(position).getPhone()));
                                            startActivity(intent);
                                            break;
                                        case 2:
                                            Contact contactPin = contacts.get(position);
                                            contactPin.setPinned();
                                            contacts.remove(position);
                                            if(contactPin.isPinned()) {
                                                contacts.add(0, contactPin);
                                            } else {
                                                contacts.add(contactPin);
                                            }

                                            contactAdapter.setContactList(contacts);
                                            contactRecyclerView.setAdapter(contactAdapter);
                                            break;

                                    }
                                });

                        AlertDialog dialog = contactOption.create();
                        dialog.show();
                    }
                })
        );

    }

    @Override
    protected void onPause() {
        storageManager.save(contacts);
        resetFilter();
        super.onPause();
    }

    @Override
    protected void onStop() {
        storageManager.save(contacts);
        resetFilter();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        storageManager.save(contacts);
        super.onDestroy();
    }

    private void addContactActivity() {
        Intent intent = new Intent(MainActivity.this, ContactActivity.class);
        addContactLauncher.launch(intent);
    }

    private void modifyContactActivity(Contact contact, int position) {
        contactPosition = position;
        Intent intent = new Intent(MainActivity.this, ContactActivity.class);
        intent.putExtra(MODIFY_CONTACT, contact);
        modifyContactLauncher.launch(intent);
    }

    private void filterContact() {

        favoriteSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            Log.i("Contact", contacts.toString());
            contactAdapter.setDisplayFavorite(b);
            contactAdapter.getFilter().filter(searchView.getQuery());
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                contactAdapter.getFilter().filter(s);
                return true;
            }
        });
    }

    private void resetFilter() {
        favoriteSwitch.setChecked(false);
        searchView.setQuery("", false);
    }


}
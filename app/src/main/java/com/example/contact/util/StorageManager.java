package com.example.contact.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.example.contact.MainActivity;
import com.example.contact.entity.Contact;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StorageManager extends AppCompatActivity {
    public static final String CONTACT_FILE = "com.example.contact.CONTACT_FILE";
    private static StorageManager instance = new StorageManager();

    private StorageManager() {}

    public static StorageManager getInstance() {
        if(instance == null) {
            synchronized(StorageManager.class) {
                if(instance == null) {
                    instance = new StorageManager();
                }
            }
        }

        return instance;
    }
    public void save(List<Contact> contacts) {
        SharedPreferences sharedPreferences = MainActivity.context.getSharedPreferences(CONTACT_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(contacts);
        editor.putString(CONTACT_FILE, json);
        editor.apply();
    }

    public List<Contact> load() {
        SharedPreferences sharedPreferences = MainActivity.context.getSharedPreferences(CONTACT_FILE, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(CONTACT_FILE, null);
        Type type = new TypeToken<ArrayList<Contact>>() {}.getType();
        ArrayList<Contact> contacts = gson.fromJson(json, type);

        if(contacts == null) {
            contacts = new ArrayList<>();
        }

        return contacts;
    }
}

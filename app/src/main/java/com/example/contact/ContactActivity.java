package com.example.contact;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.contact.entity.Contact;
import com.example.profile.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ContactActivity extends AppCompatActivity {
    public static final String CONTACTS = "com.example.contact.CONTACTS";
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private Contact modifyContact;
    private Button validate;
    private FloatingActionButton changeImage;

    private Context context;
    private ImageView image;

    //Checkbox
    private CheckBox favoriteCheckBox;
    private boolean isPinned;

    //TextBox
    private TextInputEditText editFirstName;
    private TextInputEditText editLastName;
    private TextInputEditText editBirthDate;
    private TextInputEditText editEmail;
    private TextInputEditText editPhone;
    //RadioButton
    private RadioGroup genderRadioGroup;
    private Calendar calendar;
    private Uri imageUri;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        //Button
        validate = findViewById(R.id.buttonValidate);
        changeImage = findViewById(R.id.changeImageBtn);

        context = getApplicationContext();
        image = findViewById(R.id.imageView);

        //Checkbox
        favoriteCheckBox = findViewById(R.id.checkBox);

        //TextBox
        editFirstName = findViewById(R.id.editTextFirstName);
        editLastName = findViewById(R.id.editTextLastName);
        editBirthDate = findViewById(R.id.editTextDate);
        editEmail = findViewById(R.id.editTextEmail);
        editPhone = findViewById(R.id.editTextPhone);

        //RadioButton
        genderRadioGroup = findViewById(R.id.groupGender);

        //DatePicker
        calendar = Calendar.getInstance();

        image.setBackgroundResource(R.drawable.male);
        imageUri = null;

        //Check data to modify
        modifyContact = (Contact) getIntent().getSerializableExtra(MainActivity.MODIFY_CONTACT);
        if(modifyContact != null) {
            Log.i("Contact", modifyContact.toString());
            initContactToModify();
        }

        setEditBirthDate();

        validate.setOnClickListener(view -> {

            boolean valid = true;

            boolean favorite = favoriteCheckBox.isChecked();
            String contactName = editFirstName.getText().toString();
            String contactAge = editLastName.getText().toString();
            String birthDate = editBirthDate.getText().toString();
            String contactEmail = editEmail.getText().toString();
            String contactPhone = editPhone.getText().toString();
            Uri contactImage = imageUri;

            RadioButton checkedButton = findViewById(genderRadioGroup.getCheckedRadioButtonId());
            String gender = "Other";

            if(checkedButton != null) {
                gender = checkedButton.getText().toString();
            }

            if((contactName.isEmpty() || contactAge.isEmpty() || contactEmail.isEmpty())) {
                Snackbar.make(
                        view,
                        "Name, age and email cannot be empty",
                        Snackbar.LENGTH_SHORT
                ).show();
                valid = false;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(contactEmail).matches() && valid) {
                Snackbar.make(
                        view,
                        "Wrong email format",
                        Snackbar.LENGTH_SHORT
                ).show();
                valid = false;
            }

            if(valid) {
                Toast.makeText(
                        context,
                        "Contact saved",
                        Toast.LENGTH_SHORT
                ).show();

                Contact contact = new Contact(
                        isPinned,
                        favorite,
                        contactName,
                        contactAge,
                        birthDate,
                        contactEmail,
                        contactPhone,
                        gender,
                        contactImage
                );

                sendContactDetails(contact);

            }
        });

        genderRadioGroup.setOnCheckedChangeListener((radioGroup, checkedID) -> {
                    RadioButton checkedRadioButton = (RadioButton) findViewById(checkedID);
                    String inputRadio = checkedRadioButton.getText().toString();

                    setContactImage(inputRadio);

        });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        imageUri = intent.getData();
                        final int takeFlags = intent.getFlags()
                                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
                        image.setImageURI(imageUri);
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
//                    if(result.getResultCode() == RESULT_OK && result.getData() != null) {
//                        image.setImageURI(imageUri);
//                    }
                    image.setImageURI(imageUri);

                }
        );

        changeImage.setOnClickListener(view -> {

            String[] choice = {"Gallery", "Camera"};

            AlertDialog.Builder alertImage = new AlertDialog.Builder(ContactActivity.this);
            alertImage.setTitle("Pick the photo from")
                    .setItems(choice, (dialogInterface, i) -> {
                        switch (i) {
                            case 0:
                                Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                galleryIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                                galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                galleryLauncher.launch(galleryIntent);
                                break;
                            case 1:
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
                                Uri imagePath = createImage();
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath);
                                requestCameraPermission();
                                cameraLauncher.launch(cameraIntent);
                                break;
                        }
                    });

            AlertDialog dialog = alertImage.create();
            dialog.show();
        });
    }

    public void sendContactDetails(Contact contact) {
        Intent intent = new Intent();
        intent.putExtra(CONTACTS, contact);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void requestCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }
    }

    private void setContactImage(String gender) {
        if(imageUri == null) {
            if(gender.equals("Male")) {
                image.setImageResource(R.drawable.male);
            } else {
                image.setImageResource(R.drawable.female);
            }
        } else {
            image.setImageURI(imageUri);
        }
    }

    private void setEditBirthDate() {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                //update text
                String format="dd/MM/yy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.FRENCH);
                editBirthDate.setText(dateFormat.format(calendar.getTime()));
            }
        };

        editBirthDate.setOnClickListener(view -> new DatePickerDialog(
                ContactActivity.this,
                date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show());
    }

    private void initContactToModify() {
        isPinned = modifyContact.isPinned();
        favoriteCheckBox.setChecked(modifyContact.isFavorite());
        editFirstName.setText(modifyContact.getFirstName());
        editLastName.setText(modifyContact.getLastName());
        editBirthDate.setText(modifyContact.getBirthDate());
        editEmail.setText(modifyContact.getEmail());
        editPhone.setText(modifyContact.getPhone());
        imageUri = modifyContact.getImage();

        String modifyContactGender = modifyContact.getGender();
        setContactImage(modifyContactGender);

        switch (modifyContactGender) {
            case "Male":
                ((RadioButton)genderRadioGroup.getChildAt(0)).setChecked(true);
                break;
            case "Female":
                ((RadioButton)genderRadioGroup.getChildAt(1)).setChecked(true);
                break;
            default:
                ((RadioButton)genderRadioGroup.getChildAt(2)).setChecked(true);

        }
    }

    private Uri createImage(){
        Uri URI = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        ContentResolver contentResolver = getContentResolver();

        String image = String.valueOf(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, image + ".jpg");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Contact/");

        Uri finalURI = contentResolver.insert(URI, contentValues);
        imageUri = finalURI;

        return finalURI;
    }
}

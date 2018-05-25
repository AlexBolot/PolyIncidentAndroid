package polytechnice.si3.ihm.android;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import polytechnice.si3.ihm.android.database.model.Category;
import polytechnice.si3.ihm.android.database.model.Importance;
import polytechnice.si3.ihm.android.database.model.Issue;
import polytechnice.si3.ihm.android.database.model.User;
import polytechnice.si3.ihm.android.database.viewmodel.CategoryViewModel;
import polytechnice.si3.ihm.android.database.viewmodel.ImportanceViewModel;
import polytechnice.si3.ihm.android.database.viewmodel.IssueViewModel;
import polytechnice.si3.ihm.android.database.viewmodel.UserViewModel;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class AddingActivity extends AppCompatActivity {

    private String selectedPath = "";
    private EditText txtPhoneNumber;
    private User userConnected;
    private LatLng selectedLocation = new LatLng(-1, -1);

    public static final int CONTACT_REQUEST_CODE = 1;
    public static final int IMAGE_REQUEST_CODE = 2;
    public static final int LOCATION_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding);

        userConnected = new User(getIntent());

        int loggedIn = getIntent().getIntExtra("LoggedIn", 0);
        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getByID(loggedIn).ifPresent(userViewModel::logIn);

        findViewById(R.id.btn_add_pic).setOnClickListener(view -> getImageFromAlbum());

        findViewById(R.id.btn_add_pos).setOnClickListener(view -> startActivityForResult(new Intent(this, MapsActivity.class), LOCATION_REQUEST_CODE));

        findViewById(R.id.btn_add_issue).setOnClickListener(view -> addIssue());

        setUpSpinners();

        Button pickContact = findViewById(R.id.pickContact);

        txtPhoneNumber = findViewById(R.id.phoneNumber);
        txtPhoneNumber.setText(userConnected.getPhoneNumber());

        pickContact.setOnClickListener(v -> {
            Intent pickContact1 = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(pickContact1, CONTACT_REQUEST_CODE);
        });
    }

    private void addIssue() {

        //Get ViewModel
        IssueViewModel issueViewModel = ViewModelProviders.of(this).get(IssueViewModel.class);

        //Get the fields from view
        EditText txtTitle = findViewById(R.id.txtTitle);
        EditText txtDescr = findViewById(R.id.txtDescr);
        Spinner ddlCategory = findViewById(R.id.ddlCategory);
        Spinner ddlImportance = findViewById(R.id.ddlImportance);
        Spinner ddlAssignee = findViewById(R.id.ddlAssignee);

        String title = txtTitle.getText().toString().trim();
        String descr = txtDescr.getText().toString().trim();
        Category category = (Category) ddlCategory.getSelectedItem();
        Importance importance = (Importance) ddlImportance.getSelectedItem();
        User assignee = (User) ddlAssignee.getSelectedItem();
        String phoneNumber = txtPhoneNumber.getText().toString().trim();

        int assigneeID = assignee.getId();
        int creatorID = userConnected.getId();
        int categoryID = category.getId();
        int importanceID = importance.getId();
        int progressID = 1; //corresponds to default state.

        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("'Le' dd/MM/yyyy 'à' hh:mm");

        Issue issue = new Issue(
                assigneeID,
                creatorID,
                title,
                descr,
                selectedPath,
                format.format(currentDate),
                categoryID,
                progressID,
                importanceID,
                phoneNumber,
                selectedLocation.latitude,
                selectedLocation.longitude);

        issueViewModel.insert(issue);

        Log.d("New Issue created", issue.toString());

        Toast.makeText(this, "Incident ajouté", Toast.LENGTH_SHORT).show();

        if (!phoneNumber.equals(userConnected.getPhoneNumber())) {
            Uri uri = Uri.parse("smsto:" + phoneNumber);
            Intent sendIncidentIntent = new Intent(Intent.ACTION_SENDTO, uri);
            sendIncidentIntent.putExtra("sms_body", "Je viens d'ajouter votre numéro comme contact d'urgence " +
                    "sur un nouvel incident de PolyIncident, dont les détails sont : " + title + " : " + descr + ".");
            startActivity(sendIncidentIntent);
        }

        setResult(RESULT_OK);
        finish();
    }

    private void setUpSpinners() {
        int spinner_item_ID = android.R.layout.simple_spinner_dropdown_item;
        Context context = getApplicationContext();

        //Get ViewModels
        CategoryViewModel categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        ImportanceViewModel importanceViewModel = ViewModelProviders.of(this).get(ImportanceViewModel.class);
        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        //Create empty Adapters
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(context, spinner_item_ID, new ArrayList<>());
        ArrayAdapter<Importance> importanceAdapter = new ArrayAdapter<>(context, spinner_item_ID, new ArrayList<>());
        ArrayAdapter<User> userAdapter = new ArrayAdapter<>(context, spinner_item_ID, new ArrayList<>());

        //Get the Spinners from view
        Spinner ddlCategory = findViewById(R.id.ddlCategory);
        Spinner ddlImportance = findViewById(R.id.ddlImportance);
        Spinner ddlAssignee = findViewById(R.id.ddlAssignee);

        //Set the Adapters to the Spinners
        ddlCategory.setAdapter(categoryAdapter);
        ddlImportance.setAdapter(importanceAdapter);
        ddlAssignee.setAdapter(userAdapter);

        //Update Adapters content whenever getAll() changes
        categoryViewModel.getAll().observeForever(categories -> {
            if (categories != null && !categories.isEmpty()) {
                categoryAdapter.clear();
                categoryAdapter.addAll(categories);
            }
        });
        importanceViewModel.getAll().observeForever(importances -> {
            if (importances != null && !importances.isEmpty()) {
                importanceAdapter.clear();
                importanceAdapter.addAll(importances);
            }
        });
        userViewModel.getAll().observeForever(users -> {
            if (users != null && !users.isEmpty()) {
                userAdapter.clear();
                userAdapter.addAll(users);
                userViewModel.getLoggedIn().ifPresent(userAdapter::remove);
            }
        });
    }

    private void getImageFromAlbum() {
        if (checkSelfPermission(READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 1);
        }

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");

        startActivityForResult(photoPickerIntent, IMAGE_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CONTACT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Cursor cursor = null;
                    try {
                        String phoneNo;
                        // getData() avec le content uri du contact selectionne
                        Uri contactUri = data.getData();
                        //query le content uri
                        cursor = getContentResolver().query(contactUri, null, null, null, null);
                        cursor.moveToFirst();
                        int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        phoneNo = cursor.getString(phoneIndex);
                        txtPhoneNumber.setText(phoneNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                }
                break;

            case IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();

                    selectedPath = selectedImage.toString();

                    try {
                        Log.d("Debug.alex", selectedImage.toString());

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        ImageView imageView = findViewById(R.id.add_imageView);
                        imageView.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        Log.d("Debug.catch", "Some exception " + e.getStackTrace().toString());
                    }
                }
                break;

            case LOCATION_REQUEST_CODE:
                if (resultCode == RESULT_OK && data.hasExtra("location")) {
                    selectedLocation = (LatLng) data.getExtras().get("location");
                }
        }
    }
}
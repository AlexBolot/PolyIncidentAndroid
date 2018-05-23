package polytechnice.si3.ihm.android;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.InputStream;
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

public class AddingActivity extends AppCompatActivity {

    private String selectedPath = "";
    private static final int RESULT_PICK_CONTACT = 85500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding);

        int loggedIn = getIntent().getIntExtra("LoggedIn", 0);
        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getByID(loggedIn).ifPresent(userViewModel::logIn);

        findViewById(R.id.btn_add_pic).setOnClickListener(view -> getImageFromAlbum());

        //TODO : this is temporary
        findViewById(R.id.btn_add_pos).setOnClickListener(view -> request());

        findViewById(R.id.btn_add_issue).setOnClickListener(view -> addIssue());

        setUpSpinners();
    }

    public void pickContact(View v)
    {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    private void addIssue() {

        //Get ViewModel
        IssueViewModel issueViewModel = ViewModelProviders.of(this).get(IssueViewModel.class);
        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        //Get the fields from view
        EditText txtTitle = findViewById(R.id.txtTitle);
        EditText txtDescr = findViewById(R.id.txtDescr);
        Spinner ddlCategory = findViewById(R.id.ddlCategory);
        Spinner ddlImportance = findViewById(R.id.ddlImportance);
        Spinner ddlAssignee = findViewById(R.id.ddlAssignee);
        EditText txtPhoneNumber = findViewById(R.id.phoneNumber);

        String title = txtTitle.getText().toString().trim();
        String descr = txtDescr.getText().toString().trim();
        Category category = (Category) ddlCategory.getSelectedItem();
        Importance importance = (Importance) ddlImportance.getSelectedItem();
        User assignee = (User) ddlAssignee.getSelectedItem();
        String phoneNumber = txtPhoneNumber.getText().toString().trim();

        userViewModel.getLoggedIn().ifPresent(currentUser -> {
            int assigneeID = assignee.getId();
            int creatorID = currentUser.getId();
            int categoryID = category.getId();
            int importanceID = importance.getId();
            int progressID = 1; //corresponds to default state.

            Issue issue = new Issue(
                    assigneeID,
                    creatorID,
                    title,
                    descr,
                    selectedPath,
                    new Date().toString(),
                    categoryID,
                    progressID,
                    importanceID,
                    phoneNumber);

            issueViewModel.insert(issue);

            Log.d("BOB", issue.toString());
        });


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

    private void request() {

        if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, 1);

        } else {
            // Permission has already been granted

            Uri file = Uri.parse("content://media/external/images/media/110642");

            try (InputStream inputStream = getContentResolver().openInputStream(file)) {
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                ImageView imageView = findViewById(R.id.add_imageView);
                imageView.setImageBitmap(bmp);
            } catch (Exception e) {
                Log.d("Debug.catch", e.getMessage());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {

            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Uri file = Uri.parse("content://media/external/images/media/110642");

                    try (InputStream inputStream = getContentResolver().openInputStream(file)) {
                        Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                        ImageView imageView = findViewById(R.id.add_imageView);
                        imageView.setImageBitmap(bmp);
                    } catch (Exception e) {
                        Log.d("Debug.catch", e.getMessage());
                    }
                }
            }
        }
    }

    private void getImageFromAlbum() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
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
    }
}
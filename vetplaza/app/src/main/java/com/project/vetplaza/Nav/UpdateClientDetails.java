package com.project.vetplaza.Nav;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.vetplaza.Activity.Client.ClientActivity;
import com.project.vetplaza.Activity.Clinic.ClinicActivity;
import com.project.vetplaza.Auth.Login;
import com.project.vetplaza.Data.ClientData;
import com.project.vetplaza.R;

public class UpdateClientDetails extends AppCompatActivity {
    private DocumentReference documentReference;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private TextView phone, address, petMedicalHistory, petName, name, password, email;
    private AutoCompleteTextView petType;
    private Button button;
    private ImageView imageView;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri mImageUri;
    private ProgressBar progressBar;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String[] pet = new String[]{"Dog",
            "Cat", "Fish", "Bird", "Guinea Pig", "Others"};
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.additional_client_details);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("client");
        storageReference = FirebaseStorage.getInstance().getReference("imageFolder");
        String userID = fAuth.getCurrentUser().getUid();
        documentReference = fStore.collection("users").document(userID);

        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        petType = findViewById(R.id.petType);
        petName = findViewById(R.id.petName);
        petMedicalHistory = findViewById(R.id.petMedicalHistory);
        button = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);

        imageView = findViewById(R.id.imageView);

        button.setOnClickListener(view1 -> {
            if (imageView.getDrawable() != null) {
                save();
            } else {
                Toast.makeText(this, "Please add owner image", Toast.LENGTH_SHORT).show();
            }
        });

        imageView.setOnClickListener(v -> openFileChooser());

        setData();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, pet);
        petType.setAdapter(adapter);
    }

    private void setData() {
        if (fAuth.getCurrentUser() != null) {
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String _name = (String) document.get("name");
                    String _email = (String) document.get("email");
                    String _password = (String) document.get("password");

                    name.setText(_name);
                    email.setText(_email);
                    password.setText(_password);
                }
            });
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void save() {
        progressBar.setVisibility(View.VISIBLE);
        button.setVisibility(View.INVISIBLE);
            StorageReference Imagename = storageReference.child("image" + mImageUri.getLastPathSegment());
            Imagename.putFile(mImageUri).addOnSuccessListener(taskSnapshot ->
                    Imagename.getDownloadUrl().addOnSuccessListener(this::addData));
    }

    public void addData(Uri uri) {
        String id = databaseReference.push().getKey();
        ClientData data = new ClientData(id, uri.toString(), name.getText().toString(), email.getText().toString(),
                password.getText().toString(), phone.getText().toString(), address.getText().toString(),
                petName.getText().toString(), petType.getText().toString(), petMedicalHistory.getText().toString());
        databaseReference.child(id).setValue(data).addOnCompleteListener(task -> {
            String userID = fAuth.getCurrentUser().getUid();
            documentReference = fStore.collection("users").document(userID);
            documentReference.update("id", id).addOnCompleteListener(task1 -> {
                documentReference.update("firstTimeLogin", false).addOnCompleteListener(task2 -> {
                    documentReference.update("imageUrl", uri.toString()).addOnCompleteListener(task3 -> {
                        Intent intent = new Intent(getApplicationContext(), ClientActivity.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                        finishAffinity();
                    });
                });
            });
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Toast.makeText(this, "Added Succesfully", Toast.LENGTH_SHORT).show();
            mImageUri = data.getData();
            Glide.with(this).asBitmap().load(mImageUri).centerCrop().into(imageView);
        }
    }

}

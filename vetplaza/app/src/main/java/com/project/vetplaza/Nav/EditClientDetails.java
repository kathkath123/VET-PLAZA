package com.project.vetplaza.Nav;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.vetplaza.Activity.Client.ClientActivity;
import com.project.vetplaza.Data.ClientData;
import com.project.vetplaza.Data.ClinicData;
import com.project.vetplaza.R;

public class EditClientDetails extends DialogFragment {
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
    ProgressBar progressBar;
    ClientData clientData;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String[] pet = new String[]{"Dog",
            "Cat", "Fish", "Bird", "Guinea Pig", "Others"};


    public EditClientDetails() {
        // Required empty public constructor
    }

    public static EditClinicDetails newInstance() {
        EditClinicDetails fragment = new EditClinicDetails();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.edit_client_details, container, false);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("client");
        storageReference = FirebaseStorage.getInstance().getReference("imageFolder");
        String userID = fAuth.getCurrentUser().getUid();
        documentReference = fStore.collection("users").document(userID);


        name = view.findViewById(R.id.name);
        password = view.findViewById(R.id.password);
        email = view.findViewById(R.id.email);
        address = view.findViewById(R.id.address);
        phone = view.findViewById(R.id.phone);
        petType = view.findViewById(R.id.petType);
        petName = view.findViewById(R.id.petName);
        petMedicalHistory = view.findViewById(R.id.petMedicalHistory);
        button = view.findViewById(R.id.button);
        progressBar = view.findViewById(R.id.progressBar);

        imageView = view.findViewById(R.id.imageView);

        button.setOnClickListener(view1 -> {
                save();
        });

        imageView.setOnClickListener(v -> openFileChooser());


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(),
                android.R.layout.simple_dropdown_item_1line, pet);
        petType.setAdapter(adapter);

        if (getArguments() != null) {
            clientData = (ClientData) getArguments().getSerializable("data");
            setData(clientData);
        }
        return view;
    }

    private void setData(ClientData clientData) {
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
                    phone.setText(clientData.phone);
                    address.setText(clientData.address);
                    petName.setText(clientData.petName);
                    petType.setText(clientData.petType, false);
                    Glide.with(requireActivity()).asBitmap().load(clientData.imageUri).centerCrop().into(imageView);
                    petMedicalHistory.setText(clientData.petMedicalHistory);
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

        if (email.getText().toString().trim() != clientData.email && password.getText().toString().trim() != clientData.password) {
            FirebaseUser user = fAuth.getCurrentUser();
            AuthCredential credential = EmailAuthProvider
                    .getCredential(clientData.email, clientData.password);
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        FirebaseUser user1 = fAuth.getCurrentUser();
                        user1.updateEmail(email.getText().toString().trim()).addOnCompleteListener( task2 -> {
                            user1.updatePassword(password.getText().toString().trim()).addOnCompleteListener(
                                    task3 -> {
                                        if (mImageUri != null) {
                                            StorageReference Imagename = storageReference.child("image" + mImageUri.getLastPathSegment());
                                            Imagename.putFile(mImageUri).addOnSuccessListener(taskSnapshot ->
                                                    Imagename.getDownloadUrl().addOnSuccessListener(this::addData));
                                        } else {
                                            addData();
                                        }
                                    }
                            );

                        });
                    });
        } else {
            if (mImageUri != null) {
                StorageReference Imagename = storageReference.child("image" + mImageUri.getLastPathSegment());
                Imagename.putFile(mImageUri).addOnSuccessListener(taskSnapshot ->
                        Imagename.getDownloadUrl().addOnSuccessListener(this::addData));
            } else {
                addData();
            }
        }
    }

    public void addData(Uri uri) {
        String id = databaseReference.push().getKey();
        ClientData data = new ClientData(id, uri.toString(), name.getText().toString(), email.getText().toString(),
                password.getText().toString(), phone.getText().toString(), address.getText().toString(),
                petName.getText().toString(), petType.getText().toString(), petMedicalHistory.getText().toString());
        databaseReference.child(id).setValue(data).addOnCompleteListener(task -> {
            String userID = fAuth.getCurrentUser().getUid();
            documentReference = fStore.collection("users").document(userID);
            documentReference.update("name", name.getText().toString(),
                    "imageUrl", uri.toString(), "email", email.getText().toString(),
                    "password", password.getText().toString()).addOnCompleteListener(task3 -> {
                fAuth.getCurrentUser().updateEmail(email.getText().toString()).addOnCompleteListener(task1 ->
                        fAuth.getCurrentUser().updatePassword(password.getText().toString()).addOnCompleteListener(task2 -> dismiss())
                );
            });
        });
    }


    public void addData() {
        ClientData data = new ClientData(clientData.getId(), clientData.imageUri , name.getText().toString(), email.getText().toString(),
                password.getText().toString(), phone.getText().toString(), address.getText().toString(),
                petName.getText().toString(), petType.getText().toString(), petMedicalHistory.getText().toString());
        databaseReference.child(clientData.getId()).setValue(data).addOnCompleteListener(task -> {
            String userID = fAuth.getCurrentUser().getUid();
            documentReference = fStore.collection("users").document(userID);
            documentReference.update("name", name.getText().toString(), "email", email.getText().toString(),
                    "password", password.getText().toString()).addOnCompleteListener(task3 -> {
                fAuth.getCurrentUser().updateEmail(email.getText().toString()).addOnCompleteListener(task1 ->
                        fAuth.getCurrentUser().updatePassword(password.getText().toString()).addOnCompleteListener(task2 -> dismiss())
                );
            });
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Toast.makeText(requireActivity(), "Added Succesfully", Toast.LENGTH_SHORT).show();
            mImageUri = data.getData();
            Glide.with(this).asBitmap().load(mImageUri).centerCrop().into(imageView);
        }
    }

}

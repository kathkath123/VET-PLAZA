package com.project.vetplaza.Nav;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.project.vetplaza.Data.ClinicData;
import com.project.vetplaza.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class UpdateClinicDetails extends AppCompatActivity {
    private DocumentReference documentReference;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private TextView phone, address, clinicHours, name, password, email, about, start, end, gcash;
    private String servicesType;
    private Button button;
    private ImageView imageView;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    Uri mImageUri;
    private ProgressBar progressBar;
    LinearLayout timeStart,timeEnd;
    final Calendar timeStarts= Calendar.getInstance();
    final Calendar timeEnds = Calendar.getInstance();
    TimePickerDialog.OnTimeSetListener timeStartsListener;
    TimePickerDialog.OnTimeSetListener timeEndsListener;
    public CheckBox grooming, pharmacy, dentistry, surgical, laboratory, emergency;

    private static final int PICK_IMAGE_REQUEST = 1;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.additional_hospital_details);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("clinic");
        storageReference = FirebaseStorage.getInstance().getReference("imageFolder");
        String userID = fAuth.getCurrentUser().getUid();
        documentReference = fStore.collection("users").document(userID);

        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        button = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);
        timeStart = findViewById(R.id.input7);
        timeEnd  = findViewById(R.id.input8);

        about = findViewById(R.id.about);
        start = findViewById(R.id.hoursIn);
        end = findViewById(R.id.hoursOut);

        gcash = findViewById(R.id.gcashNumber);

        imageView = findViewById(R.id.imageView);

        grooming = findViewById(R.id.grooming);
        pharmacy = findViewById(R.id.pharmacy);
        dentistry = findViewById(R.id.dentistry);
        surgical = findViewById(R.id.surgical);
        laboratory = findViewById(R.id.laboratory);
        emergency = findViewById(R.id.emergency);


        button.setOnClickListener(view1 -> {
            if (imageView.getDrawable() != null) {
                save();
            } else {
                Toast.makeText(this, "Please add clinic image", Toast.LENGTH_SHORT).show();
            }
        });

        imageView.setOnClickListener(v -> openFileChooser());

        timeStart.setOnClickListener( v-> {
            new TimePickerDialog(this, timeStartsListener, timeStarts.get(Calendar.HOUR_OF_DAY), timeStarts.get(Calendar.MINUTE), false).show();
        });

        start.setOnClickListener( v-> {
            new TimePickerDialog(this, timeStartsListener, timeStarts.get(Calendar.HOUR_OF_DAY), timeStarts.get(Calendar.MINUTE), false).show();
        });

        timeEnd.setOnClickListener( v-> {
            new TimePickerDialog(this, timeEndsListener, timeEnds.get(Calendar.HOUR_OF_DAY), timeEnds.get(Calendar.MINUTE), false).show();
        });

        end.setOnClickListener( v-> {
            new TimePickerDialog(this, timeEndsListener, timeEnds.get(Calendar.HOUR_OF_DAY), timeEnds.get(Calendar.MINUTE), false).show();
        });




        setData();

       timePicker();
    }

    private void timePicker() {
        timeStartsListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hour, int minute) {
                updateTimeStart(hour, minute);
            }
        };


        timeEndsListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hour, int minute) {
                updateTimeEnd(hour, minute);
            }
        };
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
        DateFormat df = new SimpleDateFormat("EEE,MMMM d yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        String clinicHours = start.getText().toString() + " - " + end.getText().toString();
        Boolean cbGrooming = grooming.isChecked();
        Boolean cbPharmacy = pharmacy.isChecked();
        Boolean cbDentistry = dentistry.isChecked();
        Boolean cbSurgical = surgical.isChecked();
        Boolean cbLaboratory = laboratory.isChecked();
        Boolean cbEmergency = emergency.isChecked();

        String id = databaseReference.push().getKey();
        ClinicData data = new ClinicData(id, uri.toString(), name.getText().toString(), email.getText().toString(),
                password.getText().toString(), phone.getText().toString(), address.getText().toString(),  clinicHours, "", date, about.getText().toString(), gcash.getText().toString(),
                cbGrooming, cbPharmacy, cbDentistry, cbSurgical, cbLaboratory, cbEmergency);
        databaseReference.child(id).setValue(data).addOnCompleteListener(task -> {
            String userID = fAuth.getCurrentUser().getUid();
            documentReference = fStore.collection("users").document(userID);
            documentReference.update("id", id).addOnCompleteListener(task1 -> {
                documentReference.update("firstTimeLogin", false).addOnCompleteListener(task2 -> {
                    documentReference.update("imageUrl", uri.toString()).addOnCompleteListener(task3 -> {
                        Intent intent = new Intent(getApplicationContext(), ClinicActivity.class);
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


    private void updateTimeStart(int hour, int minute) {
        timeStarts.set(Calendar.HOUR, hour);
        timeStarts.set(Calendar.MINUTE, minute);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        start.setText(format.format(timeStarts.getTime()));
    }

    public void updateTimeEnd(int hour, int minute) {
        timeEnds.set(Calendar.HOUR, hour);
        timeEnds.set(Calendar.MINUTE, minute);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        end.setText(format.format(timeEnds.getTime()));
    }
}

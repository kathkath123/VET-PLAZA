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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.vetplaza.Activity.Client.ClientActivity;
import com.project.vetplaza.Activity.ClinicDetailsFragment;
import com.project.vetplaza.Data.ClinicData;
import com.project.vetplaza.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class EditClinicDetails extends DialogFragment {
    private DocumentReference documentReference;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private Button button;
    private ImageView imageView;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    Uri mImageUri;
    ClinicData clinicData;
    private static final int PICK_IMAGE_REQUEST = 1;
    private TextView phone, address, clinicHours, name, password, email, about, start, end, gcash;
    private ProgressBar progressBar;
    LinearLayout timeStart, timeEnd;
    final Calendar timeStarts = Calendar.getInstance();
    final Calendar timeEnds = Calendar.getInstance();
    TimePickerDialog.OnTimeSetListener timeStartsListener;
    TimePickerDialog.OnTimeSetListener timeEndsListener;

    public CheckBox grooming, pharmacy, dentistry, surgical, laboratory, emergency;


    public EditClinicDetails() {
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
        View view = inflater.inflate(R.layout.edit_hospital_details, container, false);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("clinic");
        storageReference = FirebaseStorage.getInstance().getReference("imageFolder");
        String userID = fAuth.getCurrentUser().getUid();
        documentReference = fStore.collection("users").document(userID);
        name = view.findViewById(R.id.name);
        password = view.findViewById(R.id.password);
        email = view.findViewById(R.id.email);
        address = view.findViewById(R.id.address);
        phone = view.findViewById(R.id.phone);
        button = view.findViewById(R.id.button);
        progressBar = view.findViewById(R.id.progressBar);
        timeStart = view.findViewById(R.id.input7);
        timeEnd = view.findViewById(R.id.input8);

        about = view.findViewById(R.id.about);
        start = view.findViewById(R.id.hoursIn);
        end = view.findViewById(R.id.hoursOut);

        gcash = view.findViewById(R.id.gcashNumber);

        imageView = view.findViewById(R.id.imageView);

        grooming = view.findViewById(R.id.grooming);
        pharmacy = view.findViewById(R.id.pharmacy);
        dentistry = view.findViewById(R.id.dentistry);
        surgical = view.findViewById(R.id.surgical);
        laboratory = view.findViewById(R.id.laboratory);
        emergency = view.findViewById(R.id.emergency);

        button.setOnClickListener(view1 -> {
            save();
        });

        imageView.setOnClickListener(v -> openFileChooser());

        timeStart.setOnClickListener(v -> {
            new TimePickerDialog(requireActivity(), timeStartsListener, timeStarts.get(Calendar.HOUR_OF_DAY), timeStarts.get(Calendar.MINUTE), false).show();
        });

        start.setOnClickListener(v -> {
            new TimePickerDialog(requireActivity(), timeStartsListener, timeStarts.get(Calendar.HOUR_OF_DAY), timeStarts.get(Calendar.MINUTE), false).show();
        });

        timeEnd.setOnClickListener(v -> {
            new TimePickerDialog(requireActivity(), timeEndsListener, timeEnds.get(Calendar.HOUR_OF_DAY), timeEnds.get(Calendar.MINUTE), false).show();
        });

        end.setOnClickListener(v -> {
            new TimePickerDialog(requireActivity(), timeEndsListener, timeEnds.get(Calendar.HOUR_OF_DAY), timeEnds.get(Calendar.MINUTE), false).show();
        });


        if (getArguments() != null) {
            clinicData = (ClinicData) getArguments().getSerializable("data");
            setData(clinicData);
        }



        timePicker();
        return view;
    }

    private void setData(ClinicData clinicData) {
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
                    phone.setText(clinicData.phone);
                    address.setText(clinicData.address);
                    start.setText(clinicData.time.substring(0, clinicData.time.indexOf(" -")));
                    end.setText(clinicData.time.substring(clinicData.time.lastIndexOf("- ") + 1));
                    Glide.with(requireActivity()).asBitmap().load(clinicData.imageUri).centerCrop().into(imageView);
                    gcash.setText(clinicData.gcash);
                    about.setText(clinicData.about);
                    grooming.setChecked(clinicData.grooming);
                    pharmacy.setChecked(clinicData.pharmacy);
                    dentistry.setChecked(clinicData.dentistry);
                    surgical.setChecked(clinicData.surgical);
                    laboratory.setChecked(clinicData.laboratory);
                    emergency.setChecked(clinicData.emergency);
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

        if (email.getText().toString().trim() != clinicData.email && password.getText().toString().trim() != clinicData.password) {
            FirebaseUser user = fAuth.getCurrentUser();
            AuthCredential credential = EmailAuthProvider
                    .getCredential(clinicData.email, clinicData.password);
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        FirebaseUser user1 = fAuth.getCurrentUser();
                        user1.updateEmail(email.getText().toString().trim()).addOnCompleteListener(task2 -> {
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
        DateFormat df = new SimpleDateFormat("EEE , MMMM d yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        Boolean cbGrooming = grooming.isChecked();
        Boolean cbPharmacy = pharmacy.isChecked();
        Boolean cbDentistry = dentistry.isChecked();
        Boolean cbSurgical = surgical.isChecked();
        Boolean cbLaboratory = laboratory.isChecked();
        Boolean cbEmergency = emergency.isChecked();
        ClinicData data = new ClinicData(clinicData.getId(), uri.toString(), name.getText().toString(), email.getText().toString(),
                password.getText().toString(), phone.getText().toString(), address.getText().toString(), start.getText().toString() + " - " + end.getText().toString(), clinicData.hasApproved, date, about.getText().toString(), gcash.getText().toString(), cbGrooming, cbPharmacy, cbDentistry, cbSurgical, cbLaboratory, cbEmergency);
        databaseReference.child(clinicData.getId()).setValue(data).addOnCompleteListener(task -> {
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
        DateFormat df = new SimpleDateFormat("EEE , MMMM d yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        Boolean cbGrooming = grooming.isChecked();
        Boolean cbPharmacy = pharmacy.isChecked();
        Boolean cbDentistry = dentistry.isChecked();
        Boolean cbSurgical = surgical.isChecked();
        Boolean cbLaboratory = laboratory.isChecked();
        Boolean cbEmergency = emergency.isChecked();
        ClinicData data = new ClinicData(clinicData.getId(), clinicData.getImageUri(), name.getText().toString(), email.getText().toString(),
                password.getText().toString(), phone.getText().toString(), address.getText().toString(), start.getText().toString() + " - " + end.getText().toString(), clinicData.hasApproved, date, about.getText().toString(), gcash.getText().toString(), cbGrooming, cbPharmacy, cbDentistry, cbSurgical, cbLaboratory, cbEmergency);
        databaseReference.child(clinicData.getId()).setValue(data).addOnCompleteListener(task -> {
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

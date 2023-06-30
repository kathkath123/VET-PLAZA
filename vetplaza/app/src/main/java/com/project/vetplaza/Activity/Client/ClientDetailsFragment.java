package com.project.vetplaza.Activity.Client;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.vetplaza.Activity.Appointment.BookAppointmentFragment;
import com.project.vetplaza.Data.AppointmentData;
import com.project.vetplaza.Data.ClientData;
import com.project.vetplaza.Data.ClinicData;
import com.project.vetplaza.R;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;


public class ClientDetailsFragment extends DialogFragment {
    TextView address, name, phone, pet_info, pet_history;
    RelativeLayout login;
    ImageView imgurl,delete;
    Button save, cancel;
    private DatabaseReference dataRefClient;
    private DatabaseReference dataRefClinic;
    String id;
    public TextView grooming, pharmacy, dentistry, surgical, laboratory, emergency;

    private ClientData clientData;
    private AppointmentData appointmentData;

    public ClientDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.client_details, container, false);

        address = view.findViewById(R.id.address);
        name = view.findViewById(R.id.title);
        pet_history = view.findViewById(R.id.pet_history);
        pet_info = view.findViewById(R.id.pet_info);
        imgurl = view.findViewById(R.id.profile_image);
        phone = view.findViewById(R.id.phone);

        grooming = view.findViewById(R.id.grooming);
        pharmacy = view.findViewById(R.id.pharmacy);
        dentistry = view.findViewById(R.id.dentistry);
        surgical = view.findViewById(R.id.surgical);
        laboratory = view.findViewById(R.id.laboratory);
        emergency = view.findViewById(R.id.emergency);

        imgurl.setOnClickListener(v ->
                showImage());

        if (getArguments() != null) {

            clientData = (ClientData) getArguments().getSerializable("data");
            appointmentData = (AppointmentData) getArguments().getSerializable("appointmentData");
            dataRefClinic = FirebaseDatabase.getInstance().getReference("client").child(clientData.getId());
            setData(clientData, appointmentData);
        }

        return view;
    }

    private void showImage() {
        final Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_image_slider);
        ImageView imgViewer = dialog.findViewById(R.id.imgViewer);
        Glide.with(this).load(clientData.getImageUri()).into(imgViewer);
        dialog.show();

        imgViewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void setData(ClientData clientData,AppointmentData appointmentData) {
        address.setText(clientData.address);
        name.setText(clientData.name);
        pet_info.setText(clientData.petName + " / " + clientData.petType);
        Glide.with(this).load(clientData.imageUri).into(imgurl);
        phone.setText(clientData.phone);
        pet_history.setText(clientData.petMedicalHistory);

        if(appointmentData.grooming){
            grooming.setVisibility(View.VISIBLE);
        } else {
            grooming.setVisibility(View.GONE);
        }
        if(appointmentData.pharmacy){
            pharmacy.setVisibility(View.VISIBLE);
        } else {
            pharmacy.setVisibility(View.GONE);
        }
        if(appointmentData.dentistry){
            dentistry.setVisibility(View.VISIBLE);
        } else {
            dentistry.setVisibility(View.GONE);
        }
        if(appointmentData.surgical){
            surgical.setVisibility(View.VISIBLE);
        } else {
            surgical.setVisibility(View.GONE);
        }
        if(appointmentData.laboratory){
            laboratory.setVisibility(View.VISIBLE);
        } else {
            laboratory.setVisibility(View.GONE);
        }

        if(appointmentData.emergency){
            emergency.setVisibility(View.VISIBLE);
        } else {
            emergency.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}

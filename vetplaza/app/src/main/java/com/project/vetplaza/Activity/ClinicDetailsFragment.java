package com.project.vetplaza.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.vetplaza.Activity.Appointment.BookAppointmentFragment;
import com.project.vetplaza.Data.AppointmentData;
import com.project.vetplaza.Data.ClinicData;
import com.project.vetplaza.Messaging.MainActivity;
import com.project.vetplaza.Nav.EditClinicDetails;
import com.project.vetplaza.R;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ClinicDetailsFragment extends DialogFragment {
    TextView address, name, time, phone, login_time, about, gcashNumber;
    RelativeLayout login;
    ImageView imgurl, delete, message;
    Button save, cancel;
    private DatabaseReference dataRefClient;
    private DatabaseReference dataRefClinic;
    String id;
    public TextView grooming, pharmacy, dentistry, surgical, laboratory, emergency;

    private ClinicData clinicData;

    public ClinicDetailsFragment() {
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
        View view = inflater.inflate(R.layout.clinic_details, container, false);

        address = view.findViewById(R.id.address);
        name = view.findViewById(R.id.title);
        time = view.findViewById(R.id.time);
        imgurl = view.findViewById(R.id.profile_image);
        phone = view.findViewById(R.id.phone);
        save = view.findViewById(R.id.save);
        delete = view.findViewById(R.id.delete);
        message = view.findViewById(R.id.message);
        cancel = view.findViewById(R.id.cancel);
        login = view.findViewById(R.id.logged_in_layout);
        login_time = view.findViewById(R.id.last_login);
        about = view.findViewById(R.id.about_info);
        gcashNumber = view.findViewById(R.id.gcashNumber);

        grooming = view.findViewById(R.id.grooming);
        pharmacy = view.findViewById(R.id.pharmacy);
        dentistry = view.findViewById(R.id.dentistry);
        surgical = view.findViewById(R.id.surgical);
        laboratory = view.findViewById(R.id.laboratory);
        emergency = view.findViewById(R.id.emergency);

        imgurl.setOnClickListener(v ->
                showImage());

        if (getArguments() != null) {
            id = getArguments().getString("id");

            if (id.equals("admin")) {
                save.setText("Approve");
                cancel.setText("Decline");
            } else if (id.equals("admin_approved")) {
                save.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                login.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
            } else if (id.equals("appointment")) {
                save.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
            } else {
                dataRefClient = FirebaseDatabase.getInstance().getReference("client").child(id);
            }


            if (Objects.equals(getArguments().getString("message"), "true")) {
                message.setVisibility(View.VISIBLE);
            }
            clinicData = (ClinicData) getArguments().getSerializable("data");
            dataRefClinic = FirebaseDatabase.getInstance().getReference("clinic").child(clinicData.getId());
            setData(clinicData);
        }

        save.setOnClickListener(v -> {
                    if (getArguments() != null) {
                        if (id.equals("admin")) {
                            dialog("true", "Are you sure to approve?");
                        } else {
                            bookAppointment(clinicData);
                        }
                    }
                }
        );

        cancel.setOnClickListener(v ->
                {
                    if (getArguments() != null) {
                        if (id.equals("admin")) {
                            dialog("false", "Are you sure to decline?");
                        } else {
                            dismiss();
                        }
                    }
                }
        );

        delete.setOnClickListener(v ->
                {
                    dataRefClinic.child("hasApproved").setValue("deleted").addOnCompleteListener(task2 -> {
                        dismiss();
                    });
                }
        );

        message.setOnClickListener(v ->
                {
                    Intent intent = new Intent(requireContext(), MainActivity.class);
                    intent.putExtra("clinicId", clinicData.getId());
                    intent.putExtra("clientId", id);
                    intent.putExtra("origin", "client");
                    intent.putExtra("imageUri", getArguments().getSerializable("imageUri"));
                    intent.putExtra("name", getArguments().getString("name"));
                    startActivity(intent);
                }
        );

        return view;
    }

    private void showImage() {

        final Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_image_slider);
        ImageView imgViewer = dialog.findViewById(R.id.imgViewer);
        Glide.with(this).load(clinicData.getImageUri()).into(imgViewer);
        dialog.show();

        imgViewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void bookAppointment(ClinicData clinicData) {
        Bundle args = new Bundle();
        args.putSerializable("data", clinicData);
        args.putString("id", id);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        BookAppointmentFragment bookAppointmentFragment = new BookAppointmentFragment();
        bookAppointmentFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        bookAppointmentFragment.setArguments(args);
        bookAppointmentFragment.show(ft, "bookAppointment");
    }

    private void setData(ClinicData clinicData) {
        address.setText(clinicData.getAddress());
        name.setText(clinicData.getName());
        time.setText(clinicData.getTime());
        Glide.with(this).load(clinicData.getImageUri()).into(imgurl);
        phone.setText(clinicData.getPhone());
        if (id.equals("admin_approved")) {
            login_time.setText(clinicData.getLastLogin());
        }
        about.setText(clinicData.about);
        gcashNumber.setText(clinicData.getGcash());

        if(clinicData.grooming){
            grooming.setVisibility(View.VISIBLE);
        } else {
            grooming.setVisibility(View.GONE);
        }
        if(clinicData.pharmacy){
            pharmacy.setVisibility(View.VISIBLE);
        } else {
            pharmacy.setVisibility(View.GONE);
        }
        if(clinicData.dentistry){
            dentistry.setVisibility(View.VISIBLE);
        } else {
            dentistry.setVisibility(View.GONE);
        }
        if(clinicData.surgical){
            surgical.setVisibility(View.VISIBLE);
        } else {
            surgical.setVisibility(View.GONE);
        }
        if(clinicData.laboratory){
            laboratory.setVisibility(View.VISIBLE);
        } else {
            laboratory.setVisibility(View.GONE);
        }

        if(clinicData.emergency){
            emergency.setVisibility(View.VISIBLE);
        } else {
            emergency.setVisibility(View.GONE);
        }

}

    private void dialog(String save, String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Confirm");
        builder.setMessage(description);

        builder.setPositiveButton("YES", (dialog, which) -> {
            if (save.equals("true")) {
                dataRefClinic.child("hasApproved").setValue("true").addOnCompleteListener(task2 -> {
                    sendPushNotification("Customers will see your account", "You're clinic has been approved", clinicData.getId(), this);
                });
            } else {
                dataRefClinic.child("hasApproved").setValue("false").addOnCompleteListener(task2 -> {
                    dialog.dismiss();
                    dismiss();
                });
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    public static void sendPushNotification(final String body, final String title, String id, ClinicDetailsFragment clinicDetailsFragment) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String finalResponse = null;
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                JSONObject notificationJson = new JSONObject();
                JSONObject dataJson = new JSONObject();
                try {
                    notificationJson.put("body", body);
                    notificationJson.put("title", title);
                    notificationJson.put("priority", "high");
                    dataJson.put("customId", "02");
                    dataJson.put("badge", 1);
                    dataJson.put("alert", "Alert");
                    json.put("notification", notificationJson);
                    json.put("data", dataJson);
                    json.put("to", "/topics/" + id);
                    json.put("token", "/topics/" + id);
                    RequestBody body1 = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=AAAAsqxwtMs:APA91bHZo5c63mQttdodgj-LKAeoDx5tMHx8fJ6tL5Rhc7lHf_dL8skyCkrWt2mwBeMt2NiOcXpwY_yzgPl4DtF2uBO1IZCExx5xsXOSIOz0JHAImbyuHRdVyaVvBesG7xC4HYrd47Eg")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body1)
                            .build();
                    Response response = client.newCall(request).execute();
                    finalResponse = response.body().string();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return finalResponse;
            }


            @Override
            protected void onPostExecute(String auth2) {
                if (auth2.contains("message_id")) {
                    cancel(true);
                    clinicDetailsFragment.getDialog().dismiss();
                    clinicDetailsFragment.dismiss();
                }
            }
        }.execute();
    }

}

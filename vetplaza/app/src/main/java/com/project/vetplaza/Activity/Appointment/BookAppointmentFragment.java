package com.project.vetplaza.Activity.Appointment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.project.vetplaza.Data.AppointmentData;
import com.project.vetplaza.Data.ClinicData;
import com.project.vetplaza.R;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class BookAppointmentFragment extends DialogFragment {
    TextView date, name, time, phone, login_time;
    RelativeLayout login;
    ImageView imgurl, back;
    Button save, cancel;
    private DatabaseReference dataRefAppointment;
    String id;
    final Calendar myCalendar = Calendar.getInstance();
    final Calendar myTime = Calendar.getInstance();
    private ClinicData clinicData;
    DatePickerDialog.OnDateSetListener datepick;
    TimePickerDialog.OnTimeSetListener mTimeListener;
    public CheckBox grooming, pharmacy, dentistry, surgical, laboratory, emergency;

    public BookAppointmentFragment() {
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
        View view = inflater.inflate(R.layout.book_appointment, container, false);

        name = view.findViewById(R.id.name);
        date = view.findViewById(R.id.date);
        time = view.findViewById(R.id.time);
        phone = view.findViewById(R.id.phone);
        save = view.findViewById(R.id.save);
        back = view.findViewById(R.id.cancel);

        grooming = view.findViewById(R.id.grooming);
        pharmacy = view.findViewById(R.id.pharmacy);
        dentistry = view.findViewById(R.id.dentistry);
        surgical = view.findViewById(R.id.surgical);
        laboratory = view.findViewById(R.id.laboratory);
        emergency = view.findViewById(R.id.emergency);


        if (getArguments() != null) {
            id = (String) getArguments().getString("id");

            clinicData = (ClinicData) getArguments().getSerializable("data");
            dataRefAppointment = FirebaseDatabase.getInstance().getReference("appointment");
        }

        save.setOnClickListener(v -> {
                    dialog();
                }
        );

        back.setOnClickListener(v -> {
                    dismiss();
                }
        );

        date.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), datepick, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });


        time.setOnClickListener(v -> {
            new TimePickerDialog(getContext(), mTimeListener, myTime.get(Calendar.HOUR_OF_DAY), myTime.get(Calendar.MINUTE), true).show();
        });

        datePicker();


        return view;
    }


    private void bookAppointment(ClinicData clinicData, DialogInterface dialog) {
        if (getArguments() != null) {
            String key = dataRefAppointment.push().getKey();
            String id = getArguments().getString("id");
            String getName = name.getText().toString();
            String getDate = date.getText().toString();
            String getTime = time.getText().toString();
            String getPhone = phone.getText().toString();
            Boolean cbGrooming = grooming.isChecked();
            Boolean cbPharmacy = pharmacy.isChecked();
            Boolean cbDentistry = dentistry.isChecked();
            Boolean cbSurgical = surgical.isChecked();
            Boolean cbLaboratory = laboratory.isChecked();
            Boolean cbEmergency = emergency.isChecked();
            AppointmentData appointmentData = new AppointmentData(key, getName, getDate, getTime, getPhone, clinicData.getImageUri(), "", id, clinicData.getId(), cbGrooming, cbPharmacy, cbDentistry, cbSurgical, cbLaboratory, cbEmergency );
            dataRefAppointment.child(clinicData.getId()).child(key).setValue(appointmentData).addOnCompleteListener(task1 -> {
                dataRefAppointment.child(id).child(key).setValue(appointmentData).addOnCompleteListener(task2 -> {
                    sendPushNotification("Click here to check it out","You have new appointment", clinicData.getId(), this);
                });
            });
        }
    }


    private void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to book appointment?");

        builder.setPositiveButton("YES", (dialog, which) -> {
            bookAppointment(clinicData, dialog);
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    public void datePicker() {
        datepick = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            }
        };

        mTimeListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hour, int minute) {
                updateTime(hour, minute);
            }
        };
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        date.setText(dateFormat.format(myCalendar.getTime()));
    }

    public void updateTime(int hour, int minute) {
        myTime.set(Calendar.HOUR, hour);
        myTime.set(Calendar.MINUTE, minute);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        time.setText(format.format(myTime.getTime()));
    }


    public static void sendPushNotification(final String body, final String title, String id, BookAppointmentFragment bookAppointmentFragment) {
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
                    json.put("to","/topics/" + id);
                    json.put("token","/topics/" + id);
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

                return finalResponse + "12";
            }


            @Override
            protected void onPostExecute(String auth2) {
                    if(auth2.contains("message_id")){
                        cancel(true);
                        bookAppointmentFragment.dismiss();
                    }
            }
        }.execute();
    }
}

package com.project.vetplaza.Activity.Clinic;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.vetplaza.Activity.Client.ClientDetailsFragment;
import com.project.vetplaza.Activity.ClinicDetailsFragment;
import com.project.vetplaza.Auth.Login;
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


public class AppointmentRequestActivity extends AppCompatActivity implements ClinicRequestListAdapter.OnShareClickedListener, ClinicRequestListAdapter.OnViewClickedListener {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private RecyclerView recyclerView;
    private ClinicRequestListAdapter adapter;

    FirebaseRecyclerOptions<AppointmentData> options;
    String id;
    private DatabaseReference dataRefClient;
    private DatabaseReference dataRefClinic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_appointment_request);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataRefClinic = FirebaseDatabase.getInstance().getReference("appointment");

        if (fAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finishAffinity();
        }


        recyclerView = findViewById(R.id.recycler1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        options = new FirebaseRecyclerOptions.Builder<AppointmentData>()
                .setQuery(dataRefClinic.child(getIntent().getExtras().getString("id")).orderByChild("hasAccepted").equalTo(""), AppointmentData.class)
                .build();
        adapter = new ClinicRequestListAdapter(options, this);
        recyclerView.setAdapter(adapter);

        adapter.setOnShareClickedListener(this);
        adapter.setOnViewClickedListener(this);

//        orderByChild("clientId").equalTo(getIntent().getExtras().getString("id"))

    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onClicked(AppointmentData data, Boolean hasApprove) {
        if(hasApprove){
            sendPushNotification("Check it out now","Your appointment has been accepted", data.getUserId());
        } else {
            sendPushNotification("Inquire to the clinic","Your appointment has been declined", data.getUserId());
        }
        dataRefClinic.child(getIntent().getExtras().getString("id")).child(data.getId()).child("hasAccepted").setValue(hasApprove.toString()).addOnCompleteListener(
                task -> dataRefClinic.child(data.getUserId()).child(data.getId()).child("hasAccepted").setValue(hasApprove.toString())
        );
    }

    public static void sendPushNotification(final String body, final String title, String id) {
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

                return finalResponse;
            }

            @Override
            protected void onPostExecute(String auth2) {
                if(auth2.contains("message_id")){
                    cancel(true);
                }
            }
        }.execute();
    }

    @Override
    public void onClicked(AppointmentData data) {
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("client");
        Query query = firebaseDatabase.orderByChild("id").equalTo(data.userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot activitySnapShot : snapshot.getChildren()) {
                        ClientData receivedData = activitySnapShot.getValue(ClientData.class);
                        Bundle args = new Bundle();
                        args.putSerializable("data", receivedData);
                        args.putSerializable("appointmentData", data);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ClientDetailsFragment clientDetailsFragment = new ClientDetailsFragment();
                        clientDetailsFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
                        clientDetailsFragment.setArguments(args);
                        clientDetailsFragment.show(ft, "profile");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

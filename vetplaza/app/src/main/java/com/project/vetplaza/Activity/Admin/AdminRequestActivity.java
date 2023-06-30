package com.project.vetplaza.Activity.Admin;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.vetplaza.Activity.Clinic.ClinicRequestListAdapter;
import com.project.vetplaza.Activity.ClinicDetailsFragment;
import com.project.vetplaza.Auth.Login;
import com.project.vetplaza.Data.AppointmentData;
import com.project.vetplaza.Data.ClinicData;
import com.project.vetplaza.R;


public class AdminRequestActivity extends AppCompatActivity implements AdminClinicRequestListAdapter.OnShareClickedListener {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private RecyclerView recyclerView;
    private AdminClinicRequestListAdapter adapter;

    FirebaseRecyclerOptions<ClinicData> options;
    String id;
    private DatabaseReference dataRefClient;
    private Query dataRefClinic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin_request);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataRefClinic = FirebaseDatabase.getInstance().getReference("clinic").orderByChild("hasApproved").equalTo("");

        if (fAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finishAffinity();
        }


        recyclerView = findViewById(R.id.recycler1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);


        options = new FirebaseRecyclerOptions.Builder<ClinicData>()
                .setQuery(dataRefClinic, ClinicData.class)
                .build();
        adapter = new AdminClinicRequestListAdapter(options, this);
        recyclerView.setAdapter(adapter);

        adapter.setOnShareClickedListener(this);

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
    public void onClicked(ClinicData data) {
        Bundle args = new Bundle();
        args.putSerializable("data", data);
        args.putString("id", "admin");
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ClinicDetailsFragment clinicDetails = new ClinicDetailsFragment();
        clinicDetails.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        clinicDetails.setArguments(args);
        clinicDetails.show(ft, "profile");
    }


}

package com.project.vetplaza.Activity.Clinic;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.project.vetplaza.Activity.Client.ClientDetailsFragment;
import com.project.vetplaza.Auth.Login;
import com.project.vetplaza.Data.AppointmentData;
import com.project.vetplaza.Data.ClientData;
import com.project.vetplaza.Data.ClinicData;
import com.project.vetplaza.Messaging.MainActivity;
import com.project.vetplaza.Nav.EditClinicDetails;
import com.project.vetplaza.Nav.UpdateClinicDetails;
import com.project.vetplaza.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class ClinicActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ClinicAppointmentListAdapter.OnShareClickedListener {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button logout, messages;
    TextView profile_name, profile_email, edit_profile;
    ImageView profileImage, addRequest;
    private RecyclerView recyclerView;
    private ClinicAppointmentListAdapter adapter;
    private ClinicData clinicData;

    private static final int REQUEST_CODE = 101;
    private DrawerLayout drawer = null;
    private ActionBarDrawerToggle toggle = null;
    private NavigationView navigationView;
    DocumentReference documentReference;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<AppointmentData> options;
    String id;
    private DatabaseReference dataRefClinic;
    private DatabaseReference tokenReference;
    List<String> tokens = new ArrayList<>();
    boolean bool;
    Bundle bundle;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bundle = getIntent().getExtras();
        documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

        if (fAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finishAffinity();
        } else if (fAuth.getCurrentUser() != null) {

            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Boolean hasFirstTimeLogin = (boolean) document.get("firstTimeLogin");
                     name = (String) document.get("name");

                    if (hasFirstTimeLogin) {
                        Intent intent = new Intent(getApplicationContext(), UpdateClinicDetails.class);
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        id = (String) document.get("id");

                        {
                            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("clinic");
                            Query query = firebaseDatabase.orderByChild("id").equalTo(id);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot activitySnapShot : snapshot.getChildren()) {
                                            clinicData = activitySnapShot.getValue(ClinicData.class);

                                            if(Objects.equals(clinicData.hasApproved, "deleted")){
                                                Toast.makeText(ClinicActivity.this, "Your account is disabled. Please contact admin", Toast.LENGTH_LONG).show();
                                                logout.callOnClick();
                                            }
                                        }
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }


                        FirebaseMessaging.getInstance().subscribeToTopic(id)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        String msg = "Subscribed";
                                        if (!task.isSuccessful()) {
                                            msg = "Subscribe failed " + id;
                                        }
                                        Log.d("xxx fcm", msg + id);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("xxx fcm", e.toString());
                                    }
                                });

                        changeLogin();
                    }
                }
            });

        }

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openNavDrawer, R.string.closeNavDrawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        logout = navigationView.findViewById(R.id.logout);
        messages = navigationView.findViewById(R.id.messages);
        edit_profile = navigationView.findViewById(R.id.editProfile);
        profileImage = navigationView.findViewById(R.id.profile);
        profile_name = navigationView.findViewById(R.id.profile_name);
        profile_email = navigationView.findViewById(R.id.profile_email);
        addRequest = findViewById(R.id.add);

        loadData();

        edit_profile.setOnClickListener(view -> {
            edit();
        });

        recyclerView = findViewById(R.id.recycler1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);


        if (bundle != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("appointment").child(bundle.getString("id"));
            options = new FirebaseRecyclerOptions.Builder<AppointmentData>()
                    .setQuery(databaseReference.orderByChild("hasAccepted").equalTo("true"), AppointmentData.class)
                    .build();
            adapter = new ClinicAppointmentListAdapter(options, this);
            adapter.setOnShareClickedListener(this);
        }

        recyclerView.setAdapter(adapter);

        logout.setOnClickListener(view -> {
            fAuth.signOut();
            startActivity(new Intent(this, Login.class));
            finish();
        });

        messages.setVisibility(View.VISIBLE);
        messages.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), InboxActivity.class);
            intent.putExtra("clinicName", clinicData.getName());
            intent.putExtra("clinicId", clinicData.getId());
            startActivity(intent);
        });

        addRequest.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AppointmentRequestActivity.class);
            intent.putExtra("id", bundle.getString("id"));
            startActivity(intent);
        });
    }

    private void loadData() {
        String userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            String email = documentSnapshot.getString("email");
            String name = documentSnapshot.getString("name");
            String profile = documentSnapshot.getString("imageUrl");
            Picasso.get().load(profile).centerCrop().fit().into(profileImage);
            profile_name.setText(name);
            profile_email.setText(email);

        });
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }


    public void edit() {
        Bundle args = new Bundle();
        args.putSerializable("data", clinicData);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        EditClinicDetails editClinicDetails = new EditClinicDetails();
        editClinicDetails.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        editClinicDetails.setArguments(args);
        editClinicDetails.show(ft, "editClinicDetails");
    }

    public void changeLogin() {
        dataRefClinic = FirebaseDatabase.getInstance().getReference("clinic").child(id).child("lastLogin");
        DateFormat df = new SimpleDateFormat("EEE , MMMM d yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        dataRefClinic.setValue(date);
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

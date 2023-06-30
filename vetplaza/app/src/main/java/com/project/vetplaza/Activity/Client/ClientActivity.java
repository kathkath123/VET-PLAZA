package com.project.vetplaza.Activity.Client;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.project.vetplaza.Activity.ClinicDetailsFragment;
import com.project.vetplaza.Auth.Login;
import com.project.vetplaza.Data.ClientData;
import com.project.vetplaza.Data.ClinicData;
import com.project.vetplaza.Nav.EditClientDetails;
import com.project.vetplaza.Nav.UpdateClientDetails;
import com.project.vetplaza.R;
import com.squareup.picasso.Picasso;


public class ClientActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ClientListAdapter.OnShareClickedListener {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button logout,appointment;
    TextView profile_name, profile_email,edit_profile;
    ImageView profileImage;
    String id, name, profile;

    private static final int REQUEST_CODE = 101;
    private DrawerLayout drawer = null;
    private ActionBarDrawerToggle toggle = null;
    private NavigationView navigationView;
    DocumentReference documentReference;
    private RecyclerView recyclerView;
    private ClientListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

        if(fAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finishAffinity();
        } else {
            if (fAuth.getCurrentUser() != null) {
                documentReference.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        id = (String) document.get("id");
                        Boolean hasFirstTimeLogin = (boolean) document.get("firstTimeLogin");

                        if(hasFirstTimeLogin){
                            Intent intent = new Intent(getApplicationContext(), UpdateClientDetails.class);
                            startActivity(intent);
                            finishAffinity();
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

                    }
                });

            }
        }

        drawer =  findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openNavDrawer, R.string.closeNavDrawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        logout = navigationView.findViewById(R.id.logout);
        appointment = navigationView.findViewById(R.id.appointment);
        edit_profile = navigationView.findViewById(R.id.editProfile);
        profileImage = navigationView.findViewById(R.id.profile);
        profile_name = navigationView.findViewById(R.id.profile_name);
        profile_email = navigationView.findViewById(R.id.profile_email);
        loadData();

        logout.setOnClickListener(view -> {
            fAuth.signOut();
            startActivity(new Intent(this, Login.class));
            finish();
        });

        edit_profile.setOnClickListener(view -> {
            edit();
        });


        appointment.setVisibility(View.VISIBLE);
        appointment.setOnClickListener(view -> {
            FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
            Bundle args = new Bundle();
            args.putString("id", id);
            ClientAppointmentFragment clientAppointmentFragment = new ClientAppointmentFragment();
            clientAppointmentFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
            clientAppointmentFragment.setArguments(args);
            clientAppointmentFragment.show(ft, "appointmentFragment");
        });

        recyclerView = findViewById(R.id.recycler1);
        LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(this,  LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<ClinicData> options =
                new FirebaseRecyclerOptions.Builder<ClinicData>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("clinic").orderByChild("hasApproved").equalTo("true"), ClinicData.class)
                        .build();

        adapter = new ClientListAdapter(options, this);
        adapter.setOnShareClickedListener(this);
        recyclerView.setAdapter(adapter);
    }


    private void loadData() {
        String userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            String email = documentSnapshot.getString("email");
            name = documentSnapshot.getString("name");
            profile = documentSnapshot.getString("imageUrl");
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
        } if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public void edit() {
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("client");
        Query query = firebaseDatabase.orderByChild("id").equalTo(id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot activitySnapShot : snapshot.getChildren()) {
                        ClientData receivedData = activitySnapShot.getValue(ClientData.class);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        EditClientDetails editClientDetails = new EditClientDetails();
                        Bundle args = new Bundle();
                        args.putSerializable("data", receivedData);
                        editClientDetails.setArguments(args);
                        editClientDetails.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
                        editClientDetails.show(ft, "EditClientDetails");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        args.putString("id", id);
        args.putString("name", name);
        args.putString("message", "true");
        args.putString("imageUri", profile);
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ClinicDetailsFragment clinicDetails = new ClinicDetailsFragment();
        clinicDetails.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        clinicDetails.setArguments(args);
        clinicDetails.show(ft, "profile");
    }
}

package com.project.vetplaza.Activity.Admin;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.vetplaza.Activity.ClinicDetailsFragment;
import com.project.vetplaza.Auth.AdminRegister;
import com.project.vetplaza.Auth.Login;
import com.project.vetplaza.Data.ClinicData;
import com.project.vetplaza.R;

public class AdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdminClinicListAdapter.OnShareClickedListener{

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button logout,mAdminBtn;
    TextView profile_name, profile_email;
    ImageView profileImage;
    ImageView addRequest;
    TextView edit_profile;
    String id;

    private DrawerLayout drawer = null;
    private ActionBarDrawerToggle toggle = null;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private AdminClinicListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (fAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finishAffinity();
        }

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openNavDrawer, R.string.closeNavDrawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        mAdminBtn = navigationView.findViewById(R.id.add_admin);
        logout = navigationView.findViewById(R.id.logout);
        profileImage = navigationView.findViewById(R.id.profile);
        profile_name = navigationView.findViewById(R.id.profile_name);
        profile_email = navigationView.findViewById(R.id.profile_email);
        edit_profile = navigationView.findViewById(R.id.editProfile);

        edit_profile.setVisibility(View.INVISIBLE);

        mAdminBtn.setVisibility(View.VISIBLE);
        mAdminBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), AdminRegister.class));
        });

        addRequest = findViewById(R.id.add);

        loadData();

        logout.setOnClickListener(view -> {
            fAuth.signOut();
            startActivity(new Intent(this, Login.class));
            finish();
        });

        recyclerView = findViewById(R.id.recycler1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<ClinicData> options =
                new FirebaseRecyclerOptions.Builder<ClinicData>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("clinic").orderByChild("hasApproved").equalTo("true"), ClinicData.class)
                        .build();

        adapter = new AdminClinicListAdapter(options, this);
        adapter.setOnShareClickedListener(this);
        recyclerView.setAdapter(adapter);

        addRequest.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AdminRequestActivity.class);
            startActivity(intent);
        });

    }


    private void loadData() {
        String userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            String email = documentSnapshot.getString("email");
            String name = documentSnapshot.getString("name");
            id = documentSnapshot.getString("id");
//                Picasso.get().load(profile).centerCrop().fit().into(profileImage);
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
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
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
        args.putString("id", "admin_approved");
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ClinicDetailsFragment clinicDetails = new ClinicDetailsFragment();
        clinicDetails.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        clinicDetails.setArguments(args);
        clinicDetails.show(ft, "profile");
    }
}

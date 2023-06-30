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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.vetplaza.Activity.Client.ClientDetailsFragment;
import com.project.vetplaza.Auth.Login;
import com.project.vetplaza.Data.AppointmentData;
import com.project.vetplaza.Data.ClientData;
import com.project.vetplaza.Data.MessageData;
import com.project.vetplaza.Messaging.FriendlyMessage;
import com.project.vetplaza.Messaging.MainActivity;
import com.project.vetplaza.R;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;


public class InboxActivity extends AppCompatActivity implements InboxListAdapter.OnShareClickedListener {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private RecyclerView recyclerView;
    private InboxListAdapter adapter;

    FirebaseRecyclerOptions<MessageData> options;
    String id;
    private DatabaseReference dataRefClient;
    private DatabaseReference dataRefClinic;
    String clinicName,clinicId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.layout_inbox_message);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        clinicId = bundle.getString("clinicId");
        clinicName = bundle.getString("clinicName");
        dataRefClinic = FirebaseDatabase.getInstance().getReference().child("messagesData");
;
        if (fAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finishAffinity();
        }


        recyclerView = findViewById(R.id.recycler1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        options = new FirebaseRecyclerOptions.Builder<MessageData>()
                .setQuery(dataRefClinic.orderByChild("clinicId").equalTo(clinicId), MessageData.class)
                .build();
        adapter = new InboxListAdapter(options, this);
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
    public void onClicked(MessageData data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("clinicId",  data.clinicId);
        intent.putExtra("clientId",data.getId());
        intent.putExtra("name",clinicName);
        intent.putExtra("origin", "clinic");
        startActivity(intent);
    }
}

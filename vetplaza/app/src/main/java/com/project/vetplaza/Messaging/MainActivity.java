package com.project.vetplaza.Messaging;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.project.vetplaza.Activity.Clinic.ClinicAppointmentListAdapter;
import com.project.vetplaza.Data.AppointmentData;
import com.project.vetplaza.Data.MessageData;
import com.project.vetplaza.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    FirebaseRecyclerOptions<FriendlyMessage> options;
    
    private RecyclerView mMessageListView;
    private NewMessageAdapter messageListAdapter;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private Button mSendButton;

    private String mUsername;
    //Firebase Variables
    private FirebaseDatabase mFireDb;
    private DatabaseReference mMessageDbReference, mMessageInfoDbReference;
    String clientId, clinicId, imageUri, origin;

    List<FriendlyMessage> friendlyMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        
        mFireDb = FirebaseDatabase.getInstance();

        Bundle bundle = getIntent().getExtras();
        mUsername = bundle.getString("name");
        imageUri = bundle.getString("imageUri");
         clientId = bundle.getString("clientId");
         clinicId = bundle.getString("clinicId");
        origin = bundle.getString("origin");
        mMessageDbReference = mFireDb.getReference().child("messages").child(clinicId).child(clientId);
        mMessageInfoDbReference = mFireDb.getReference().child("messagesData");
        mProgressBar = findViewById(R.id.progressBar);
        mMessageListView = findViewById(R.id.recycler1);
        mMessageEditText = findViewById(R.id.messageEditText);
        mSendButton = findViewById(R.id.sendButton);

        // Initialize message ListView and its adapter
        friendlyMessages = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMessageListView.setLayoutManager(linearLayoutManager);

        options = new FirebaseRecyclerOptions.Builder<FriendlyMessage>()
                .setQuery(mMessageDbReference, FriendlyMessage.class)
                .build();
        messageListAdapter = new NewMessageAdapter(options,this,  mUsername);
        mMessageListView.setAdapter(messageListAdapter);
        messageListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                mMessageListView.scrollToPosition(messageListAdapter.getItemCount() - 1);
            }
        });


//        attachDatabaseReadListener();

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click
//                String id = databaseReference.push().getKey();

                @SuppressLint("SimpleDateFormat") DateFormat time = new SimpleDateFormat("HH:mm");
                @SuppressLint("SimpleDateFormat") DateFormat date = new SimpleDateFormat("MMMM dd");
                String getTime = time.format(Calendar.getInstance().getTime());
                String getDate = date.format(Calendar.getInstance().getTime());
                FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(), mUsername, getTime, getDate );
                mMessageDbReference.push().setValue(friendlyMessage);

                if(origin.equals("client")){
                    MessageData messageData = new MessageData(clientId, imageUri,  mUsername, clinicId);
                    mMessageInfoDbReference.child(clinicId+clientId).setValue(messageData);
                }
                // Clear input box
                mMessageEditText.setText(null);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        messageListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        messageListAdapter.stopListening();
    }



}


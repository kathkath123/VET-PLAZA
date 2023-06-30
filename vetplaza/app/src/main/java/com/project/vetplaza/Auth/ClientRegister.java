package com.project.vetplaza.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.vetplaza.Activity.Client.ClientActivity;
import com.project.vetplaza.Nav.UpdateClientDetails;
import com.project.vetplaza.Nav.UpdateClinicDetails;
import com.project.vetplaza.R;

import java.util.HashMap;
import java.util.Map;

public class ClientRegister extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mFullName, mEmail, mPassword;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_signup);

        mFullName = findViewById(R.id.fullName);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mLoginBtn = findViewById(R.id.createText);
        mEmail = findViewById(R.id.Email1);
        mPassword = findViewById(R.id.password);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);


        mRegisterBtn.setOnClickListener(v -> register());

        mLoginBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Login.class)));

    }

    private void register() {
        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();
        final String fullName = mFullName.getText().toString();

        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Email is Required.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Password is Required.");
            return;
        }

        if (password.length() < 6) {
            mPassword.setError("Password Must be >= 6 Characters");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // register the user in firebase
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ClientRegister.this, "User Created.", Toast.LENGTH_SHORT).show();
                userID = fAuth.getCurrentUser().getUid();
                DocumentReference documentReference = fStore.collection("users").document(userID);
                Map<String, Object> user = new HashMap<>();
                user.put("name", fullName);
                user.put("email", email);
                user.put("password", password);
                user.put("user", "client");
                user.put("firstTimeLogin", true);
                user.put("id", "");
                documentReference.set(user).addOnSuccessListener(aVoid ->
                        {
                            Intent intent = new Intent(getApplicationContext(), UpdateClientDetails.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                ).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e));
            } else {
                Toast.makeText(ClientRegister.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}
package com.project.vetplaza.Auth;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.vetplaza.Activity.Admin.AdminActivity;
import com.project.vetplaza.Activity.Client.ClientActivity;
import com.project.vetplaza.Activity.Clinic.ClinicActivity;
import com.project.vetplaza.R;

public class Login extends AppCompatActivity {
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION"};
    EditText mEmail,mPassword;
    Button mLoginBtn;
    TextView mCreateBtn;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.loginBtn);
        mCreateBtn = findViewById(R.id.createText);
        fStore = FirebaseFirestore.getInstance();


        mLoginBtn.setOnClickListener( v -> {
            String email = mEmail.getText().toString();
            String password = mPassword.getText().toString();
            if(TextUtils.isEmpty(email)){
                mEmail.setError("Email is Required.");
                return;
            }

            if(TextUtils.isEmpty(password)){
                mPassword.setError("Password is Required.");
                return;
            }

            if(password.length() < 6){
                mPassword.setError("Password Must be >= 6 Characters");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            // authenticate the user

            fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    String userID = fAuth.getCurrentUser().getUid();
                    Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @SuppressLint("CheckResult")
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String user = documentSnapshot.getString("user");
                            String id = (String) documentSnapshot.getString("id");
                            assert user != null;
                            if(user.equals("admin")){
                                startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                                finishAffinity();
                            } else if(user.equals("client")){
                                Intent intent = new Intent(getApplicationContext(), ClientActivity.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                                finishAffinity();
                            } else if(user.equals("hospital")){
                                Intent intent = new Intent(getApplicationContext(), ClinicActivity.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                                finishAffinity();
                            }
                        }
                    });
                }else {
                    Toast.makeText(Login.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }


            });

        });


        mCreateBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ChooseRegister.class)));

        if(allPermissionsGranted()){
            location();
        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, 101);
        }
    }


    public boolean isLocationEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager lm = (LocationManager) this.getBaseContext().getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
            int mode = Settings.Secure.getInt(this.getBaseContext().getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return  (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }

    public void location(){
        if(!isLocationEnabled()){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("GPS Location is Disabled, Turn on GPS?");
            builder1.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 516);
                    dialog.dismiss();
                }
            });
            builder1.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(Login.this, "Please Open your GPS",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            AlertDialog dialog1 = builder1.create();
            dialog1.show();
        }

    }

    private boolean allPermissionsGranted(){
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(Login.this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

}
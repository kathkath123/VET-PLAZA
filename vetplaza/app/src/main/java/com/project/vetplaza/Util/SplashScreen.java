package com.project.vetplaza.Util;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.vetplaza.Activity.Admin.AdminActivity;
import com.project.vetplaza.Activity.Client.ClientActivity;
import com.project.vetplaza.Activity.Clinic.ClinicActivity;
import com.project.vetplaza.Auth.Login;
import com.project.vetplaza.Messaging.MainActivity;
import com.project.vetplaza.R;


public class SplashScreen extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(fAuth.getCurrentUser() != null) {
                        String userID = fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fStore.collection("users").document(userID);
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @SuppressLint("CheckResult")
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String user = documentSnapshot.getString("user");
                                String id = (String) documentSnapshot.getString("id");

                                if(user != null){
                                    if(user.equals("admin")){
                                        startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                                        finishAffinity();
                                    } else if(user.equals("client")){
                                        startActivity(new Intent(getApplicationContext(), ClientActivity.class));
                                        finishAffinity();
                                    } else if(user.equals("hospital")){
                                        Intent intent = new Intent(getApplicationContext(), ClinicActivity.class);
                                        intent.putExtra("id", id);
                                        startActivity(intent);
                                        finishAffinity();
                                    }
                                }
                            }
                        });

                    } else {
                        Intent intent = new Intent(SplashScreen.this, Login.class);
                        SplashScreen.this.startActivity(intent);
                        SplashScreen.this.finish();
                    }
                }
            }, 2000);
    }

}
package com.project.vetplaza.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.project.vetplaza.R;

public class ChooseRegister extends AppCompatActivity {
    Button mHospitalBtn, mClientBtn,mAdminBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_signup);

        mHospitalBtn = findViewById(R.id.hospitalBtn);
        mClientBtn = findViewById(R.id.clientBtn);
        mAdminBtn = findViewById(R.id.adminBtn);


        mHospitalBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), HospitalRegister.class));
        });

        mClientBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ClientRegister.class));
        });
    }

}
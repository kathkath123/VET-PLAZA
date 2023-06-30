package com.project.vetplaza.Activity.Client;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.vetplaza.Activity.Admin.AdminClinicListAdapter;
import com.project.vetplaza.Activity.ClinicDetailsFragment;
import com.project.vetplaza.Data.AppointmentData;
import com.project.vetplaza.Data.ClinicData;
import com.project.vetplaza.R;


public class ClientAppointmentFragment extends DialogFragment implements ClientAppointmentListAdapter.OnShareClickedListener {
    private RecyclerView recyclerView;
    private ClientAppointmentListAdapter adapter;

    public ClientAppointmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_appointment, container, false);


        recyclerView = view.findViewById(R.id.recycler1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        String id = getArguments().getString("id");
        FirebaseRecyclerOptions<AppointmentData> options =
                new FirebaseRecyclerOptions.Builder<AppointmentData>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("appointment").child(id).orderByChild("hasAccepted").equalTo("true"), AppointmentData.class)
                        .build();

        adapter = new ClientAppointmentListAdapter(options, requireActivity());
        recyclerView.setAdapter(adapter);
        adapter.setOnShareClickedListener(this);


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onClicked(AppointmentData data) {
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("clinic");
        Query query = firebaseDatabase.orderByChild("id").equalTo(data.clientId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot activitySnapShot: snapshot.getChildren()){
                        ClinicData receivedData = activitySnapShot.getValue(ClinicData.class);
                        Bundle args = new Bundle();
                        args.putSerializable("data", receivedData);
                        args.putString("id","appointment");
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ClinicDetailsFragment clinicDetails = new ClinicDetailsFragment();
                        clinicDetails.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
                        clinicDetails.setArguments(args);
                        clinicDetails.show(ft, "profile");
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

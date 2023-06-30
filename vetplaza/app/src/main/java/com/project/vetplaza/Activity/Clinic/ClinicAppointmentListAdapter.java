package com.project.vetplaza.Activity.Clinic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.project.vetplaza.Data.AppointmentData;
import com.project.vetplaza.R;

public class ClinicAppointmentListAdapter extends FirebaseRecyclerAdapter<AppointmentData, ClinicAppointmentListAdapter.PastViewHolder>  {
    private OnShareClickedListener mCallback;
    private Context mContext;
    private  ViewGroup parent;

    public ClinicAppointmentListAdapter(@NonNull FirebaseRecyclerOptions<AppointmentData> options, Context context) {
        super(options);
        mContext = context;
    }


    @Override
    protected void onBindViewHolder(@NonNull PastViewHolder holder, int i, @NonNull final AppointmentData data) {
        int item = getItemCount();
        holder.time.setText(data.getTime());
        holder.date.setText(data.getDate());
        holder.cardView.setOnClickListener(v-> {
            mCallback.onClicked(data);
        });
    }

    @NonNull
    @Override
    public PastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_appointment_clinic, parent, false);
        this.parent = parent;
        return new PastViewHolder(view);
    }


    class PastViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView name, date, time;
        ImageView imgurl;


        public PastViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            imgurl = itemView.findViewById(R.id.imageShow);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    public void setOnShareClickedListener(OnShareClickedListener mCallback) {
        this.mCallback = mCallback;
    }

    public interface OnShareClickedListener {
        void onClicked(AppointmentData data);
    }

}
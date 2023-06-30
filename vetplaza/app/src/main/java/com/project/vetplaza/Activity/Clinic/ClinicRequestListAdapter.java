package com.project.vetplaza.Activity.Clinic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.project.vetplaza.Data.AppointmentData;
import com.project.vetplaza.R;

public class ClinicRequestListAdapter extends FirebaseRecyclerAdapter<AppointmentData, ClinicRequestListAdapter.PastViewHolder>  {
    private OnShareClickedListener mCallback;
    private OnViewClickedListener mViewback;
    private Context mContext;
    private  ViewGroup parent;

    public ClinicRequestListAdapter(@NonNull FirebaseRecyclerOptions<AppointmentData> options, Context context) {
        super(options);
        mContext = context;
    }


    @Override
    protected void onBindViewHolder(@NonNull PastViewHolder holder, int i, @NonNull final AppointmentData data) {
        int item = getItemCount();
        holder.time.setText(data.getTime());
        holder.date.setText(data.getDate());
        holder.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onClicked(data, true);
            }
        });
        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onClicked(data, false);
            }
        });

        holder.cardView.setOnClickListener(
                task1 -> {
                    mViewback.onClicked(data);
                }
        );
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
        TextView name, date, time;
        ImageView imgurl;
        RelativeLayout layout;
        Button approve, decline;
        CardView cardView;


        public PastViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            imgurl = itemView.findViewById(R.id.imageShow);
            layout = itemView.findViewById(R.id.request_layout);
            cardView = itemView.findViewById(R.id.cardView);

            layout.setVisibility(View.VISIBLE);

            approve = itemView.findViewById(R.id.accept);
            decline = itemView.findViewById(R.id.decline);

        }
    }

    public void setOnShareClickedListener(OnShareClickedListener mCallback) {
        this.mCallback = mCallback;
    }

    public interface OnShareClickedListener {
        void onClicked(AppointmentData data, Boolean hasApprove);
    }

    public void setOnViewClickedListener(OnViewClickedListener mViewback) {
        this.mViewback = mViewback;
    }

    public interface OnViewClickedListener {
        void onClicked(AppointmentData data);
    }
}
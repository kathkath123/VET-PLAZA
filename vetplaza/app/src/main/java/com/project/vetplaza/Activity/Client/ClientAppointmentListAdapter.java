package com.project.vetplaza.Activity.Client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.project.vetplaza.Data.AppointmentData;
import com.project.vetplaza.R;

public class ClientAppointmentListAdapter extends FirebaseRecyclerAdapter<AppointmentData, ClientAppointmentListAdapter.PastViewHolder>  {
    private OnShareClickedListener mCallback;
    private Context mContext;
    private  ViewGroup parent;

    public ClientAppointmentListAdapter(@NonNull FirebaseRecyclerOptions<AppointmentData> options, Context context) {
        super(options);
        mContext = context;
    }


    @Override
    protected void onBindViewHolder(@NonNull PastViewHolder holder, int i, @NonNull final AppointmentData data) {
        int item = getItemCount();
        holder.name.setText(data.getClient());
        holder.time.setText(data.getTime());
        holder.date.setText(data.getDate());
        Glide.with(mContext).load(data.getImageUri()).into(holder.imgurl);
        holder.cardview.setOnClickListener(v -> {
            mCallback.onClicked(data);
                }
        );
    }

    @NonNull
    @Override
    public PastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_appointment, parent, false);
        this.parent = parent;
        return new PastViewHolder(view);
    }


    class PastViewHolder extends RecyclerView.ViewHolder{

        TextView name, date, time;
        ImageView imgurl;
        CardView cardview;


        public PastViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            imgurl = itemView.findViewById(R.id.imageShow);
            cardview = itemView.findViewById(R.id.cardView);
        }
    }

    public void setOnShareClickedListener(OnShareClickedListener mCallback) {
        this.mCallback = mCallback;
    }

    public interface OnShareClickedListener {
        void onClicked(AppointmentData data);
    }

}
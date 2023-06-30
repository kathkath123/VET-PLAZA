package com.project.vetplaza.Activity.Admin;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.project.vetplaza.Activity.Clinic.ClinicAppointmentListAdapter;
import com.project.vetplaza.Data.AppointmentData;
import com.project.vetplaza.Data.ClinicData;
import com.project.vetplaza.R;

public class AdminClinicListAdapter extends FirebaseRecyclerAdapter<ClinicData, AdminClinicListAdapter.PastViewHolder> {
    private OnShareClickedListener mCallback;
    private Context mContext;
    private  ViewGroup parent;

    public AdminClinicListAdapter(@NonNull FirebaseRecyclerOptions<ClinicData> options, Context context) {
        super(options);
        mContext = context;
    }


    @Override
    protected void onBindViewHolder(@NonNull PastViewHolder holder, int i, @NonNull final ClinicData data) {
        int item = getItemCount();
        holder.address.setText(data.getAddress());
        holder.name.setText(data.getName().toUpperCase());
        holder.time.setText(data.getTime());
        holder.phone.setText(data.getPhone());
        Glide.with(mContext).load(data.getImageUri()).into(holder.imgurl);
//        holder.imgurl.setOnClickListener(v -> idCode(data));
        holder.cardView.setOnClickListener(view -> {
            mCallback.onClicked(data);
        });
    }

    @NonNull
    @Override
    public PastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_clinic, parent, false);
        this.parent = parent;
        return new PastViewHolder(view);
    }

    class PastViewHolder extends RecyclerView.ViewHolder{

        TextView address,name, time, phone;
        ImageView imgurl;
        CardView cardView;


        public PastViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            imgurl = itemView.findViewById(R.id.imageShow);
            phone = itemView.findViewById(R.id.phone);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }

//    public void idCode (ClinicData data) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.layout_image_slider, parent, false);
//        ImageView imgViewer = view.findViewById(R.id.imgViewer);
//        Glide.with(view).load(data.getImageUri()).into(imgViewer);
//
//        final Dialog dialog = new Dialog(mContext);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
////        dialog.setTitle("Set your Value");
//        dialog.setContentView(view);
//        dialog.show();
//
//        imgViewer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//    }

    public void setOnShareClickedListener(OnShareClickedListener mCallback) {
        this.mCallback = mCallback;
    }

    public interface OnShareClickedListener {
        void onClicked(ClinicData data);
    }
}
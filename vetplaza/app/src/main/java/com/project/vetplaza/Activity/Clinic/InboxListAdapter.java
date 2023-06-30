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

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.project.vetplaza.Data.AppointmentData;
import com.project.vetplaza.Data.MessageData;
import com.project.vetplaza.R;

public class InboxListAdapter extends FirebaseRecyclerAdapter<MessageData, InboxListAdapter.PastViewHolder>  {
    private OnShareClickedListener mCallback;
    private Context mContext;
    private  ViewGroup parent;

    public InboxListAdapter(@NonNull FirebaseRecyclerOptions<MessageData> options, Context context) {
        super(options);
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull PastViewHolder holder, int i, @NonNull final MessageData data) {
        holder.name.setText(data.getName());
        Glide.with(mContext).load(data.getImageUri()).into(holder.imgurl);
        holder.cardView.setOnClickListener(v-> {
            mCallback.onClicked(data);
        });
    }

    @NonNull
    @Override
    public PastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_message_list, parent, false);
        this.parent = parent;
        return new PastViewHolder(view);
    }


    class PastViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView name;
        ImageView imgurl;


        public PastViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            imgurl = itemView.findViewById(R.id.profile);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    public void setOnShareClickedListener(OnShareClickedListener mCallback) {
        this.mCallback = mCallback;
    }

    public interface OnShareClickedListener {
        void onClicked(MessageData data);
    }

}
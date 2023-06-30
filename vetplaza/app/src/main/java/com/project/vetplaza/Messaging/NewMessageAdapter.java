package com.project.vetplaza.Messaging;

import android.content.Context;
import android.text.TextUtils;
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

import java.util.Objects;

public class NewMessageAdapter extends FirebaseRecyclerAdapter<FriendlyMessage, RecyclerView.ViewHolder>  {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    Context mcontext;
    String user;
    String same;

    public NewMessageAdapter(FirebaseRecyclerOptions<FriendlyMessage> options, Context context, String mUsername) {
        super(options);
        mcontext = context;
        user = mUsername;
    }

    @Override
    public int getItemViewType(int position) {
        FriendlyMessage message = getItem(position);

        if (TextUtils.equals(message.getName(),user)){
            if(position != 0){
                if(Objects.equals(message.getName(), getItem(position - 1).getName())){
                    same = "true";
                } else {
                    same = "false";
                }
            }
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
        // If some other user sent the message
        // Return VIEW_TYPE_MESSAGE_RECEIVED;
            if(position != 0) {
                if (Objects.equals(message.getName(), getItem(position - 1).getName())) {
                    same = "true";
                } else {
                    same = "false";
                }
            }
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }


    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int position, @NonNull FriendlyMessage model) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                if(Objects.equals(same, "true")){
                    ((NewMessageAdapter.SentMessageHolder) holder).bind(model, "true");
                } else {
                    ((NewMessageAdapter.SentMessageHolder) holder).bind(model);
                }

                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                if(Objects.equals(same, "true")){
                    ((NewMessageAdapter.ReceivedMessageHolder) holder).bind(model,"true");
                } else {
                    ((NewMessageAdapter.ReceivedMessageHolder) holder).bind(model);
                }
                break;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_MESSAGE_SENT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_sent, parent, false);
                return new SentMessageHolder(view);
            case VIEW_TYPE_MESSAGE_RECEIVED:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_received, parent, false);
                return new ReceivedMessageHolder(view);
        }
        return null;
    }


    private static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, dateText, nameText, timestamp;
        ImageView profileImage;

        ReceivedMessageHolder( View itemView) {
            super(itemView);

            messageText =  itemView.findViewById(R.id.text_gchat_message_other);
            dateText = (TextView) itemView.findViewById(R.id.text_gchat_date_other);
            nameText =itemView.findViewById(R.id.text_gchat_user_other);
            timestamp = itemView.findViewById(R.id.timestamp);
//            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        }

        void bind(FriendlyMessage message) {
            messageText.setText(message.getText());

            // Format the stored timestamp into a readable String using method.
//            dateText.setText(Utils.formatDateTime(message.getCreatedAt()));

            nameText.setText(message.getName());
            dateText.setVisibility(View.VISIBLE);
            dateText.setText(message.getDate());
            timestamp.setText(message.getTime());
            // Insert the profile image from the URL into the ImageView.
//            Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
        }

        public void bind(FriendlyMessage model, String atrue) {
            messageText.setText(model.getText());
            dateText.setVisibility(View.GONE);
            timestamp.setText(model.getTime());
            nameText.setVisibility(View.GONE);
        }
    }

    private static class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, dateText, timestamp;

        SentMessageHolder( View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.messageTextView);
            dateText = itemView.findViewById(R.id.text_gchat_date_me);
            timestamp = itemView.findViewById(R.id.timestamp);
        }

        void bind(FriendlyMessage message) {
            messageText.setText(message.getText());
            dateText.setVisibility(View.VISIBLE);

            // Format the stored timestamp into a readable String using method.
            dateText.setText(message.getDate());
            timestamp.setText(message.getTime());
        }

        public void bind(FriendlyMessage model, String atrue) {
            messageText.setText(model.getText());
            dateText.setVisibility(View.GONE);
            timestamp.setText(model.getTime());
        }
    }
}
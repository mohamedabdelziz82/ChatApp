package com.example.mohamedabdelazizhamad.whatsapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mohamedabdelazizhamad.whatsapp.Model.ChatMessagesModel;
import com.example.mohamedabdelazizhamad.whatsapp.Model.GroupMessagesModel;
import com.example.mohamedabdelazizhamad.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesAdapter.viewHolder> {
    private Context context;
    private ArrayList<ChatMessagesModel> messagesList;
    private FirebaseAuth mAuth;
    private String senderMessageID;

    class viewHolder extends RecyclerView.ViewHolder {
        TextView SendtD, SendtM,  SendtT;
        TextView rectD, rectM, rectT;
        CardView sendCardView, recCardView;

        public viewHolder(View v) {
            super(v);
            rectM = v.findViewById(R.id.one_chat_message_display);
            rectD = v.findViewById(R.id.one_chat_date_display);
            rectT = v.findViewById(R.id.one_chat_time_display);
            recCardView = v.findViewById(R.id.one_chat_card);

            SendtM = v.findViewById(R.id.one_sender_chat_message_display);
            SendtD = v.findViewById(R.id.one_sender_chat_date_display);
            SendtT = v.findViewById(R.id.one_sender_chat_time_display);
            sendCardView = v.findViewById(R.id.one_sender_chat_card);

        }

    }

    public ChatMessagesAdapter(Context context, ArrayList<ChatMessagesModel> messagesList) {
        this.context = context;
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public ChatMessagesAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.chat_message_row, viewGroup, false);
        mAuth = FirebaseAuth.getInstance();
        return new viewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ChatMessagesAdapter.viewHolder viewHolder, int i) {
        senderMessageID = mAuth.getCurrentUser().getUid();
        ChatMessagesModel m = messagesList.get(i);
        String fromUserID = m.getFrom();
        String typeUserMessage=m.getType();
if (typeUserMessage.equals("text")) {
    if (fromUserID.equals(senderMessageID)) {
        viewHolder.recCardView.setVisibility(View.INVISIBLE);
        viewHolder.sendCardView.setVisibility(View.VISIBLE);

        viewHolder.sendCardView.setCardBackgroundColor(R.drawable.sender_messages_layout);
        viewHolder.SendtD.setText(m.getDate());
        viewHolder.SendtM.setText(m.getMessage());
        viewHolder.SendtT.setText(m.getTime());
    } else {
        viewHolder.sendCardView.setVisibility(View.INVISIBLE);
        viewHolder.recCardView.setVisibility(View.VISIBLE);
        viewHolder.recCardView.setCardBackgroundColor(R.drawable.receiver_messages_layout);
        viewHolder.rectD.setText(m.getDate());
        viewHolder.rectM.setText(m.getMessage());
        viewHolder.rectT.setText(m.getTime());
    }
//        viewHolder.tD.setText(m.getDate());
//        viewHolder.tM.setText(m.getMessage());
//        viewHolder.tN.setText(m.getName());
//        viewHolder.tT.setText(m.getTime());
}

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}

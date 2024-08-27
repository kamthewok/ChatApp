package com.example.secchatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class MessagesAdapter extends RecyclerView.Adapter  {

    Context context;
    ArrayList<Messages> messagesArrayList;

    int ITEM_SEND = 1;
    int ITEM_RECIVE = 2;

    public MessagesAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.senderchatlayout, parent, false);
            return new SenderViewHolder(view);
        }
        else{

            View view = LayoutInflater.from(context).inflate(R.layout.reciverchatlayout, parent, false);
            return new ReciverViewHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        Messages messages = messagesArrayList.get(position);

        if(holder.getClass() == SenderViewHolder.class){
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            viewHolder.textViewmessage.setText(messages.getMessage());
            viewHolder.messagetime.setText(messages.getCurrenttime());
        }
        else {
            ReciverViewHolder viewHolder = (ReciverViewHolder) holder;
            viewHolder.textViewmessage.setText(messages.getMessage());
            viewHolder.messagetime.setText(messages.getCurrenttime());
        }



    }

    @Override
    public int getItemViewType(int position) {

        Messages messages = messagesArrayList.get(position);

        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderid())){

            return ITEM_SEND;
        }
        else {
            return ITEM_RECIVE;
        }

    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    class SenderViewHolder extends RecyclerView.ViewHolder{

        TextView textViewmessage;
        TextView messagetime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewmessage = itemView.findViewById(R.id.sendermessage);
            messagetime = itemView.findViewById(R.id.messagetime);
        }
    }

    class ReciverViewHolder extends RecyclerView.ViewHolder{

        TextView textViewmessage;
        TextView messagetime;

        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewmessage = itemView.findViewById(R.id.sendermessage);
            messagetime = itemView.findViewById(R.id.messagetime);
        }
    }

}

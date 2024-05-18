package com.example.aichatbot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View conversation = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation, null);
        MyViewHolder myViewHolder = new MyViewHolder(conversation);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messages.get(position);
        if (message.getSend_by().equals(Message.sent_by_user)) {
            holder.respond.setVisibility(View.GONE);
            holder.answer.setVisibility(View.VISIBLE);
            holder.t_answer.setText(message.getMessage());
        } else {
            holder.answer.setVisibility(View.GONE);
            holder.respond.setVisibility(View.VISIBLE);
            holder.t_respond.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout answer, respond;
        TextView t_answer, t_respond;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            answer = itemView.findViewById(R.id.lo_answer);
            respond = itemView.findViewById(R.id.lo_respond);
            t_answer = itemView.findViewById(R.id.tv_answer);
            t_respond = itemView.findViewById(R.id.tv_respond);
        }
    }
}

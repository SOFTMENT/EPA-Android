package com.application.epa.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.application.epa.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import com.application.epa.ChatScreenActivity;
import com.application.epa.Models.LastMessageModel;
import com.application.epa.Utils.Services;

public class ChatHomeAdapter extends RecyclerView.Adapter<ChatHomeAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<LastMessageModel> lastMessageModels;
    public ChatHomeAdapter(Context context, ArrayList<LastMessageModel> lastMessageModels) {
        this.context = context;
        this.lastMessageModels = lastMessageModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_home_layout_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            LastMessageModel lastMessageModel = lastMessageModels.get(position);
           Glide.with(context).load(lastMessageModel.getSenderImage()).placeholder(R.drawable.man).into(holder.profile_image);
           holder.username.setText(lastMessageModel.getSenderName());
           holder.message.setText(lastMessageModel.getMessage());
           holder.time.setText(Services.convertDateToTimeString(lastMessageModel.getDate()));

        Log.d("ISREAD",""+lastMessageModel.isRead());
           if (lastMessageModel.isRead()) {

               holder.chatRR.setBackground(ContextCompat.getDrawable(context,R.drawable.transparent_back));
           }
           else {
               Log.d("ISREAD","OH NO");
               holder.chatRR.setBackground(ContextCompat.getDrawable(context,R.drawable.red_back));
           }
           holder.view.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(context, ChatScreenActivity.class);
                   intent.putExtra("sellerId",lastMessageModel.getSenderUid());
                   intent.putExtra("sellerName",lastMessageModel.getSenderName());
                   intent.putExtra("sellerImage",lastMessageModel.getSenderImage());
                   intent.putExtra("sellerToken",lastMessageModel.getSenderToken());
                   context.startActivity(intent);
               }
           });
    }

  @Override
    public int getItemCount() {

        return lastMessageModels.size();
    }

   static public class ViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView profile_image;
        private TextView username, message, time;
        private View view;
        private RelativeLayout chatRR;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            chatRR = view.findViewById(R.id.chat_rr);
            profile_image = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.username);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
        }
    }

    public void filter(String text) {
        ArrayList<LastMessageModel> newlastMessageModels = new ArrayList<>();

        for (LastMessageModel lastMessageModel : LastMessageModel.lastMessageModels) {

            if (lastMessageModel.getSenderName().toLowerCase().contains(text.toLowerCase())) {
                newlastMessageModels.add(lastMessageModel);
            }
        }
        lastMessageModels.clear();
        lastMessageModels.addAll(newlastMessageModels);
        notifyDataSetChanged();
    }
}

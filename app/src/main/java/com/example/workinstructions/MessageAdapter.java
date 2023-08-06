package com.example.workinstructions;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    List<Message> messageList;
    Context c;
    private OnItemClickListener onItemClickListener;
    public MessageAdapter(Context c, List<Message> messageList) {
        this.messageList = messageList;
        this.c=c;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,null);
        MyViewHolder myViewHolder = new MyViewHolder(chatView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        if(message.getSentBy().equals(Message.SENT_BY_ME)){
            holder.leftChatView.setVisibility(View.GONE);
            holder.rightChatView.setVisibility(View.VISIBLE);
            holder.suggestedByGPT.setVisibility(View.GONE);
            if(message.getImage()!=null){
                holder.uploadImageUser.setImageBitmap(message.getImage());
            }else{
                holder.rightTextView.setText(message.getMessage());
            }
        }else{
            holder.rightChatView.setVisibility(View.GONE);
            holder.leftChatView.setVisibility(View.VISIBLE);
            holder.suggestedByGPT.setVisibility(View.VISIBLE);
            holder.leftTextView.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftChatView,rightChatView;
        TextView leftTextView,rightTextView, suggestedByGPT;
        ImageView uploadImageBot, uploadImageUser;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatView  = itemView.findViewById(R.id.left_chat_view);
            rightChatView = itemView.findViewById(R.id.right_chat_view);
            leftTextView = itemView.findViewById(R.id.left_chat_text_view);
            rightTextView = itemView.findViewById(R.id.right_chat_text_view);
            uploadImageBot = itemView.findViewById(R.id.upload_image_bot);
            uploadImageUser = itemView.findViewById(R.id.upload_image_user);
            suggestedByGPT = itemView.findViewById(R.id.by_api);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(messageList.get(position).getSentBy().equals(Message.SENT_AS_PROMPT)){
                        Message clickedItem = messageList.get(position);
                        onItemClickListener.onItemClick(position, clickedItem);
                    }
                }
            });
        }
    }
    public interface OnItemClickListener {
        void onItemClick(int position, Message item);
    }
}
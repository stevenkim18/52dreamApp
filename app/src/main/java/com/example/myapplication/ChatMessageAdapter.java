package com.example.myapplication;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

//5.
//6.
public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>{

    //7.
    private ArrayList<ChatMessage> chatMessages = null;
    private String userName;

    //1.
    public class ViewHolder extends RecyclerView.ViewHolder{

        //2.
        TextView userName_TextView, message_TextView, date_TextView;
        LinearLayout main_LinearLayout;

        //3.
        ViewHolder(View itemView){
            super(itemView);

            //4.
            userName_TextView = itemView.findViewById(R.id.userName_textView); // 사용자 이름
            message_TextView = itemView.findViewById(R.id.message_textView);   // 메시지
            date_TextView = itemView.findViewById(R.id.date_textView);         // 메세지를 입력한 시간
            main_LinearLayout = itemView.findViewById(R.id.main_LinearLayout); // 3가지를 감싸고 있는 LinearLayout

        }

    }

    //8.
    public ChatMessageAdapter(ArrayList<ChatMessage> chatMessages, String userName) {
        this.chatMessages = chatMessages;
        // 이름을 생성자에서 받아옴.
        // 본인이 쓴 메시지면 오른쪽으로 상대방이면 다른쪽으로 하기 위해서서
        this.userName = userName;
    }

    //9.
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_message_item1, parent, false);
        return new ViewHolder(view);
    }

    //10.
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);

        // 사용자 이름
        holder.userName_TextView.setText(message.getUsername());
        // 메시지 내용
        holder.message_TextView.setText(message.getMessage());
        // 메시지 입력 시간
        holder.date_TextView.setText(message.getDate());

        Log.v("이름", message.getUsername() + " = " + this.userName);

        // 이용자가 본인 이면 오른쪽으로 보이게 함
        if(message.getUsername().equals(this.userName)){
            // LinearLayout 오른쪽으로
            holder.main_LinearLayout.setGravity(Gravity.RIGHT);
            holder.message_TextView.setBackgroundColor(Color.parseColor("#ffa72b"));
        }
        else{
            holder.main_LinearLayout.setGravity(Gravity.LEFT);
            holder.message_TextView.setBackgroundColor(Color.parseColor("#ffffff"));
        }

    }

    //11.
    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    // 채팅 데이터를 추가하기!
    public void addChat(ChatMessage message){
        chatMessages.add(message);

        // 어뎁터를 갱신함.
        // 항상 쓰던 notifyDataSetChanged 를 사용하지 않고
        // ArrayList에 가장 마지막에 있기 때문에 notifyItemInserted 를 사용
        notifyItemInserted(chatMessages.size() - 1);

    }

}

package com.example.myapplication;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChattingActivity extends AppCompatActivity {

    //View
    Toolbar toolbar;
    RecyclerView chatting_recyclerView;
    EditText message_EditText;
    ImageButton send_ImageButton;

    //
    ArrayList<ChatMessage> chatMessages;

    // RecyclerView Adapter
    ChatMessageAdapter messageAdapter;

    //Firebase
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        toolbar = findViewById(R.id.toolbar);
        chatting_recyclerView = findViewById(R.id.chatting_RecyclerView);
        message_EditText = findViewById(R.id.message_editText);
        send_ImageButton = findViewById(R.id.send_imageButton);

        // 툴바 설정
        setSupportActionBar(toolbar);
        // 툴바 왼쪽 버튼 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);

        //Firebase 현재 사용자
        user = FirebaseAuth.getInstance().getCurrentUser();

        //RecyclerView 세팅
        chatMessages = new ArrayList<>();
        messageAdapter = new ChatMessageAdapter(chatMessages, user.getDisplayName());
        chatting_recyclerView.setAdapter(messageAdapter);
        chatting_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = FirebaseDatabase.getInstance();
        // message라는 key가 생기고
        myRef = database.getReference();

        // 화면에 들어왔을 때 RecyclerView가 가장 최신의 대화내용을 보여줌.
        //chatting_recyclerView.scrollToPosition(chatMessages.size() - 1);

        // 보내기 버튼 눌렀을 때 메시지 보내기
        send_ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 메시지창이 비어있으면 보내기가 안됨.
                if(message_EditText.getText().toString().length() == 0 || message_EditText.getText().toString() == null){
                    return;
                }

                ChatMessage message = new ChatMessage();
                message.setUsername(user.getDisplayName());  // 메시지를 쓴 사용자 이름 넣기
                message.setMessage(message_EditText.getText().toString()); // 메시지 넣기

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                message.setDate(sdf.format(date)); // 메시지 보낸 시간 넣기

                //메세지 객체를 파이어베이스로 보내기
                myRef.push().setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    // 메시지가 성공적으로 보내졌을 때
                    public void onComplete(@NonNull Task<Void> task) {
                        // 메시지 입력창이 비워짐.
                        message_EditText.setText("");
                    }
                });


            }
        });
        message_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatting_recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        chatting_recyclerView.scrollToPosition(chatMessages.size() - 1);
                    }
                },500);
            }
        });


        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);

                // 어뎁터에 메시지 넣어주기기
                messageAdapter.addChat(message);

                // 새로운 채팅이 데이터 추가 될 경우 RecyclerView의 스크롤을 최신으로 해주기
                chatting_recyclerView.scrollToPosition(chatMessages.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

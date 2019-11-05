package com.example.myapplication;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MybroadActivity extends AppCompatActivity {

    //게시글을 담을 리스트 생성
    ArrayList<Post> posts = new ArrayList<>();

    Toolbar toolbar;
    RecyclerView recyclerView;
    // 어뎁터
    PostAdapter adapter;

    //파이어베이스 유저
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybroad);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);

        // 툴바 설정
        setSupportActionBar(toolbar);
        // 툴바 왼쪽 버튼 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //리싸이클러뷰 아이템마다 구분선 넣기
        recyclerView.addItemDecoration(new DividerItemDecoration(MybroadActivity.this,1));

        user = FirebaseAuth.getInstance().getCurrentUser();

        adapter = new PostAdapter(posts);

        recyclerView.setAdapter(adapter);

        getPostDataFromSharedPreferences(user);


    }

    private void getPostDataFromSharedPreferences(FirebaseUser user){

        SharedPreferences preferences = getSharedPreferences("posts", MODE_PRIVATE);
        String datesValue = preferences.getString("dates", null);
        Log.v("내가쓴게시물", datesValue);
        String[] dates = datesValue.split("★");

        for(int i = 0; i< dates.length; i++){
            String postValue = preferences.getString(dates[i], null);
            Log.v("내가쓴게시물", postValue);
            String[] values = postValue.split("★");

            Log.v("내가쓴게시물", "접속 유저 uid : " + user.getUid());
            Log.v("내가쓴게시물", "게시물 uid : " + values[5]);
            // 게시물의 uid와 접속한 uid가 같은 경우
            if(user.getUid().equals(values[5])){
                // 사진 있는 경우
                if(values.length == 7){
                    Post post = new Post(values[0], values[1], values[2], values[3], values[4], values[5], values[6]);
                    posts.add(0, post);
                }
                // 사진이 없는 경우
                else {
                    Post post = new Post(values[0], values[1], values[2], values[3], values[4], values[5]);
                    posts.add(0, post);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }
}

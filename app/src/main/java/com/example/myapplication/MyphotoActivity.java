package com.example.myapplication;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MyphotoActivity extends AppCompatActivity {

    ArrayList<PhotoPost> photoPosts = new ArrayList<>();

    Toolbar toolbar;
    RecyclerView recyclerView;

    PhotoPostAdapter adapter;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myphoto);

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);

        // 툴바 설정
        setSupportActionBar(toolbar);
        // 툴바 왼쪽 버튼 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);

        // 리싸이클러뷰 그리드 레이아웃으로 정렬
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        adapter = new PhotoPostAdapter(photoPosts);
        recyclerView.setAdapter(adapter);

        user = FirebaseAuth.getInstance().getCurrentUser();

        getDataFromSharedPreferences(user);

        adapter.setOnImageViewClick(new PhotoPostAdapter.OnImageViewClickListener() {
            @Override
            public void onImageViewClick(View v, int position) {

            }
        });

    }

    // 쉐어드에 내가 올린 사진 데이터만 가지고 오기
    private void getDataFromSharedPreferences(FirebaseUser user){

        SharedPreferences preferences = getSharedPreferences("photoPosts", MODE_PRIVATE);
        String datesValue = preferences.getString("dates", null);

        String[] dates = datesValue.split("★");

        for(int i = 0; i< dates.length; i++){
            String postValue = preferences.getString(dates[i], null);
            Log.v("내가쓴게시물", postValue);
            String[] values = postValue.split("★");

            // 게시물의 uid와 접속한 uid가 같은 경우
            if(user.getUid().equals(values[4])){
                PhotoPost photoPost = new PhotoPost(values[0], values[1], values[2], values[3], values[4]);
                photoPosts.add(photoPost);
            }
        }

        adapter.notifyDataSetChanged();

    }
}

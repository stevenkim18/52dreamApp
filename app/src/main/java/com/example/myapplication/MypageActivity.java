package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MypageActivity extends AppCompatActivity {

    ImageView user_ImageView;
    TextView id_TextView, name_TextView, village_TextView, ageNum_TextView;
    Button myBoard_Button, myPhoto_Button, logout_Button;
    Switch music_Switch, alarm_Switch;
    Toolbar toolbar;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        user_ImageView = findViewById(R.id.user_imageView); // 유저 이미지
        id_TextView = findViewById(R.id.id_textView); // 유저 아이디(이메일)
        name_TextView = findViewById(R.id.name_textView); // 유저 이름
        village_TextView = findViewById(R.id.village_textView); // 유저 마을
        ageNum_TextView = findViewById(R.id.ageNum_textView); // 유저 기수
        myBoard_Button = findViewById(R.id.myBroad_button); // 내가 쓴 게시물 버튼
        myPhoto_Button = findViewById(R.id.myPhoto_button); // 내가 올린 사진 버튼
        music_Switch = findViewById(R.id.music_switch);     // 베경음악 설정
        alarm_Switch = findViewById(R.id.alarm_switch);     // 알람 설정
        logout_Button = findViewById(R.id.logout_button);   // 로그아웃 버튼
        toolbar = findViewById(R.id.toolbar);

        user = FirebaseAuth.getInstance().getCurrentUser();

        Profile profile = getProfileDataFromSharedPreferences(user);

        setProfileDataToViews(profile);

        // 툴바 설정
        setSupportActionBar(toolbar);
        // 툴바 왼쪽 버튼 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);

        // 내가 쓴 게시물
        myBoard_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, MybroadActivity.class);
                startActivity(intent);
            }
        });
        // 내가 올린 사진
        myPhoto_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, MyphotoActivity.class);
                startActivity(intent);
            }
        });


        // 배경음악 스위치
        music_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 채크 되어 있을 때
                if(isChecked){
                    // 시작
                    Log.v("배경음악", "체크 : " + isChecked);
                    Intent intent = new Intent(MypageActivity.this, MusicService.class);
                    intent.putExtra("isPlay", true);
                    startService(intent);
                }
                // 채크 안되어 있을 때
                else{
                    // 정지
                    Log.v("배경음악", "체크 : " + isChecked);
                    Intent intent = new Intent(MypageActivity.this, MusicService.class);
                    intent.putExtra("isPlay", false);
                    startService(intent);

                }

            }
        });

        //로그아웃 버튼
        logout_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 파이어베이스 로그 아웃
                FirebaseAuth.getInstance().signOut();

                // 음악 종료
                Intent intent = new Intent(MypageActivity.this, MusicService.class);
                intent.putExtra("isPlay", false);
                startService(intent);

                // 로그인 화면으로 이동
                Intent loginIntent = new Intent(MypageActivity.this, LoginActivity.class);
                Toast.makeText(getApplicationContext(), "로그아웃 완료", Toast.LENGTH_LONG).show();
                startActivity(loginIntent);
                finish();
            }
        });

    }

    //회원정보를 쉐어드에서 가지고 오기
    private Profile getProfileDataFromSharedPreferences(FirebaseUser user){

        SharedPreferences preferences = getSharedPreferences("profiles", MODE_PRIVATE);
        String value = preferences.getString(user.getUid(), null);
        String[] values = value.split("★");

        Profile profile = new Profile(values[0], values[1], values[2], values[3], values[4], values[5], values[6]);

        return profile;
    }

    //회원정보 데이터들을 뷰에게 전달 하기
    private void setProfileDataToViews(Profile profile){
        //이미지
        Glide.with(this).load(Uri.parse(profile.getImageUrl())).apply(new RequestOptions().circleCrop()).into(user_ImageView);
        //아이디
        id_TextView.setText(profile.getEmail());
        //이름
        name_TextView.setText(profile.getName());
        //마을
        village_TextView.setText(profile.getVillage());
        //기수
        ageNum_TextView.setText(profile.getAgeNum());

    }
}

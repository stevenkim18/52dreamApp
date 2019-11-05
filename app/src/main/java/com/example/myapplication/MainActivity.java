package com.example.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //뷰 변수 선언
    Button bulletin_button, introduction_button, people_button, location_button, calendar_button, webpage_button;
    Button board_button, photo_button, bible_button, chatting_button;
    Toolbar toolbar;
    TextView bible_verse_TextView;

    ArrayList<String> bible_verses;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 버튼 선언
        bulletin_button = (Button)findViewById(R.id.bulletin_button);
        introduction_button = (Button)findViewById(R.id.introduction_button);
        people_button = (Button)findViewById(R.id.people_button);
        location_button = (Button)findViewById(R.id.location_button);
        calendar_button = (Button)findViewById(R.id.calender_button);
        webpage_button = (Button)findViewById(R.id.webpage_button);
        board_button = (Button)findViewById(R.id.board_button);
        photo_button = (Button)findViewById(R.id.photo_button);
        bible_button = (Button)findViewById(R.id.bible_button);
        chatting_button = (Button)findViewById(R.id.chatting_button);
        toolbar = findViewById(R.id.toolbar);
        bible_verse_TextView = findViewById(R.id.bibleVerse_textView);

        //랜덤으로 나올 말씀 구절들 리스트에 저장
        bible_verses = new ArrayList<>();
        bible_verses.add("태초에 하나님이 천지를 창조 하시니라(창1:1)");
        bible_verses.add("여호와는 나의 목자시니 내게 부족함이 없으리로다(시23:!)");
        bible_verses.add("수고하고 무거운 짐 진 자들아 다 내게로 오라 내가 너희를 쉬게 하리라(마11:28)");
        bible_verses.add("하나님이 세상을 이처럼 사랑하사 독생자를 주셨으니 이는 그를 믿는 자마다 멸망하지 않고 영생을 얻게 하려 하심이라(요3:16)");

        // 툴바 설정
        setSupportActionBar(toolbar);

        //파이어베이스에 담은 로그인 된 회원정보 보기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.v("로그인 된 유저", user.getEmail());
        Log.v("로그인 된 유저", user.getUid());

        // 배경음악 재생
        playBackgroundMusic();

        //1. 주보 버튼
        //프로필 정보를 넘겨서 admin일때만 수정 가능
        //나머지는 수정 불가
        bulletin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BulletinActivity.class);
                startActivity(intent);
            }
        });

        //2. 교회소개 버튼
        introduction_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChurchIntroductionActivity.class);
                startActivity(intent);
            }
        });

        //3. 리더소개 버튼
        people_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LeadersIntroductionActivity.class);
                startActivity(intent);
            }
        });


        //4. 교회 위치 버튼
        location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChurchLocationActivity.class);
                startActivity(intent);
            }
        });


        //5. 청년부 일정 버튼
        calendar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });


        //6. CCM듣기 버튼
        webpage_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, YoutubeSearchActivity.class);
                startActivity(intent);
            }
        });

        //7. 게시판 버튼
        board_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoticeBoardActivity.class);
                startActivity(intent);
            }
        });

        //8. 사진첩 버튼
        photo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PhotoGalleryActivity.class);
                startActivity(intent);
            }
        });

        //9. 오늘의 말씀 버튼
        // 댓글을 남길 때 회원 이름이 필요함.
        bible_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BibleActivity.class);
                startActivity(intent);
            }
        });

        //10. 채팅 버튼
        chatting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChattingActivity.class);
                startActivity(intent);
            }
        });


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                String verse = (String)msg.obj;

                bible_verse_TextView.setText(verse);
            }
        };

        class BibleVerseThread implements Runnable {

            Handler mHandler = handler;
            @Override
            public void run() {

                while (true){

                    Message message = handler.obtainMessage();

                    Random random = new Random();

                    message.obj = bible_verses.get(random.nextInt(bible_verses.size()));

                    try{
                        Thread.sleep(3000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    handler.sendMessage(message);
                }

            }

        }

        BibleVerseThread bibieVerseThread = new BibleVerseThread();
        Thread thread = new Thread(bibieVerseThread);
        thread.start();
    }


    //툴바 메뉴 만들기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main_menu, menu);

        return true;
    }

    //툴바 메뉴 설정

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 로그인한 회원정보 보기
            case R.id.action_profile:
                Intent intent = new Intent(MainActivity.this, MypageActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    // 배경음악 재생 서비스 실행
    private void playBackgroundMusic(){
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("isPlay", true);
        startService(intent);
    }

}


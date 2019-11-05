package com.example.myapplication;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class BulletinActivity extends AppCompatActivity {

    //뷰 변수 선언
    TextView date_textView, line3_textView, line4_textView, right2_textView, right3_textView, right4_textView, right9_textView;
    Toolbar toolbar;

    //프로필 정보를 담을 객체 생성
    Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin);

        Log.v("주보","onCreate");
        //뷰 객체 생성
        date_textView = (TextView)findViewById(R.id.date_textView); //날짜
        line3_textView = (TextView)findViewById(R.id.line3_textView); // 성경 구절
        line4_textView= (TextView)findViewById(R.id.line4_textView); // 말씀 제목
        right2_textView = (TextView)findViewById(R.id.right2_textView); // 대표기도
        right3_textView = (TextView)findViewById(R.id.right3_textView); // 봉독자
        right4_textView = (TextView)findViewById(R.id.right4_textView); // 설교자
        right9_textView = (TextView)findViewById(R.id.right9_textView); // 축도자
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        // 툴바 설정
        setSupportActionBar(toolbar);
        // 툴바 왼쪽 버튼 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);

        //보내준 인텐트 받기
        Intent intent = getIntent();
        profile = (Profile)intent.getSerializableExtra("profile");


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == 52){
            if(resultCode == RESULT_OK){

                //TextView에 받아 온 값 넣기
                date_textView.setText(intent.getStringExtra("date"));
                line3_textView.setText(intent.getStringExtra("line3"));
                line4_textView.setText(intent.getStringExtra("line4"));
                right2_textView.setText(intent.getStringExtra("right2"));
                right3_textView.setText(intent.getStringExtra("right3"));
                right4_textView.setText(intent.getStringExtra("right4"));
                right9_textView.setText(intent.getStringExtra("right9"));

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("주보","onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("주보","onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("주보","onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("주보","onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("주보","onDestroy");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("주보","onRestart");

    }

    //툴바 메뉴 만들기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_buttetin_menu, menu);

        return true;
    }

    //툴바 메뉴 설정

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 뒤로가기 버튼
            case android.R.id.home:
                Toast.makeText(getApplicationContext(),"뒤로가기 클릭",Toast.LENGTH_SHORT).show();
                return true;
            // 수정 버튼
            case R.id.action_edit:
                Toast.makeText(getApplicationContext(),"수정 클릭",Toast.LENGTH_SHORT).show();
                Intent editIntent = new Intent(BulletinActivity.this, BulletinEditActivity.class);
                //수정할 수 있는 값들 넘겨줌
                editIntent.putExtra("date",date_textView.getText().toString());
                editIntent.putExtra("line3", line3_textView.getText().toString());
                editIntent.putExtra("line4", line4_textView.getText().toString());
                editIntent.putExtra("right2", right2_textView.getText().toString());
                editIntent.putExtra("right3", right3_textView.getText().toString());
                editIntent.putExtra("right4", right4_textView.getText().toString());
                editIntent.putExtra("right9", right9_textView.getText().toString());
                startActivityForResult(editIntent, 52);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}

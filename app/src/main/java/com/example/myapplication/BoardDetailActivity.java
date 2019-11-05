package com.example.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BoardDetailActivity extends AppCompatActivity {

    // 게시글 데이터를 담은 객체변수 선언
    Post post;

    //뷰 객체 선언
    TextView category_TextView, title_TextView, content_TextView, name_TextView, date_TextView;
    ImageView imageView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);

        // 뷰 생성
        category_TextView = findViewById(R.id.category_textView);
        title_TextView = findViewById(R.id.title_textView);
        content_TextView = findViewById(R.id.content_textView);
        name_TextView = findViewById(R.id.id_textView);
        date_TextView = findViewById(R.id.date_textView);
        imageView = findViewById(R.id.post_imageView);
        toolbar = findViewById(R.id.toolbar);

        // 툴바 설정
        setSupportActionBar(toolbar);
        // 툴바 왼쪽 버튼 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);

        //인텐트로 게시물 객체 받기
        Intent intent = getIntent();

        //인텐트에서 객체 받기
        Post post = (Post)intent.getSerializableExtra("selectedPost");

        //카테고리
        category_TextView.setText(post.getCategory());
        //제목
        title_TextView.setText(post.getTitle());
        //내용
        content_TextView.setText(post.getContent());
        //글쓴이
        name_TextView.setText(post.getName());
        // 날짜
        date_TextView.setText(post.getDate());
        //사진
        //사진이 있는 경우
        if(intent.getBooleanExtra("isPhoto", false)){
            imageView.setImageURI(Uri.parse(post.getPhotoUri()));
            Log.v("사진", "상세 게시물 보기 사진 있음");
        }
        else{
            imageView.setVisibility(View.GONE);
            Log.v("사진", "상세 게시물 보기 사진 없음");
        }

    }
    //툴바 메뉴 만들기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_broad_detail_menu, menu);

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

                return true;
            // 삭제 버튼
            case R.id.action_delete:
                Toast.makeText(getApplicationContext(),"삭제 클릭",Toast.LENGTH_SHORT).show();

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}

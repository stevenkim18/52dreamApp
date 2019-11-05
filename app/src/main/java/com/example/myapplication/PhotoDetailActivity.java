package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoDetailActivity extends AppCompatActivity {

    // 뷰 객체 선언
    TextView title_TextView, date_TextView, name_TextView;
    ImageView imageView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        // 뷰 객체 생성
        title_TextView = findViewById(R.id.title_textView);
        name_TextView = findViewById(R.id.name_textView);
        date_TextView = findViewById(R.id.date_textView);
        imageView = findViewById(R.id.post_imageView);
        toolbar = findViewById(R.id.toolbar);

        // 툴바 설정
        setSupportActionBar(toolbar);
        // 툴바 왼쪽 버튼 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);

        PhotoPost photoPost = (PhotoPost) getIntent().getSerializableExtra("selectedPhotoPost");

        putDataOnVIewsFromIntent(photoPost);

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

    // 데이터를 뷰에 뿌려주기
    private void putDataOnVIewsFromIntent(PhotoPost photoPost){

        //사진
        imageView.setImageURI(Uri.parse(photoPost.getPhotoUri()));
        //제목
        title_TextView.setText(photoPost.getTitle());
        //이름
        name_TextView.setText(photoPost.getName());
        //날짜
        date_TextView.setText(photoPost.getDate());

    }
}

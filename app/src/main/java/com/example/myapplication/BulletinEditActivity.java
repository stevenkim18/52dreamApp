package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BulletinEditActivity extends AppCompatActivity {

    //뷰 변수 선언
    EditText date_editText, line3_editText, line4_editText, right2_editText, right3_editText, right4_editText, right9_editText;
    Button complete_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin_edit);

        // 뷰 객체 생성
        date_editText = (EditText)findViewById(R.id.date_editText);
        line3_editText = (EditText)findViewById(R.id.line3_editText);
        line4_editText = (EditText)findViewById(R.id.line4_editText);
        right2_editText = (EditText)findViewById(R.id.right2_editText);
        right3_editText = (EditText)findViewById(R.id.right3_editText);
        right4_editText = (EditText)findViewById(R.id.right4_editText);
        right9_editText = (EditText)findViewById(R.id.right9_editText);
        complete_button = (Button)findViewById(R.id.complete_button);

        //가져온 값들 editText에 넣기
        date_editText.setText(getIntent().getStringExtra("date"));
        line3_editText.setText(getIntent().getStringExtra("line3"));
        line4_editText.setText(getIntent().getStringExtra("line4"));
        right2_editText.setText(getIntent().getStringExtra("right2"));
        right3_editText.setText(getIntent().getStringExtra("right3"));
        right4_editText.setText(getIntent().getStringExtra("right4"));
        right9_editText.setText(getIntent().getStringExtra("right9"));

        // 완료 버튼 클릭
        complete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                //수정된 값 보내기
                intent.putExtra("date", date_editText.getText().toString());
                intent.putExtra("line3", line3_editText.getText().toString());
                intent.putExtra("line4", line4_editText.getText().toString());
                intent.putExtra("right2", right2_editText.getText().toString());
                intent.putExtra("right3", right3_editText.getText().toString());
                intent.putExtra("right4", right4_editText.getText().toString());
                intent.putExtra("right9", right9_editText.getText().toString());
                setResult(RESULT_OK, intent);
                finish();

            }
        });

    }
}

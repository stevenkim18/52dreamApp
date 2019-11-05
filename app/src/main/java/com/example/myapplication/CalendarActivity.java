package com.example.myapplication;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Hashtable;

public class CalendarActivity extends AppCompatActivity {

    //뷰 생성
    CalendarView calendarView;
    TextView date_textView, schedule_textView;
    Button add_button;

    // 날짜 저장할 변수
    String date;

    // 데이터 저장할 Hashtable 생성
    Hashtable<String, String> hashtable = new Hashtable<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = (CalendarView)findViewById(R.id.calendarView);
        date_textView = (TextView)findViewById(R.id.date_textView);
        add_button = (Button)findViewById(R.id.add_button);
        schedule_textView = (TextView)findViewById(R.id.schedule_textView);

        // 캘린더 뷰에서 날짜를 클릭 했을 때
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                //텍스트 뷰 화면에 날짜 찍히게 하기
                date = String.format("%s%s%s", Integer.toString(year), Integer.toString(month + 1), Integer.toString(dayOfMonth));
                date_textView.setText(year+"년 "+ (month + 1) + "월 " + dayOfMonth + "일" );
                schedule_textView.setText(hashtable.get(date));
            }
        });

        //추가 버튼 클릭 했을 때
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(CalendarActivity.this);

                // 다이얼로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this);
                // 다이얼로그 제목
                builder.setTitle("일정 추가");
                // 다이얼로그 내용
                builder.setMessage(date);
                // 다이얼로그 뷰 설정 --> EditText로
                builder.setView(editText);

                //저장 클릭시
                builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"저장", Toast.LENGTH_LONG).show();
                        hashtable.put(date, editText.getText().toString());
                    }
                });

                //취소 클릭시
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"취소", Toast.LENGTH_LONG).show();
                    }
                });

                builder.show();

            }
        });
    }
}

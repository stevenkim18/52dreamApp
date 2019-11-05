package com.example.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class BibleActivity extends AppCompatActivity {

    //뷰 변수 선언
    Toolbar toolbar;
    RecyclerView recyclerView, bible_verse_RecyclerView;
    TextView bibleVerse_TextView, date_TextView, title_TextView;

    //댓글들을 담을 리스트 변수
    ArrayList<Comment> comments = new ArrayList<>();
    ArrayList<BibleVerse> bibleVerses = new ArrayList<>();

    //댓글 어뎁터 변수 선언
    CommentAdapter adapter;
    BibleVerseAdapter bibleVerseAdapter;

    //파이어베이스 유저
    FirebaseUser user;

    //선택된 날짜
    String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bible);

        //뷰 생성
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        bible_verse_RecyclerView = findViewById(R.id.bibleVerse_RecyclerView);
        bibleVerse_TextView = findViewById(R.id.bibleVerse_textView);
        date_TextView = findViewById(R.id.date_textView);
        title_TextView = findViewById(R.id.title_textView);

        user = FirebaseAuth.getInstance().getCurrentUser();

        // addItems();
        // recyclerView 설정
        // 댓글 recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //어뎁터 생성
        adapter = new CommentAdapter(comments);
        //리싸이클러뷰에 어뎁터 붙여주기
        recyclerView.setAdapter(adapter);

        //리싸이클러뷰 아이템마다 구분선 넣기
        recyclerView.addItemDecoration(new DividerItemDecoration(BibleActivity.this,1));

        // 성경 구절 recyclerView
        bible_verse_RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 어뎁터 생성
        bibleVerseAdapter = new BibleVerseAdapter(bibleVerses);
        // recyclerView에 BibleVerseAdapter 붙여 주기
        bible_verse_RecyclerView.setAdapter(bibleVerseAdapter);

        final SharedPreferences preferences = getSharedPreferences("bibleVerses", MODE_PRIVATE);
        // 오늘 날짜 가져오기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String todayDate = sdf.format(date);
        selectedDate = todayDate;
        getCommentDataFromSharedPreferences(selectedDate);

        // 키 값에 해당 날짜가 없으면 크롤링해서 가지고 오기
        if(!preferences.contains(todayDate)) {
            getBibleVerseByCrwaling();
        }
        else{
            // 크롤링한 값을 쉐어드에서 가지고 와서 뿌려주기
            getBibleVerseFromSharedPreference(todayDate);
        }


        // 툴바 설정
        setSupportActionBar(toolbar);
        // 툴바 왼쪽 버튼 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);


        //메뉴 이미지 버튼 눌렀을 때
        adapter.setOnImageButtonClickListener(new CommentAdapter.onImageButtonClickListener() {
            @Override
            public void onImageButtonClick(View v, final int position) {
                Toast.makeText(getApplicationContext(), "메뉴이미지버튼" + position, Toast.LENGTH_SHORT).show();

                // 팝업 메뉴 객체 생성
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                // 팝업 객체와 아이템.xml를 붙여줌
                getMenuInflater().inflate(R.menu.image_button_menu, popupMenu.getMenu());
                // 팝업한 버튼을 눌렀을 때.
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            //수정 버튼
                            case R.id.edit_button:
                                // 다이얼로그 창에 넣을 EditText 만들기
                                final EditText editText = new EditText(BibleActivity.this);

                                // 수정하기 전의 내용을 EditText에 올려둠.
                                editText.setText(comments.get(position).getContents());

                                // 다이얼로그 생성
                                AlertDialog.Builder builder = new AlertDialog.Builder(BibleActivity.this, R.style.AlertDialog);
                                // 다이얼로그 제목
                                builder.setTitle("댓글 수정");
                                // 다이얼로그 뷰
                                builder.setView(editText);
                                // 묵상 남기기 버튼
                                builder.setPositiveButton("수정완료", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //댓글
                                        String comment = editText.getText().toString();

                                        //새로 반영된 댓글을 댓글 리스트에 넣기
                                        comments.get(position).setContents(comment);

                                        //쉐어드에도 수정 된 값 저장
                                        SharedPreferences preferences1 =  getSharedPreferences("comments", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences1.edit();

                                        String name = comments.get(position).getName();
                                        String content = comments.get(position).getContents();
                                        String date = comments.get(position).getDate();
                                        String uid = comments.get(position).getUid();

                                        String commentValue = name + "★" + content + "★" + date + "★" + uid;

                                        editor.putString(comments.get(position).getDate(), commentValue);
                                        editor.apply();

                                        // 어뎁터 새로고침
                                        adapter.notifyDataSetChanged();

                                    }
                                });
                                // 취소 버튼
                                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                // 다이얼로그 보이기
                                builder.show();


                                break;
                            //삭제 버튼
                            case R.id.delete_button:
                                Toast.makeText(getApplicationContext(),"삭제" + position, Toast.LENGTH_SHORT).show();

                                //쉐어드에서 지우기
                                SharedPreferences preferences1 = getSharedPreferences("comments", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences1.edit();

                                //해당날짜+dates에서 먼저 날짜 지우기
                                String date =  preferences1.getString(selectedDate + "dates", null);
                                String[] dates = date.split("★");

                                ArrayList<String> dateList = new ArrayList<>();

                                for(int i = 0; i < dates.length; i++){
                                    // 삭제하려고 하는 게시물 날짜와 같으면 null값으로 함
                                    if(comments.get(position).getDate().equals(dates[i])){
                                        dates[i] = null;
                                    }
                                    // 아니면 날짜를 만든 리스트에 넣음.
                                    else{
                                        dateList.add(dates[i]);
                                    }
                                }

                                // 다시 새로 넣음 dates 값을 합치기 위해 StringBuffer 생성
                                StringBuffer stringBuffer = new StringBuffer(dateList.get(0));

                                // dateList 사이즈 만큼 돌면서 stringBuffer에 날짜 추가
                                for(int i = 1; i < dateList.size(); i++){
                                    stringBuffer.append("★" + dateList.get(i));
                                }

                                date = stringBuffer.toString();

                                // 삭제가 완료된 날짜들 vaule를 dates key값에 넣음
                                editor.putString(selectedDate+"dates", date);

                                editor.remove(comments.get(position).getDate());
                                editor.apply();

                                // 댓글 객체를 리스트에서 삭제
                                comments.remove(position);

                                //어뎁터 새로고침
                                adapter.notifyDataSetChanged();

                                break;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

    }

    //툴바 메뉴 만들기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    //툴바 메뉴 설정
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 날짜 변경
            case R.id.action_calendar:
                Toast.makeText(getApplicationContext(), "날짜 변경", Toast.LENGTH_SHORT).show();

                // 현재 날짜를 받는 객체
                final Calendar calendar = Calendar.getInstance();

                // 날짜 선택 다이얼로그 생성
                // 2번째 매게변수는 스타일 지정해줌.
                DatePickerDialog dialog = new DatePickerDialog(BibleActivity.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {

                    // 날짜를 선택 했을 때 이벤트 발생
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        // '월'이 한 자리수이면 앞에 0을 붙여줌
                        if(month + 1 < 10){
                            // '일'이 한 자리수이면 앞에 0을 붙여줌
                            if(dayOfMonth < 10){
                                selectedDate = String.format(("%d0%d0%d"), year, month + 1, dayOfMonth);
                            }
                            // '일'이 두 자리수이면 그래도
                            else{
                                selectedDate = String.format(("%d0%d%d"), year, month + 1, dayOfMonth);
                            }
                        }
                        // 월이 두자리수면 그대로
                        else{
                            // '일'이 한 자리수이면 앞에 0을 붙여줌
                            if(dayOfMonth < 10){
                                selectedDate = String.format(("%d%d0%d"), year, month + 1, dayOfMonth);
                            }
                            // '일'이 두 자리수이면 그래도
                            else{
                                selectedDate = String.format(("%d%d%d"), year, month + 1, dayOfMonth);
                            }
                        }

                        SharedPreferences preferences = getSharedPreferences("bibleVerses",MODE_PRIVATE);
                        if(preferences.contains(selectedDate)){
                            getBibleVerseFromSharedPreference(selectedDate);
                            getCommentDataFromSharedPreferences(selectedDate);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "해당 날짜에는 데이터가 없습니다", Toast.LENGTH_SHORT).show();
                        }

                    }
                    //오늘 날짜가 처음 선택 되게 함(매개변수 5개)
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));


                // 입력한 날짜 이후로 클릭 안되게 옵션
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                // 달력 다이얼로그가 화면에 뜸.
                dialog.show();

                return true;
            // 묵상 댓글 달기
            case R.id.action_comment:
                Toast.makeText(getApplicationContext(),"묵상 댓글", Toast.LENGTH_SHORT).show();

                // 다이얼로그 창에 넣을 EditText 만들기
                final EditText editText = new EditText(BibleActivity.this);

                // 다이얼로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(BibleActivity.this, R.style.AlertDialog);
                // 다이얼로그 제목
                builder.setTitle("한줄 묵상");
                // 다이얼로그 내용
                builder.setMessage("말씀을 읽고 묵상을 남겨주세요,");
                // 다이얼로그 뷰
                builder.setView(editText);
                // 묵상 남기기 버튼
                builder.setPositiveButton("묵상 남기기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"묵상 남기기", Toast.LENGTH_SHORT).show();

                        //이름
                        String name = user.getDisplayName();
                        //댓글
                        String comment = editText.getText().toString();
                        //날짜
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        long time = System.currentTimeMillis();// 시간
                        Date date = new Date(time);   // 날짜
                        String currentTime = simpleDateFormat.format(date);
                        //유저 uid
                        String uid = user.getUid();

                        Comment newComment = new Comment(name, comment, currentTime, uid);

                        // 쉐어드에 저장
                        putCommentDataInSharedPreferences(selectedDate, newComment);

                        // 새로운 댓글이 가장 맨 위로 생성되어서 리스트에 저장 됨.
                        comments.add(0, newComment);

                        // 어뎁터 새로고침
                        adapter.notifyDataSetChanged();

                    }
                });
                // 취소 버튼
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"취소", Toast.LENGTH_SHORT).show();
                    }
                });
                // 다이얼로그 보이기
                builder.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    //성경구절 불러오기
    private void getBibleVerseByCrwaling(){

        // 크롤링 AsyncTask
        class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

            String date, title, location;
            ArrayList<BibleVerse> bibleVerses = new ArrayList<>();

            // 실제로 할일
            @Override
            protected Void doInBackground(Void... voids) {

                try{
                    // 크롤링 할 웹사이트를 입력
                    Document document = Jsoup.connect("https://sum.su.or.kr:8888/bible/today").get();
                    // HTML 태그를 가지고 옴.
                    Log.v("크롤링", "html 문서 = " + document.toString());

                    // 날짜
                    Element date = document.getElementById("dailybible_info");
                    this.date = date.text();
                    Log.v("크롤링", "날짜 : " + this.date);

                    //제목
                    Element title = document.getElementById("bible_text");
                    this.title = title.text();
                    Log.v("크롤링", "제목 : " + this.title);

                    //본문 위치
                    Element location = document.getElementById("bibleinfo_box");
                    this.location = location.text();
                    Log.v("크롤링", "본문위치 : " + this.location);

                    // 성경 구절 숫자
                    Elements num = document.getElementsByClass("num");
                    // 성경 구절 말씀
                    Elements verse = document.getElementsByClass("info");

                    for(int i = 0; i < num.size(); i++){
                        //성경 구절 클래스 생성
                        BibleVerse bibleVerse = new BibleVerse();
                        // 성경 구절 숫자를 객체에 넣어주기
                        bibleVerse.setNumber(num.get(i).text());
                        Log.v("크롤링", "숫자 : " + bibleVerse.getNumber());
                        // 성경 구절 본문을 객체에 넣어주기
                        bibleVerse.setBible_verse(verse.get(i).text());
                        Log.v("크롤링", "말씀 : " + bibleVerse.getBible_verse());

                        // 전역변수로 선언한 ArrayList에 추가 하기
                        this.bibleVerses.add(bibleVerse);
                    }

                }catch (IOException e){
                    e.printStackTrace();
                }
                //SharedPreferences에 생성
                SharedPreferences preferences = getSharedPreferences("bibleVerses", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                //오늘 날짜 저장 ex) 2019-07-16
                //쉐어드의 key 값으로 사용
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String todayDate = sdf.format(date);

                //value 저장
                //날짜★제목★위치★숫자●성경구절■숫자●성경구절■숫자●성경구절■숫자●성경구절 ...으로 저장
                StringBuffer value = new StringBuffer("");

                value.append(this.date +"★"); //날짜 저장
                value.append(this.title +"★"); // 제목 저장
                value.append(this.location +"★"); // 위치 저장

                for(int i = 0; i < this.bibleVerses.size(); i++){

                    value.append(bibleVerses.get(i).getNumber() + "●"); // 숫자 저장

                    // 성경 구절 저장.
                    // 마지막은 네모 안넣음.
                    if(i == this.bibleVerses.size() -1){
                        value.append(bibleVerses.get(i).getBible_verse());
                    }
                    else{
                        value.append(bibleVerses.get(i).getBible_verse() + "■");
                    }

                }

                //쉐어드에 저장
                editor.putString(todayDate, value.toString());
                Log.v("쉐어드", "key : " + todayDate);
                Log.v("쉐어드", "value : " + value.toString());
                editor.apply();

                return null;
            }

            // 시작하기 전
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //TODO 프로그레스바 넣기

            //끝나고 난뒤
            @Override
            protected void onPostExecute(Void aVoid) {

            }

        }

        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
    }

    //쉐어드에서 값 가지고 오기
    private void getBibleVerseFromSharedPreference(String selectedDate){

        SharedPreferences preferences = getSharedPreferences("bibleVerses", MODE_PRIVATE);

        String value = preferences.getString(selectedDate, null);
        String[] values = value.split("★");

        String day = values[0];
        Log.v("날짜 변경", "날짜 : " + day);
        String title = values[1];
        Log.v("날짜 변경", "제목 : " + title);
        String verse = values[2];
        Log.v("날짜 변경", "구절 : " + verse);
        String bibleVerse = values[3];
        Log.v("날짜 변경", "본문 : " + bibleVerse);

        String[] bibleVersesTemp = bibleVerse.split("■");

        // 성경 구절 담는 리스트 비워줌!
        bible_verse_RecyclerView.getRecycledViewPool().clear();
        bibleVerses.clear();

        for (int i = 0; i < bibleVersesTemp.length; i++) {
            String[] verses = bibleVersesTemp[i].split("●");

            // 성경구절을 담을 객체 생성
            BibleVerse bibleVerse1 = new BibleVerse();

            // 숫자
            bibleVerse1.setNumber(verses[0]);

            // 내용
            bibleVerse1.setBible_verse(verses[1]);

            bibleVerses.add(bibleVerse1);

            Log.v("날짜 변경", "성경구절 리스트 크기 : " + bibleVerses.size());

        }

        //크롤링해서 받아오는 성경구절 중에 "본문 : " 글씨를 빼기 위해서 substring 사용
        bibleVerse_TextView.setText(verse.substring(5));
        //크롤링해서 받아오는 날짜 중에 "매일성경" 글씨를 빼기 위해서 substring 사용
        date_TextView.setText(day.substring(5));

        title_TextView.setText(title);

        // 댓글 어뎁터를 계속 갱신해서 에러 뜸...
        // 데이터 갱신
        bibleVerseAdapter.notifyDataSetChanged();

    }

    // 댓글을 데이터를 SharedPreferences에 넣기
    private void putCommentDataInSharedPreferences(String selectedDate, Comment comment){
        SharedPreferences preferences = getSharedPreferences("comments", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        //key: 190724dates value: 2019-07-24 13:00:02★2019-07-24 15:23:21...
        //key: 2019-07-24 13:00:02 value:김승우★댓글내용★날짜★uid

        String name = comment.getName();  // 댓글 쓴 유저 이름
        String content = comment.getContents(); // 댓글 내용
        String date = comment.getDate();       // 댓글 쓴 시각
        String uid = comment.getUid();         // 댓글 쓴 유저의 고유번호

        //해당 날짜에 key가 없을 경우
        if(!preferences.contains(selectedDate+"dates")){
            editor.putString(selectedDate+"dates", date);
        }
        //key가 있을 경우
        else{
            String values = preferences.getString(selectedDate+"dates", null);

            StringBuffer stringBuffer = new StringBuffer(values);

            stringBuffer.append("★"+date);

            editor.putString(selectedDate+"dates", stringBuffer.toString());
        }

        //댓글 정보 쉐어드에 넣기
        String commentValue = name + "★" + content + "★" + date + "★" + uid;
        editor.putString(date, commentValue);

        //쉐어드에 적용
        editor.apply();

    }

    //쉐어드에서 댓글 데이터 가지고 오기
    private void getCommentDataFromSharedPreferences(String selectedDate){
        // 댓글 리스트 비우기
        comments.clear();

        SharedPreferences preferences = getSharedPreferences("comments", MODE_PRIVATE);
        String valueDate = preferences.getString(selectedDate+"dates", null);

        // 해당 날짜의 키가 있을 때
        if(valueDate != null){
            String[] keyDates = valueDate.split("★");

            for(int i = 0; i < keyDates.length; i++){
                String[] values = preferences.getString(keyDates[i],null).split("★");

                Comment comment = new Comment(values[0], values[1], values[2], values[3]);

                comments.add(comment);
            }
        }

        adapter.notifyDataSetChanged();

    }

}

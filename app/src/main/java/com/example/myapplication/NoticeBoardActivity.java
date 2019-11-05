package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class NoticeBoardActivity extends AppCompatActivity {

    //게시글을 담을 리스트 생성
    ArrayList<Post> posts = new ArrayList<>();

    //뷰 변수 선언
    Toolbar toolbar;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;

    // 어뎁터
    PostAdapter adapter;

    // 파이어베이스 유저 정보를 담는 객체
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);

        Log.v("게시판", "onCreate");
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        // 툴바 설정
        setSupportActionBar(toolbar);
        // 툴바 왼쪽 버튼 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);

        //파어어베이스 로그인 된 사용자 정보 가지고 오기
        user = FirebaseAuth.getInstance().getCurrentUser();

        Log.v("아이디", user.getUid());
        Log.v("아이디", user.getEmail());
        Log.v("아이디", user.getDisplayName());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //리싸이클러뷰 아이템마다 구분선 넣기
        recyclerView.addItemDecoration(new DividerItemDecoration(NoticeBoardActivity.this,1));

        adapter = new PostAdapter(posts);

        getPostbyCategory("전체");

        adapter.setPostAdapter(posts);

//        //쉐어드 프리퍼런스에 저장된 게시물 값을 뺴기
//        SharedPreferences preferences = getSharedPreferences("posts", MODE_PRIVATE);
//        // posts.xml 에서 dates 값 가져오기
//        String dates = preferences.getString("dates", null);
//
//        //SharedPreferences가 비었을 때는 작동을 안하도록
//        if(dates != null){
//            // 날짜를 하나씩 쪼개서 배열에 저장
//            String[] date = dates.split("★");
//            // 쪼갠 날짜를 순서대로 key 값에 대입
//            // key 값으로 해당 게시물을 가지고 오면 value 값을 다시 제목, 이름 , 날짜, 내용 순으로 쪼갬
//            // post 객체를 만들어서 posts ArrayList에 저장
//            for(int i = 0; i< date.length; i++){
//                String postValue = preferences.getString(date[i], null);
//                String[] postElement = postValue.split("★");
//
//                Post post;
//
//                // 사진 없는 게시물
//                if(postElement.length == 6){
//                    post = new Post(postElement[0], postElement[1], postElement[2], postElement[3], postElement[4], postElement[5]);
//                    Log.v("사진", "사진 없는 게시물 쉐어드 뻄");
//                    // 최신글로 오게 설정!!
//                    posts.add(0, post);
//                }
//                //사진 있는 게시물
//                else if(postElement.length == 7){
//                    post = new Post(postElement[0], postElement[1], postElement[2], postElement[3], postElement[4], postElement[5], postElement[6]);
//                    Log.v("사진", "사진 있는 게시물 쉐어드 뻄");
//                    // 최신글로 오게 설정!!
//                    posts.add(0, post);
//                }
//
//            }
//        }




        // 아이템 클릭 했을 때
        // 해당 아이템의 게시글 상세보기 페이지로 넘어감.
        adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Intent postIntent = new Intent(NoticeBoardActivity.this, BoardDetailActivity.class);

                // 게시물 객체 인텐트에 담기
                postIntent.putExtra("selectedPost", posts.get(position));

                //사진
                //사진이 있는 경우
                if(posts.get(position).getPhotoUri() != null){
                    postIntent.putExtra("isPhoto", true );
                }
                //사진이 없는 경우
                else{
                    postIntent.putExtra("isPhoto", false );
                }

                startActivity(postIntent);

            }
        });

        // 아이템의 이미지 버튼 클릭 했을 때
        adapter.setOnButtonClickListener(new PostAdapter.OnButtonClickListener() {
            @Override
            public void onButtonClick(View v, final int position) {
                //선택한 게시물의 회원고유번호와 접속한 사용자의 회원고유번호가 같을때
                //게시글을 작성한 사용자만 수정 삭제 가능
                if(posts.get(position).getUid().equals(user.getUid())){
                    // 팝업 메뉴 객체 생성
                    final PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                    // 팝업 객체와 아이템.xml를 붙여줌
                    getMenuInflater().inflate(R.menu.image_button_menu, popupMenu.getMenu());
                    // 팝업한 버튼을 눌렀을 때.
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                //수정 버튼
                                case R.id.edit_button:
                                    Intent postIntent = new Intent(NoticeBoardActivity.this, EditBoardActivity.class);
                                    //수정할 게시물을 인텐트에 담음.
                                    postIntent.putExtra("editedPost", posts.get(position));

                                    //사진
                                    //사진이 있을 때
                                    if(posts.get(position).getPhotoUri() !=null){
                                        postIntent.putExtra("isPhoto", true);
                                    }
                                    //사진 없을 때
                                    else{
                                        postIntent.putExtra("isPhoto", false);
                                    }

                                    // 수정할 게시물의 위치 번호도 넣어줌
                                    postIntent.putExtra("postNum", position);

                                    startActivityForResult(postIntent, 53);

                                    break;
                                //삭제 버튼
                                case R.id.delete_button:
                                    Toast.makeText(getApplicationContext(),"삭제" + position, Toast.LENGTH_LONG).show();
                                    // 삭제 확인을 뭍는 다이얼로그 창
                                    AlertDialog.Builder builder = new AlertDialog.Builder(NoticeBoardActivity.this);
                                    // 다이얼로그 제목
                                    builder.setTitle("삭제");
                                    // 다이얼로그 메세지
                                    builder.setMessage("게시물을 삭제 하시겠습니까?");
                                    // "예" 버튼
                                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getApplicationContext(),"예", Toast.LENGTH_LONG).show();

                                            //SharedPreferences 에서 먼저 삭제
                                            SharedPreferences preferences1 = getSharedPreferences("posts", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences1.edit();

                                            //dates에서 먼저 날짜를 찾아서 삭제!
                                            String dates = preferences1.getString("dates" , null);
                                            String[] date = dates.split("★");
                                            ArrayList<String> dateList = new ArrayList<>();

                                            for(int i = 0; i < date.length; i++){
                                                // 삭제하려고 하는 게시물 날짜와 같으면 null값으로 함
                                                if(posts.get(position).getDate().equals(date[i])){
                                                    date[i] = null;
                                                }
                                                // 아니면 날짜를 만든 리스트에 넣음.
                                                else{
                                                    dateList.add(date[i]);
                                                }
                                            }

                                            // 다시 새로 넣음 dates 값을 합치기 위해 StringBuffer 생성
                                            StringBuffer stringBuffer = new StringBuffer(dateList.get(0));

                                            // dateList 사이즈 만큼 돌면서 stringBuffer에 날짜 추가
                                            for(int i = 1; i < dateList.size(); i++){
                                                stringBuffer.append("★" + dateList.get(i));
                                            }

                                            dates = stringBuffer.toString();

                                            // 삭제가 완료된 날짜들 vaule를 dates key값에 넣음
                                            editor.putString("dates", dates);

                                            // SharedPreferences에서 게시물 삭제
                                            editor.remove(posts.get(position).getDate());

                                            editor.apply();

                                            //게시글 객체 삭제
                                            posts.remove(position);

                                            // 어뎁터의 최신 데이터로 바꿈.
                                            adapter.notifyDataSetChanged();

                                        }
                                    });
                                    // "아니요" 버튼
                                    builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getApplicationContext(),"아니요", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    builder.show();
                                    break;
                            }
                            return false;
                        }
                    });

                    popupMenu.show();
                }
                // 본인 게시물이 아니면 수정 삭제 불가
                else{

                }

            }
        });
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(NoticeBoardActivity.this, new LinearLayoutManager(this).getOrientation());

        // 플로팅 액션 버튼 클릭시
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoticeBoardActivity.this, WritingBoardActivity.class);
                startActivityForResult(intent, 52);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                // 게시글 등록
                case 52:
                    // 인텐트로 게시물 객체 받음
                    Post post = (Post) intent.getSerializableExtra("post");

                    // 인덱스를 0으로 해서 최신글이 맨 위로 올 수 있도록 함!!
                    posts.add(0, post);

                    // 쉐어드 프리퍼런스에 게시물 객체를 String으로 저장
                    SharedPreferences preferences = getSharedPreferences("posts", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    //1. 날짜 저장
                    //처음 저장 할 떄
                    if(preferences.getString("dates", null) == null){
                        editor.putString("dates", post.getDate());
                    }
                    //계속 날짜가 하나씩 쌓이게 할 떄
                    //ex) 20190709120932 + 20190709130811 ....
                    else {
                        // 먼저 지금 dates에 있는 값을 가져옴
                        StringBuffer dates = new StringBuffer(preferences.getString("dates", null));

                        // 최근 날짜를 구분자"★"와 함께 dates 값에 더함.
                        dates.append("★"+ post.getDate());

                        //다시 SharedPreferences에 넣어줌
                        editor.putString("dates", dates.toString());

                    }

                    //2. 게시물 저장
                    //사진이 있는 게시물
                    if(intent.getBooleanExtra("isPhoto", false)){
                        editor.putString(post.getDate(),            //key
                                          post.getCategory() + "★"//value 카테고리
                                        + post.getTitle() + "★"         //제목
                                        + post.getContent() + "★"       //내용
                                        + post.getName() + "★"          //글쓴이
                                        + post.getDate() + "★"          //날짜
                                        + post.getUid() + "★"           //회원고유번호
                                        + post.getPhotoUri());           //사진
                    }
                    // 사진이 없는 게시물
                    else{
                        editor.putString(post.getDate(),            //key
                                post.getCategory() + "★"          //value 카테고리
                                        + post.getTitle() + "★"         //제목
                                        + post.getContent() + "★"       //내용
                                        + post.getName() + "★"          //글쓴이
                                        + post.getDate() + "★"          //날짜
                                        + post.getUid());                //회원고유번호
                    }

                    editor.apply();

                    // 어뎁터 새로고침
                    adapter.notifyDataSetChanged();

                    break;

                // 게시글 수정
                case 53:
                    //게시글 객체의 리스트 인덱스
                    int position = intent.getIntExtra("position", 1);
                    Post post1 = (Post)intent.getSerializableExtra("completePost");
                    // 수정된 카테고리
                    posts.get(position).setCategory(post1.getCategory());
                    // 수정된 제목
                    posts.get(position).setTitle(post1.getTitle());
                    // 수정된 내용
                    posts.get(position).setContent(post1.getContent());
                    // 수정된 사진
                    //사진이 있는 경우
                    if(intent.getBooleanExtra("isPhoto", false)){
                        posts.get(position).setPhotoUri(post1.getPhotoUri());
                    }
                    //사진이 없는 경우
                    else{
                        posts.get(position).setPhotoUri(null);
                    }

                    //수정된 글 SharedPreferences에 저장하기
                    SharedPreferences preferences1 = getSharedPreferences("posts", MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = preferences1.edit();

                    //사진이 있는 게시물
                    if(intent.getBooleanExtra("isPhoto", false)){
                        editor1.putString(post1.getDate(),            //key
                                          post1.getCategory() + "★"//value 카테고리
                                        + post1.getTitle() + "★"         //제목
                                        + post1.getContent() + "★"       //내용
                                        + post1.getName() + "★"          //글쓴이
                                        + post1.getDate() + "★"          //날짜
                                        + post1.getUid() + "★"           //회원고유번호
                                        + post1.getPhotoUri());           //사진
                    }
                    // 사진이 없는 게시물
                    else{
                        editor1.putString(post1.getDate(),            //key
                                          post1.getCategory() + "★"          //value 카테고리
                                        + post1.getTitle() + "★"         //제목
                                        + post1.getContent() + "★"       //내용
                                        + post1.getName() + "★"          //글쓴이
                                        + post1.getDate() + "★"          //날짜
                                        + post1.getUid());                //회원고유번호
                    }

                    editor1.apply();

                    // 어뎁터의 최신 데이터로 바꿈.
                    adapter.notifyDataSetChanged();
                    break;

            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("게시판", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("게시판", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("게시판", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("게시판", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("게시판", "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("게시판", "onRestart");
    }

    //툴바 메뉴 만들기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_notice_broad_menu, menu);

        // searchView 생성
        SearchView searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        // 검색 버튼을 눌렀을 때 뷰가 꽉차게 해주기
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // searchView 힌트
        searchView.setQueryHint("게시물 검색");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);

                return false;
            }
        });

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

            // 검색하기 버튼
            case R.id.action_search:



                return true;
            // 카테고리 버튼
            case R.id.action_category:

                final CharSequence menu[] = new CharSequence[]{"전체", "공지사항", "코이노니아", "기도제목", "묵상글", "기타"};

                //다이얼로그 생성
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(NoticeBoardActivity.this);

                //제목 설정
                builder.setTitle("게시물 카테고리");

                // 배열을 다이얼로그에 붙이기
                // 배열의 항목들을 클릭 했을 시
                builder.setItems(menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getPostbyCategory(menu[which].toString());
                    }
                });

                // 리스트 다이얼로그 화면에 보이기
                builder.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    //쉐어드에서 카테고리 별로 게시물을 가지고 오기
    private void getPostbyCategory(String category){

        //게시물 ArrayList 초기화
        posts.clear();

        //쉐어드 프리퍼런스에 저장된 게시물 값을 빼기
        SharedPreferences preferences = getSharedPreferences("posts", MODE_PRIVATE);
        // posts.xml 에서 dates 값 가져오기
        String dates = preferences.getString("dates", null);

        //SharedPreferences가 비었을 때는 작동을 안하도록
        if(dates != null){
            // 날짜를 하나씩 쪼개서 배열에 저장
            String[] date = dates.split("★");
            // 쪼갠 날짜를 순서대로 key 값에 대입
            // key 값으로 해당 게시물을 가지고 오면 value 값을 다시 제목, 이름 , 날짜, 내용 순으로 쪼갬
            // post 객체를 만들어서 posts ArrayList에 저장
            for(int i = 0; i< date.length; i++){
                String postValue = preferences.getString(date[i], null);
                String[] postElement = postValue.split("★");

                Post post;

                //카테고리가 전체 일때
                if(category.equals("전체")){
                    // 사진 없는 게시물
                    if(postElement.length == 6){
                        post = new Post(postElement[0], postElement[1], postElement[2], postElement[3], postElement[4], postElement[5]);
                        Log.v("사진", "사진 없는 게시물 쉐어드 뻄");
                        // 최신글로 오게 설정!!
                        posts.add(0, post);
                    }
                    //사진 있는 게시물
                    else if(postElement.length == 7){
                        post = new Post(postElement[0], postElement[1], postElement[2], postElement[3], postElement[4], postElement[5], postElement[6]);
                        Log.v("사진", "사진 있는 게시물 쉐어드 뻄");
                        // 최신글로 오게 설정!!
                        posts.add(0, post);
                    }
                }
                //나머지 카테고리 일때
                else {
                    //카테고리 별로 분류하기
                    if(postElement[0].equals(category)){
                        // 사진 없는 게시물
                        if(postElement.length == 6){
                            post = new Post(postElement[0], postElement[1], postElement[2], postElement[3], postElement[4], postElement[5]);
                            Log.v("사진", "사진 없는 게시물 쉐어드 뻄");
                            // 최신글로 오게 설정!!
                            posts.add(0, post);
                        }
                        //사진 있는 게시물
                        else if(postElement.length == 7){
                            post = new Post(postElement[0], postElement[1], postElement[2], postElement[3], postElement[4], postElement[5], postElement[6]);
                            Log.v("사진", "사진 있는 게시물 쉐어드 뻄");
                            // 최신글로 오게 설정!!
                            posts.add(0, post);
                        }
                    }
                }
            }

            //해당 카테고리의 게시물이 없을 때 토스트 띄어주기
            if(posts.size() == 0){
                Toast.makeText(getApplicationContext(), category+" 게시물은 없습니다!", Toast.LENGTH_SHORT).show();
            }
            Log.v("게시물", posts.size()+"");

            adapter.notifyDataSetChanged();
        }
    }

}

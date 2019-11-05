package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class PhotoGalleryActivity extends AppCompatActivity {

    //뷰 변수 선언
    Toolbar toolbar;
    RecyclerView recyclerView;

    //사진게시물들을 담을 리스트 생성
    ArrayList<PhotoPost> photoPosts = new ArrayList<>();

    PhotoPostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        //뷰 생성
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        // addItems();

        // 툴바 설정
        setSupportActionBar(toolbar);
        // 툴바 왼쪽 버튼 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);

        //쉐어드에서 불러와서 ArrayList에 저장
        SharedPreferences preferences = getSharedPreferences("photoPosts", MODE_PRIVATE);

        //photoPosts.xml에서 dates 값 가져오기
        String dates = preferences.getString("dates", null);

        //SharedPreferences가 비어었을 때는 값을 안불러 옴
        if(dates != null){
            // 날짜를 하니씩 쪼개서 배열에 저장
            String[] date = dates.split("★");
            // 쪼갠 날짜를 순서대로 key 값에 대입
            // key 값으로 해당 게시물을 가지고 오면 value 값을 다시 제목, 이름 , 날짜, 내용 순으로 쪼갬
            // post 객체를 만들어서 posts ArrayList에 저장
            for(int i = 0; i< date.length; i++){
                String postValue = preferences.getString(date[i], null);
                String[] postElement = postValue.split("★");

                PhotoPost photoPost = new PhotoPost(postElement[0], postElement[1], postElement[2], postElement[3], postElement[4]);

                // 최신글로 오게 설정!!
                photoPosts.add(0, photoPost);

            }

        }

        // 리싸이클러뷰 그리드 레이아웃으로 정렬
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        adapter = new PhotoPostAdapter(photoPosts);
        recyclerView.setAdapter(adapter);


        //17. 이미지 롱클릭 햇을 때
        adapter.setOnImageViewLongClickListener(new PhotoPostAdapter.OnImageViewLongClickListener() {
            @Override
            public void onImageViewLongClick(View v, final int position) {
                Toast.makeText(PhotoGalleryActivity.this, "이미지 롱클릭" + position, Toast.LENGTH_SHORT).show();

                // '수정', '삭제' 문자를 배열에 넣음
                CharSequence menu[] = new CharSequence[] {"수정", "삭제"};

                //다이얼로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(PhotoGalleryActivity.this);

//               //제목 설정
//               builder.setTitle("사진 게시물을 어떻게 하시겠습니까?");

                //위에 문자 배열과 다이얼로그 리스트를 합침.
                builder.setItems(menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            // 수정 버튼 눌렀을 때
                            case 0:
                                Toast.makeText(PhotoGalleryActivity.this, "수정" + position, Toast.LENGTH_SHORT).show();
                                // 수정하기 화면으로 넘어가기
                                Intent intent = new Intent(PhotoGalleryActivity.this, PhotoEditActivity.class);

                                //제목 인텐트에 담기
                                intent.putExtra("title",photoPosts.get(position).getTitle());

                                //사진 uri인텐트에 담기
                                intent.putExtra("photo", photoPosts.get(position).getPhotoUri());

                                //사진 게시물 객체 인덱스 넣기(위치)
                                intent.putExtra("index", position);

                                //결과를 받기 위한 인텐트 시작
                                startActivityForResult(intent, 52);

                                break;
                            //삭제 버튼 눌렀을 때
                            case 1:
                                Toast.makeText(PhotoGalleryActivity.this, "삭제" + position, Toast.LENGTH_SHORT).show();

                                //SharedPreferences 에서 먼저 삭제
                                SharedPreferences preferences1 = getSharedPreferences("photoPosts", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences1.edit();

                                //dates에서 먼저 날짜를 찾아서 삭제!
                                String dates = preferences1.getString("dates" , null);
                                String[] date = dates.split("★");
                                ArrayList<String> dateList = new ArrayList<>();

                                for(int i = 0; i < date.length; i++){
                                    // 삭제하려고 하는 게시물 날짜와 같으면 null값으로 함
                                    if(photoPosts.get(position).getDate().equals(date[i])){
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
                                editor.remove(photoPosts.get(position).getDate());

                                editor.apply();

                                // 리스트에서 사진 게시물 삭제
                                photoPosts.remove(position);
                                // 삭제된 최근 상태로 어텝터 새로고침
                                adapter.notifyDataSetChanged();
                                break;

                        }
                        dialog.dismiss();
                    }
                });
                // 다이얼로그 화면에 보이기
                builder.show();
            }
        });

        //이미지 뷰 일반 클릭 했을 때
        adapter.setOnImageViewClick(new PhotoPostAdapter.OnImageViewClickListener() {
            @Override
            public void onImageViewClick(View v, int position) {
                Toast.makeText(PhotoGalleryActivity.this, "이미지일반클릭" + position, Toast.LENGTH_SHORT).show();
                // 이미지 상세 보기 페이지로 이동
                Intent intent = new Intent(PhotoGalleryActivity.this, PhotoDetailActivity.class);

                intent.putExtra("selectedPhotoPost", photoPosts.get(position));

                startActivity(intent);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //새로운 게시물 올리기
        if(requestCode == 50){
            Log.v("코드", requestCode+"");
            //보낸 사진 게시물 객체 받아오기
            PhotoPost photoPost = (PhotoPost) data.getSerializableExtra("photoPost");
            Log.v("넘어온 사진", "사진"+photoPost.getPhotoUri());
            Log.v("넘어온 사진", "제목"+photoPost.getTitle());
            Log.v("넘어온 사진", "이름"+photoPost.getName());
            Log.v("넘어온 사진", "날짜"+photoPost.getDate());
            Log.v("넘어온 사진", "uid"+photoPost.getUid());


            //사진 쉐어드에도 저장!
            SharedPreferences preferences = getSharedPreferences("photoPosts", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            //1. 날짜 저장
            // 처음 저장 할때
            if (preferences.getString("dates", null) == null) {
                editor.putString("dates", photoPost.getDate());
            }
            // 계속 날짜가 하나씩 쌓이게 할 때
            else {
                // 먼저 dates를 불러와서 StringBuffer에 넣음
                StringBuffer dates = new StringBuffer(preferences.getString("dates", null));

                // 최근 날짜를 구분자"★"와 함께 dates 값에 더함.
                dates.append("★" + photoPost.getDate());

                //다시 SharedPreferences에 넣어줌
                editor.putString("dates", dates.toString());

            }
            //2. 게시물 저장
            editor.putString(photoPost.getDate(),
                    photoPost.getPhotoUri() + "★"
                            + photoPost.getTitle() + "★"
                            + photoPost.getName() + "★"
                            + photoPost.getDate() + "★"
                            + photoPost.getUid());

            editor.apply();

            // 인덱스를 0으로 주어서 최신 사진이 맨 위로 올 수 있게 함.
            photoPosts.add(0, photoPost);

            adapter.notifyDataSetChanged();


        }
        //수정
        else if(requestCode == 52){
            // 인덱스 값 받아오기
            int position = data.getIntExtra("index",1);

            // 제목을 해당 인덱스 객체에 넣기
            photoPosts.get(position).setTitle(data.getStringExtra("title"));

            // 사진을 해당 인덱스 객체에 넣기
            photoPosts.get(position).setPhotoUri(data.getStringExtra("photo"));

            // 수정된 값 쉐어드에 넣기
            SharedPreferences preferences = getSharedPreferences("photoPosts", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString(photoPosts.get(position).getDate(),
                              photoPosts.get(position).getPhotoUri() + "★"
                            + photoPosts.get(position).getTitle() + "★"
                            + photoPosts.get(position).getName() + "★"
                            + photoPosts.get(position).getDate() + "★"
                            + photoPosts.get(position).getUid());
            editor.apply();

            //어뎁터 새로고침
            adapter.notifyDataSetChanged();
        }
    }

//    // 더미아이템 추가!
//    public void addItems(){
//
//        // 리소스에 있는 파일을 비트맵으로 변환
//        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.fivetwodream_logo);
//
//        // 비트맵에서 uri로 변환
//        Uri uri = getImageUri(getApplicationContext(), bitmap1);
//
//    }
//
////     비트맵(jpg)을 uri로 변환하는 메소드
//    private Uri getImageUri(Context context, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
//
//        Log.v("사진", path);
//        return Uri.parse(path);
//    }

    //툴바 메뉴 만들기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_photo_menu, menu);

        return true;
    }

    //툴바 메뉴 설정

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 사진 올리기 버튼
            case R.id.action_upload:
                Intent intent = new Intent(PhotoGalleryActivity.this, PhotoUploadActivity.class);
                startActivityForResult(intent, 50);

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}

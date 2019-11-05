package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class PhotoUploadActivity extends AppCompatActivity {

    // 사진 반활 할때 사용할 resultCode
    private static final int PICK_FROM_ALBUM = 1;  // 갤러리
    private static final int PICK_FROM_CAMERA = 2; // 카메라

    private Boolean isPermission = true;
    // 카메라에서 온 화면인지 앨범에서 온 화면인지 구분
    private Boolean isCamera = false;

    // 받아올 이미지를 저장할 파일 변수
    private File tempFile;

    //사진 게시물을 담을 객체 변수 선언
    PhotoPost photoPost;

    // 뷰 변수 선언
    ImageView imageView;
    Button camera_Button, gallery_Button, upload_Button;
    EditText title_EditText;
    Toolbar toolbar;

    //파이어베이스 유저
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);

        // 뷰 객체 생성
        imageView = (ImageView)findViewById(R.id.post_imageView);
        camera_Button = (Button)findViewById(R.id.camera_button);
        gallery_Button = (Button)findViewById(R.id.photo_gallery_button);
        upload_Button = (Button)findViewById(R.id.upload_button);
        title_EditText = (EditText)findViewById(R.id.title_editText);
        toolbar = findViewById(R.id.toolbar);

        // 툴바 설정
        setSupportActionBar(toolbar);
        // 툴바 왼쪽 버튼 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);

        // 사진 게시물 객체 선언
        photoPost = new PhotoPost();

        //권한 허용
        tedPermission();

        user = FirebaseAuth.getInstance().getCurrentUser();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence menu[] = new CharSequence[] {"사진 찍기", "갤러리에서 선택"};

                //다이얼로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(PhotoUploadActivity.this);

//              //제목 설정
//              builder.setTitle("사진 게시물을 어떻게 하시겠습니까?");

                //위에 문자 배열과 다이얼로그 리스트를 합침.
                builder.setItems(menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            // 사진 찍기
                            case 0:
                                if(isPermission){
                                    takePhoto();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
                                }
                                break;
                            // 갤러리 이동
                            case 1:
                                if(isPermission)  {
                                    goToAlbum();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
                                }
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                // 다이얼로그 화면에 보이기
                builder.show();
            }
        });


        // 사진올리기 버튼
        upload_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //사진은 아래서 넣음.
                Log.v("사진", "사진 : " + photoPost.getPhotoUri());
                //제목
                photoPost.setTitle(title_EditText.getText().toString());
                Log.v("사진", "제목 : " + photoPost.getTitle());
                //이름
                photoPost.setName(user.getDisplayName());
                Log.v("사진", "이름 : " + photoPost.getName());

                //날짜
                // 현재 시간 가져오기
                long now = System.currentTimeMillis();

                // Date 생성하기
                Date date = new Date(now);

                // 원하는 형식으로 바꾸기
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //20190709190323
                String currentTime = simpleDateFormat.format(date);

                // 업로드한 시간을 사진 게시물 객체에 저장
                photoPost.setDate(currentTime);
                Log.v("사진", "날짜 : " + photoPost.getDate());
                //uid
                photoPost.setUid(user.getUid());
                Log.v("사진", "uid : " + photoPost.getUid());

                Intent intent = new Intent();
                intent.putExtra("photoPost", photoPost);
                setResult(RESULT_OK, intent);
                finish();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //7. 예외 처리
        if(resultCode != Activity.RESULT_OK){
            Toast.makeText(this, "취소 되었습니다", Toast.LENGTH_SHORT).show();

            if(tempFile != null){
                if(tempFile.exists()){
                    if(tempFile.delete()){
                        Log.e(" ", tempFile.getAbsolutePath() + "삭제성공");
                        tempFile = null;
                    }
                }
            }

            return;
        }


        //2. 엘범에서 받아오기
        if(requestCode == PICK_FROM_ALBUM){

            // 갤러리에서 선택한 이미지를 uri로 받아옴.
            Uri photoUri = data.getData();
            Log.d("사진", "PICK_FROM_ALBUM photoUri : " + photoUri);

//            Cursor cursor = null;
//
//            //cursor를 통해 content:// --> file:// 로 변경
//            //사진의 저장된 절대 경로를 받아오는 과정
//            try{
//                String[] proj = {MediaStore.Images.Media.DATA};
//
//                assert photoUri != null;
//                cursor = getContentResolver().query(photoUri, proj, null, null, null);
//
//                assert cursor != null;
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//
//                cursor.moveToFirst();
//
//                tempFile = new File(cursor.getString(column_index));
//
//                Log.d("사진", "tempFile Uri : " + Uri.fromFile(tempFile));
//            }
//            finally {
//                if(cursor != null){
//                    cursor.close();
//                }
//            }

            cropImage(photoUri);

        }
        //6. 카메라 값 받아오기
        else if(requestCode == PICK_FROM_CAMERA){

            Uri photoUri = Uri.fromFile(tempFile);

            cropImage(photoUri);
        }
        else if(requestCode == Crop.REQUEST_CROP){

            setImage();
        }
    }

    // 이미지 크롭하기
    private void cropImage(Uri photoUri){

        Log.d("사진", "tempFile" + tempFile);

        if(tempFile == null){
            try{
                tempFile = createImageFIle();
            }catch (IOException e){
                Toast.makeText(this,"이미지 처리 오류! 다시 시도해 주세요.",Toast.LENGTH_SHORT).show();
                finish();
                e.printStackTrace();
            }

        }

        Uri savingUri = Uri.fromFile(tempFile);

        Crop.of(photoUri, savingUri).asSquare().start(this);
    }

    // 3. 갤러리에서 받아온 이미지 넣기
    private void setImage(){

        ImageResizeUtils.resizeFile(tempFile, tempFile, 1280, isCamera);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(),options);

        Log.d("사진", "setImage : " + tempFile.getAbsolutePath());

        imageView.setImageBitmap(originalBm);

        // 이미지를 사진게시물 객체에 넣기
        photoPost.setPhotoUri(Uri.fromFile(tempFile).toString());

        tempFile = null;

    }

    // 4. 카메라에서 이미지 가져오기
    private void takePhoto(){

        isCamera = true;

        // 카메라 화면을 불러오는 인텐트
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try{
            tempFile = createImageFIle();

        }catch (IOException e){
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요!", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }

        // 이미지 파일이 null 아닐때
        if(tempFile != null){

            // 추가
            if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N){
                //Uri photoUri = Uri.fromFile(tempFile);

                Uri photoUri = FileProvider.getUriForFile(this, ".provider", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }
            else{
                Uri photoUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }


        }
    }

    // 5. 카메라에서 찍은 사진을 저장할 파일 만들기
    private File createImageFIle() throws IOException{

        // 이미지 파일 이름
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "52dream_" + timeStamp + "_";

        // 이미지가 저장될 폴더 이름
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/52dream");

        if(!storageDir.exists()){
            storageDir.mkdirs();
        }

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, "jpg", storageDir);

        return image;

    }

    // 2. 앨범으로 이동하는 메소드
    private void goToAlbum(){
        isCamera = false;
        // 앨범 화면으로 이동할 인텐트
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);

    }

    // 1. 카메라 권한 설정 메소드
    private void tedPermission(){

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                isPermission = true;
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
                isPermission = false;
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }
}

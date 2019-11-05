package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PhotoEditActivity extends AppCompatActivity {

    // 사진 반활 할때 사용할 resultCode
    private static final int PICK_FROM_ALBUM = 1;  // 갤러리
    private static final int PICK_FROM_CAMERA = 2; // 카메라

    private Boolean isPermission = true;
    // 카메라에서 온 화면인지 앨범에서 온 화면인지 구분
    private Boolean isCamera = false;

    // 받아올 이미지를 저장할 파일 변수
    private File tempFile;

    //사진 uri를 담을 변수 선언
    Uri photoUri;

    // 뷰 변수 선언
    ImageView imageView;
    Button camera_Button, gallery_Button, upload_Button;
    EditText title_EditText;

    int position; // 리스트에 인덱스를 가지고 옴.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);

        // 뷰 객체 생성
        imageView = (ImageView)findViewById(R.id.post_imageView);
        camera_Button = (Button)findViewById(R.id.camera_button);
        gallery_Button = (Button)findViewById(R.id.photo_gallery_button);
        upload_Button = (Button)findViewById(R.id.upload_button);
        title_EditText = (EditText)findViewById(R.id.title_editText);

        // 제목 창에는 넘겨온 제목 글자로 설정
        title_EditText.setText(getIntent().getStringExtra("title"));

        // 이미지에는 수정하기 전 이미지로 설정
        imageView.setImageURI(Uri.parse(getIntent().getStringExtra("photo")));

        position = getIntent().getIntExtra("index",1);

        //카메라 버튼
        camera_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPermission){
                    takePhoto();
                }
                else {
                    Toast.makeText(v.getContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
                }
            }
        });

        // 갤러리로 이동 버튼
        gallery_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPermission)  {
                    goToAlbum();
                }
                else {
                    Toast.makeText(v.getContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
                }
            }
        });

        // 사진올리기 버튼
        upload_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();

                //제목
                String title = title_EditText.getText().toString();
                intent.putExtra("title", title);

                //사진 url
                intent.putExtra("photo", photoUri.toString());

                //인덱스 번호
                intent.putExtra("index", position);

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
        photoUri = Uri.fromFile(tempFile);

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

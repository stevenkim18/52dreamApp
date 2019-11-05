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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditBoardActivity extends AppCompatActivity {

    //뷰 객체 선언
    EditText title_editText, content_editText;
    ImageView imageView;
    Button edit_Button;
    Toolbar toolbar;
    Spinner category_Spinner;

    // 사진 반활 할때 사용할 resultCode
    private static final int PICK_FROM_ALBUM = 1;  // 갤러리
    private static final int PICK_FROM_CAMERA = 2; // 카메라

    private Boolean isPermission = true;
    // 카메라에서 온 화면인지 앨범에서 온 화면인지 구분
    private Boolean isCamera = false;

    // 받아올 이미지를 저장할 파일 변수
    private File tempFile;

    private Uri imageUri;

    // 게시물 객체
    Post post;

    // 수정된 게시물 위치 저장
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_board);

        title_editText = findViewById(R.id.title_editText);
        content_editText = findViewById(R.id.content_editText);
        edit_Button = findViewById(R.id.edit_button);
        imageView = findViewById(R.id.post_imageView);
        toolbar = findViewById(R.id.toolbar);
        category_Spinner = findViewById(R.id.category_spinner);

        // 툴바 설정
        setSupportActionBar(toolbar);
        // 툴바 왼쪽 버튼 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);

        // 인텐트로 수정할 게시글 객체의 리스트 인덱스를 받음
        position = getIntent().getIntExtra("postNum",1);

        post = (Post) getIntent().getSerializableExtra("editedPost");


        //제목
        title_editText.setText(post.getTitle());
        //내용
        content_editText.setText(post.getContent());
        //사진
        //사진 있을 때
        if(getIntent().getBooleanExtra("isPhoto", false)){
            imageView.setImageURI((Uri.parse(post.photoUri)));
        }
        //사진 없을 때
        else{
            imageView.setVisibility(View.GONE);
        }

        //이미지 롱클릭시 삭제
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 삭제확인에 대한 다이얼로그를 보여줌
                final AlertDialog.Builder builder = new AlertDialog.Builder(EditBoardActivity.this);
                builder.setTitle("사진을 삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 이미지뷰를 빈화면으로 만들고
                        imageView.setImageURI(null);
                        // 이미지뷰를 안보이게 하고
                        imageView.setVisibility(View.GONE);
                        // 게시물 객체에 이미지를 지운다.
                        post.setPhotoUri(null);
                    }
                });
                builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();

                return false;
            }
        });

    }

    //툴바 메뉴 만들기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_edit_board_menu, menu);

        return true;
    }

    //툴바 메뉴 설정

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //사진 올리기
            case R.id.action_photo_upload:
                CharSequence menu[] = new CharSequence[] {"사진 찍기", "갤러리에서 선택"};

                //다이얼로그 생성
                android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(EditBoardActivity.this);

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

                return true;

            // 게시글 올리기
            case R.id.action_edit_complete:

//                // 다시 인텐트로 게시판에 보내기
//                Intent intent = new Intent();
//
//                //사진
//                //사진에 이미지가 있을 때
//                if(imageView.getDrawable() != null){
//                    intent.putExtra("photo", imageUri);
//                    intent.putExtra("isPhoto", true );
//                    Log.v("사진", "사진 있음");
//                }
//                else{
//                    intent.putExtra("isPhoto", false);
//                    Log.v("사진", "사진 없음");
//                }
//
                Intent intent = new Intent();

                //수정된 카테고리
                post.setCategory(category_Spinner.getSelectedItem().toString());

                //수정된 제목
                post.setTitle(title_editText.getText().toString());

                //수정된 내용
                post.setContent(content_editText.getText().toString());

                //수정된 사진
                if(imageView.getDrawable() != null){
                    // 이미지는 수정을 한 경우
                    if(imageUri != null){
                        post.setPhotoUri(imageUri.toString());
                    }
                    intent.putExtra("isPhoto", true );
                    Log.v("사진", "사진 있음");
                }
                else{
                    intent.putExtra("isPhoto", false);
                    Log.v("사진", "사진 없음");
                }

                // 수정된 게시물 객체 인텐트에 담음
                intent.putExtra("completePost", post);
                // 수정된 게시물 위치도 인텐트에 담음
                intent.putExtra("position", position);

                setResult(RESULT_OK, intent);
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
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

        //이미지뷰 보이게 하기
        imageView.setVisibility(View.VISIBLE);

        // 파이어베이스에 저장하기 위함.
        imageUri = Uri.fromFile(tempFile);

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

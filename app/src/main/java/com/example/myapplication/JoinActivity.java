package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.soundcloud.android.crop.Crop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;


public class JoinActivity extends AppCompatActivity {

    //회원가입시 정보를 담을 Profile 객체 생성
    Profile profile;

    //뷰 선언
    ImageView imageView;
    TextInputLayout email_TextInputLayout, password_TextInputLayout, password_check_TextInputLayout, name_TextInputLayout;
    EditText email_EditText, password_EditText, password_check_EditText, name_EditText;
    Spinner spinnerAgeNumber, spinnerVillage;
    Button buttonJoin, buttonIdcheck;

    //파이어베이스 인증
    private FirebaseAuth mAuth;
    //파이어베이스 firestore
    private FirebaseFirestore db;
    //파이어베이스 storage
    private FirebaseStorage storage;

    private StorageReference reference;

    // 사진 반활 할때 사용할 resultCode
    private static final int PICK_FROM_ALBUM = 1;  // 갤러리
    private static final int PICK_FROM_CAMERA = 2; // 카메라

    private Boolean isPermission = true;
    // 카메라에서 온 화면인지 앨범에서 온 화면인지 구분
    private Boolean isCamera = false;

    // 받아올 이미지를 저장할 파일 변수
    private File tempFile;

    private Uri imageUri;

    // 이메일 보내기
    GMailSender gMailSender;
    // 이메일 인증 코드
    String code;
    // 인증여부
    boolean isAuth;

    // 이메일 인증 타이머
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        //뷰 생성
        imageView = findViewById(R.id.post_imageView); // 프로필 사진

        email_TextInputLayout = findViewById(R.id.email_TextInput); // 이메일 레이아웃
        email_EditText = findViewById(R.id.email_editText); //이메일

        password_TextInputLayout = findViewById(R.id.password_Textinput); // 비밀번호 레이아웃
        password_EditText = findViewById(R.id.password_editText); // 비밀번호

        password_check_TextInputLayout = findViewById(R.id.password_check_Textinput); // 비밀번호 확인 레이아웃
        password_check_EditText = findViewById(R.id.password_check_editText); // 비밀번호 확인

        name_TextInputLayout = findViewById(R.id.name_TextInput); // 이름 레이아웃
        name_EditText = findViewById(R.id.name_editText); // 이름

        spinnerVillage = findViewById(R.id.spinnerVillage);// 마을
        spinnerAgeNumber = findViewById(R.id.spinnerAgeNumber);// 기수

        buttonJoin = (Button)findViewById(R.id.buttonJoin); // 회원가입 버튼
        buttonIdcheck = findViewById(R.id.buttonIdCheck); // 인증하기 버튼

        //인터넷 사용을 위한 인증
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        // 파이어베이스 인증
        mAuth = FirebaseAuth.getInstance();
        // 파이어베이스 firestore
        db = FirebaseFirestore.getInstance();
        // 파이어베이스 storage
        storage = FirebaseStorage.getInstance();

        final String email = email_EditText.getText().toString();
        final String password = password_EditText.getText().toString();
        final String name = name_EditText.getText().toString();
        final String village = spinnerVillage.getSelectedItem().toString();
        final String ageNum = spinnerAgeNumber.getSelectedItem().toString();

        //권한 허용
        tedPermission();

        // 프로필사진 선택 클릭시
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence menu[] = new CharSequence[] {"사진 찍기", "갤러리에서 선택"};

                //다이얼로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);

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



        //이메일 TextInputLayout
        email_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 이메일 유효성 체크
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email_EditText.getText().toString()).matches()){
                    email_TextInputLayout.setError("이메일 형식으로 입력해주세요");
                }
                else{
                    email_TextInputLayout.setError(null);
                }
            }
        });

        //비밀번호 TextInputLayout
        password_TextInputLayout.setPasswordVisibilityToggleEnabled(true);

        //비밀번호 카운트 사용
        password_TextInputLayout.setCounterEnabled(true);

        password_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() < 8){
                    password_TextInputLayout.setError("비밀번호는 8자리 이상으로 설정해주세요");
                }
                else {
                    password_TextInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //비밀번호 확인 TextInputLayout
        password_check_TextInputLayout.setPasswordVisibilityToggleEnabled(true);

        password_check_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(password_EditText.getText().toString())){
                    password_check_TextInputLayout.setError("비밀번호가 일치 하지 않습니다!");
                }
                else{
                    password_check_TextInputLayout.setError(null);

                }
            }
        });

        //인증하기 버튼 눌렀을 때
        buttonIdcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 다이얼로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);

                //커스텀 다이얼로그와 합침
                //이메일 인증번호 확인 체크 레이아웃
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.email_check_dialog, null);
                builder.setView(view);

                final TextView email_TextView = view.findViewById(R.id.email_textView); //이메일 + 안내문
                final Button send_code_Button = view.findViewById(R.id.send_code_button); // 인증코드 보내기 버튼
                final EditText code_editText = view.findViewById(R.id.code_editText); // 코드 입력 창
                final Button check_Button = view.findViewById(R.id.check_button); // 인증코드 체크 버튼
                final TextView time_TextView = view.findViewById(R.id.time_textView); // 인증 제한 시간
                final Button cancel_Button = view.findViewById(R.id.cancel_button); // 취소 버튼
                final Button ok_Button = view.findViewById(R.id.ok_button); // 계속 회원가입 하기 버튼

                //인증코드 확인 버튼은 처음에 비활성화
                //인증코드를 보내면 활성화 시킴.
                check_Button.setEnabled(false);
                check_Button.setTextColor(Color.parseColor("#C0C0C0")); //회색

                // 계속 회원하기 버튼 비활성화 시킴
                // 인증코드가 맞으면 활성화로 전환
                ok_Button.setEnabled(false);
                ok_Button.setTextColor(Color.parseColor("#C0C0C0")); // 회색

                final AlertDialog dialog = builder.create();
                //인증코드를 보낸 이메일을 나타냄
                email_TextView.setText(email_EditText.getText().toString() + "으로");

                //인증코드 보내기 버튼
                send_code_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String receiverEmail = email_EditText.getText().toString();

                        sendEmail(receiverEmail,time_TextView, check_Button);

//                        try {
//
//                            //실제 존재하고 있는 이메일주소와 비밀번호를 넣어야 함.
//                            //그 이메일로 로그인을 해서 비밀번호를 보냄.
//                            gMailSender = new GMailSender("52dreamapp@gmail.com", "dhdlemfla");
//                            Log.v("이메일 인증", "객체 초기화");
//
//                            code = gMailSender.getEmailCode();
//                            Log.v("이메일 인증", "인증코드 생성");
//
//                            //GMailSender.sendMail(제목, 본문내용, 받는사람);
//                            gMailSender.sendMail("52Dream앱 인증번호입니다!", "인증코드는 " + code+ "입니다!" , email_EditText.getText().toString());
//                            Log.v("이메일 인증", "인증코드 발송! ");

                            // 인증번호 확인 버튼 활성화
                            check_Button.setEnabled(true);
                            check_Button.setTextColor(Color.parseColor("#000000"));

                            // 타이머 생성
                            // 180초 / 1000 호출 하는 간격
//                            countDownTimer = new CountDownTimer(40*1000, 1000) {
//                                @Override
//                                public void onTick(long millisUntilFinished) {
//
//                                    long emailAuthCount = millisUntilFinished / 1000;
//
//                                    if ((emailAuthCount - ((emailAuthCount / 60) * 60)) >= 10) { //초가 10보다 크면 그냥 출력
//                                        time_TextView.setText((emailAuthCount / 60) + " : " + (emailAuthCount - ((emailAuthCount / 60) * 60)));
//                                    } else { //초가 10보다 작으면 앞에 '0' 붙여서 같이 출력. ex) 02,03,04...
//                                        time_TextView.setText((emailAuthCount / 60) + " : 0" + (emailAuthCount - ((emailAuthCount / 60) * 60)));
//                                    }
//
//                                }
//
//                                // 끝났을 때
//                                @Override
//                                public void onFinish() {
//
//                                    // 인증 확인 버튼 비활성화
//                                    check_Button.setEnabled(false);
//                                    check_Button.setTextColor(Color.parseColor("#C0C0C0"));
//
//                                }
//                            }.start();

                            Log.v("이메일 인증", "타이머 시작");

//                            Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
//                        } catch (SendFailedException e) {
//                            Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
//                        } catch (MessagingException e) {
//                            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                    }
                });

                //인증코드 체크 버튼 클릭
                check_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 입력한 코드와 보낸 인증 코드 같으면
                        if (code_editText.getText().toString().equals(code)) {

                            // 타이머 종료!
                            countDownTimer.cancel();

                            // "인증 성공" 글자 출력
                            time_TextView.setText("인증 성공!!");

                            // 인증 코드 보내기 비활성화
                            send_code_Button.setEnabled(false);
                            send_code_Button.setTextColor(Color.parseColor("#C0C0C0")); // 회색

                            // 확인 버튼 비활성화
                            check_Button.setEnabled(false);
                            check_Button.setTextColor(Color.parseColor("#C0C0C0")); //회색

                            // 계속 회원하기 버튼 활성화
                            ok_Button.setEnabled(true);
                            ok_Button.setTextColor(Color.parseColor("#000000")); // 검은색

                            // 취소 버튼 비 활성화!
                            cancel_Button.setEnabled(false);
                            cancel_Button.setTextColor(Color.parseColor("#C0C0C0")); //회색


                        }
                        else{
                            Toast.makeText(getApplicationContext(),"인증코드 일치 하지 않습니다! 다시 한번 확인해주세요!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // 계속 회원가입 하기
                ok_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 회원가입 이메일 창 입력 비활성화 시키기
                        email_EditText.setClickable(false);
                        email_EditText.setFocusable(false);

                        // 회원가입 화면에 인증하기 버튼도 비활성화 시키기
                        buttonIdcheck.setEnabled(false);
                        buttonIdcheck.setTextColor(Color.parseColor("#C0C0C0"));

                        // 회원가입 버튼 활성화
                        isAuth = true;
                        buttonJoin.setEnabled(isAuth);
                        buttonJoin.setTextColor(Color.parseColor("#000000"));

                        // 다이얼로그 종료
                        dialog.dismiss();

                    }
                });

                // 취소 버튼
                cancel_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 다이얼로그 종료
                        dialog.dismiss();;
                    }
                });

                //다이얼로그 보여주기기
                dialog.show();

            }
        });

        // 처음 들어왔을 때는 인증 여부가 false;
        // 인증을 해야 true로 바뀜.
        isAuth = false;

        buttonJoin.setEnabled(isAuth);
        buttonJoin.setTextColor(Color.parseColor("#C0C0C0"));

        //회원가입 버튼 눌렀을 때
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이메일 검사
                //이메일이 빈칸이 아닐때
                if(email_EditText.getText().toString() != null){

                    //비밀번호 검사
                    //비밀번호가 빈칸이 아닐 때
                    if(password_EditText.getText().toString() != null){

                        //비밀번호 확인 검사
                        //비밀번호 확인과 비밀번호가 일치 할때
                        if(password_check_EditText.getText().toString().equals(password_EditText.getText().toString())){

                            //이름 검사
                            //이름이 빈칸이 아닐 때
                            if(name_EditText.getText().toString() != null){

                                // 마을 검사
                                // 마을을 선택 했을 시
                                if(!spinnerVillage.getSelectedItem().toString().equals("마을")){

                                    //기수 검사
                                    //기수를 선택 했을 시
                                    if(!spinnerAgeNumber.getSelectedItem().toString().equals("기수")){
                                        Toast.makeText(JoinActivity.this, "회원가입 완료!", Toast.LENGTH_SHORT).show();

                                        //이메일 비밀번호 회원정보 인증 계정 만들기
                                        mAuth.createUserWithEmailAndPassword(email_EditText.getText().toString(), password_EditText.getText().toString())
                                                .addOnCompleteListener(JoinActivity.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(JoinActivity.this, "회원인증 성공!", Toast.LENGTH_SHORT).show();

                                                            // 회원의 고유 아이디 값
                                                            final String userId = task.getResult().getUser().getUid();

                                                            // 프로필 이미지 storage에 저장
                                                            reference = storage.getReference().child("userImages").child(userId);
                                                            reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                    // 프로필 정보 데이터베이스에 저장
                                                                    Profile profile = new Profile(imageUri.toString(), email_EditText.getText().toString(), password_EditText.getText().toString(), name_EditText.getText().toString(), spinnerVillage.getSelectedItem().toString(), spinnerAgeNumber.getSelectedItem().toString(), userId);

                                                                    // 쉐어드에 회원정보 저장
                                                                    putProfileDateinSharedPreferences(profile);

                                                                    // auth에 display이름 넣어주기
                                                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                            .setDisplayName(name_EditText.getText().toString())
                                                                            .build();

                                                                    user.updateProfile(profileUpdates)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {

                                                                                    }
                                                                                }
                                                                            });

                                                                    db.collection("users").document(userId)
                                                                            .set(profile)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Toast.makeText(JoinActivity.this, "회원정보 저장!", Toast.LENGTH_SHORT).show();
                                                                                    Intent intent = new Intent();
                                                                                    intent.putExtra("email", email_EditText.getText().toString());
                                                                                    setResult(RESULT_OK, intent);
                                                                                    finish();
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {

                                                                                }
                                                                            });

                                                                }
                                                            });

                                                        } else {
                                                            // If sign in fails, display a message to the user.

                                                        }
                                                    }
                                                });

                                    }
                                    //기수를 선택 안 했을 시
                                    else {
                                        Toast.makeText(JoinActivity.this, "기수를 선택해주세요!",Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(JoinActivity.this, "마을을 선택해주세요!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            //이름이 빈칸 일때
                            else {
                                Toast.makeText(JoinActivity.this, "이름을 입력해주세요!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        //비밀번호확인과 비밀번호가 일치 하지 않을 때
                        else{
                            Toast.makeText(JoinActivity.this, "비밀번호를 확인해주세요!", Toast.LENGTH_SHORT).show();
                        }

                    }
                    //비밀번호가 빈칸 일 때
                    else{
                        Toast.makeText(JoinActivity.this, "비밀번호를 입력해주세요!", Toast.LENGTH_SHORT).show();
                    }


                }
                //이메일이 빈칸 일 때
                else{
                    Toast.makeText(JoinActivity.this, "이메일을 입력해주세요!", Toast.LENGTH_SHORT).show();
                }

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

        // 파이어베이스에 저장하기 위함.
        imageUri = Uri.fromFile(tempFile);
        Log.v("사진", "경로" + imageUri.toString());

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

    private void sendEmail(final String receiverEmail, final TextView timeTextView, final Button checkButton){

        class LoadingAsyncTask extends AsyncTask<Void, Void, Void>{

            AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.loading_dialog, null);
            AlertDialog dialog;
            String email = receiverEmail;

            // 백그라운드 시작 전
            @Override
            protected void onPreExecute() {
                builder.setView(view);
                dialog = builder.create();
                dialog.show();
                Log.v("AsyncTask", "onPreExecute");

            }

            // 수행 종료 후 결과 반환
            @Override
            protected void onPostExecute(Void aVoid) {
                dialog.dismiss();

                // 180초 / 1000 호출 하는 간격
                countDownTimer = new CountDownTimer(40*1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                        long emailAuthCount = millisUntilFinished / 1000;

                        if ((emailAuthCount - ((emailAuthCount / 60) * 60)) >= 10) { //초가 10보다 크면 그냥 출력
                            timeTextView.setText((emailAuthCount / 60) + " : " + (emailAuthCount - ((emailAuthCount / 60) * 60)));
                        } else { //초가 10보다 작으면 앞에 '0' 붙여서 같이 출력. ex) 02,03,04...
                            timeTextView.setText((emailAuthCount / 60) + " : 0" + (emailAuthCount - ((emailAuthCount / 60) * 60)));
                        }

                    }

                    // 끝났을 때
                    @Override
                    public void onFinish() {

                        // 인증 확인 버튼 비활성화
                        checkButton.setEnabled(false);
                        checkButton.setTextColor(Color.parseColor("#C0C0C0"));

                    }
                }.start();
                Log.v("AsyncTask", "onPostExecute");
            }

            // 작업의 진행 상황을 보고
            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }

            // 스레드 동작
            @Override
            protected Void doInBackground(Void... voids) {
                Log.v("AsyncTask", "doInBackground Start");
                try {
                    //실제 존재하고 있는 이메일주소와 비밀번호를 넣어야 함.
                    //그 이메일로 로그인을 해서 비밀번호를 보냄.
                    gMailSender = new GMailSender("52dreamapp@gmail.com", "dhdlemfla");

                    code = gMailSender.getEmailCode();

                    //GMailSender.sendMail(제목, 본문내용, 받는사람);
                    gMailSender.sendMail("52Dream앱 인증번호입니다!", "인증코드는 " + code+ "입니다!" , email);

                    Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
                } catch (SendFailedException e) {
                    Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                } catch (MessagingException e) {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.v("AsyncTask", "doInBackground End");
                return null;
            }
        }

        LoadingAsyncTask loadingAsyncTask = new LoadingAsyncTask();
        loadingAsyncTask.execute();

    }

    //회원가입 정보를 쉐어드에 넣기
    private void putProfileDateinSharedPreferences(Profile profile){
        SharedPreferences preferences = getSharedPreferences("profiles", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Log.v("사진", "쉐어드에 사진 넣는 경로 : "+ profile.getImageUrl());

        String value = profile.getImageUrl()+"★"
                        +profile.getEmail()+"★"
                        +profile.getPassword()+"★"
                        +profile.getName()+"★"
                        +profile.getVillage()+"★"
                        +profile.getAgeNum()+"★"
                        +profile.getUid();

        editor.putString(profile.getUid(), value);
        editor.apply();

    }

}

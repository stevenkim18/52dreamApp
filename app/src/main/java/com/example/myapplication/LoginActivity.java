package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {


    //뷰를 담을 변수 선언
    Button login_button, join_button;
    EditText id_editText, password_editText;
    CheckBox id_save_CheckBox;

    SharedPreferences preferences;

    //파이어베이스 인증
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 로그인 버튼
        login_button = (Button) findViewById(R.id.login_button);
        // 회원가입 버튼
        join_button = (Button) findViewById(R.id.join_button);
        // 아아디 EditText
        id_editText = (EditText) findViewById(R.id.id_editText);
        // 비밀번호 EditText
        password_editText = (EditText) findViewById(R.id.password_editText);
        // 아이디 저장 CheckBox
        id_save_CheckBox = findViewById(R.id.id_save_checkBox);


        //파이어베이스 인증
        mAuth = FirebaseAuth.getInstance();

        // 아이디 저장이 체크여부를 쉐어드에서 불러옴
        preferences = getSharedPreferences("login", MODE_PRIVATE);

        id_save_CheckBox.setChecked(preferences.getBoolean("isIdSaved", false));

        if(id_save_CheckBox.isChecked()){
            id_editText.setText(preferences.getString("savedId", null));
        }

        //아이디 저장 CheckBox 클릭시
        id_save_CheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //체크 되어 있을 때
                if(id_save_CheckBox.isChecked()){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isIdSaved", true);
                    editor.putString("savedId", id_editText.getText().toString());
                    editor.apply();
                }
                else{
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isIdSaved", false);
                    editor.remove("savedId");
                    editor.apply();
                }
            }
        });


        // 로그인 버튼 눌렀을 때
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String email = id_editText.getText().toString();
//                String password = password_editText.getText().toString();
//
//                fireBaseLoginWhileLoading(email, password);
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.loading_dialog, null);
                final AlertDialog dialog;

                builder.setView(view);
                dialog = builder.create();
                dialog.show();

                mAuth.signInWithEmailAndPassword(id_editText.getText().toString(), password_editText.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // 로그인 성공 했을 때
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this,"로그인 성공", Toast.LENGTH_SHORT).show();

                                    //아이디 저장 CheckBox 체크시 쉐어드에 아이디 저장
                                    SharedPreferences.Editor editor = preferences.edit();
                                    //true 일때 쉐어드에 아이디 저장
                                    if(preferences.getBoolean("isIdSaved", false)){
                                        editor.putString("savedId", id_editText.getText().toString());
                                        editor.apply();
                                    }
                                    //false 일때 쉐어드에 아이디 삭제
                                    else{
                                        editor.remove("savedId");
                                        editor.apply();
                                    }

                                    dialog.dismiss();

                                    Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(loginIntent);

                                   // LoginActivity Destory
                                    finish();

                                }
                                // 로그인 실패 했을 때
                                else {
                                    dialog.dismiss();

                                    Toast.makeText(LoginActivity.this,"회원정보가 일치하지 않거나 없습니다!", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

            }
        });

        // 회원가입 버튼 눌렀을 때
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //인텐트로 로그인 화면 --> 회원가입 화면으로 전환
                Intent joinIntent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivityForResult(joinIntent, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                id_editText.setText(data.getStringExtra("email"));
            }
        }
    }


    private void fireBaseLoginWhileLoading(final String email, final String password){

        class LoadingAsyncTask extends AsyncTask<Void, Void, Void> {

            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.loading_dialog, null);
            AlertDialog dialog;

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

                // 로그인이 완료 되면 메인 화면으로 넘어감!
                Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(loginIntent);

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
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // 로그인 성공 했을 때
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this,"로그인 성공", Toast.LENGTH_SHORT).show();

                                    //아이디 저장 CheckBox 체크시 쉐어드에 아이디 저장
                                    SharedPreferences.Editor editor = preferences.edit();
                                    //true 일때 쉐어드에 아이디 저장
                                    if(preferences.getBoolean("isIdSaved", false)){
                                        editor.putString("savedId", id_editText.getText().toString());
                                        editor.apply();
                                    }
                                    //false 일때 쉐어드에 아이디 삭제
                                    else{
                                        editor.remove("savedId");
                                        editor.apply();
                                    }

                                    // LoginActivity Destory
                                    finish();

                                }
                                // 로그인 실패 했을 때
                                else {
                                    Toast.makeText(LoginActivity.this,"회원정보가 일치하지 않거나 없습니다!", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

                Log.v("AsyncTask", "doInBackground End");
                return null;
            }
        }

        LoadingAsyncTask loadingAsyncTask = new LoadingAsyncTask();
        loadingAsyncTask.execute();

    }

}


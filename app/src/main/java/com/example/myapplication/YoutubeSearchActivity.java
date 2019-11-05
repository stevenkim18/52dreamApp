package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class YoutubeSearchActivity extends AppCompatActivity {

    private static String GOOGLE_YOUTUBE_API_KEY = "AIzaSyBaLJIGZQO2Y-Lm4UTFdE9Eq5v6Pv2D9wQ";
    private static String CHANNEL_ID = "UCPewLyXdoFRMybAVp40YTkA";
    private static String CHANNEL_GET_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&channelId="+CHANNEL_ID+"&maxResults=20&key="+GOOGLE_YOUTUBE_API_KEY+"";

    private ArrayList<YoutubeDataModel> dataModels = new ArrayList<>();

    RecyclerView recyclerView;
    YoutubeDataAdapter youtubeDataAdapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_search);

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerVIew);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(YoutubeSearchActivity.this, 1));

        new RequestYoutubeAPI().execute();

        // 툴바 설정
        setSupportActionBar(toolbar);
        // 툴바 왼쪽 버튼 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);

    }

    // 유튜브에서 데이터를 가지고 오는 AsyncTask
    private class RequestYoutubeAPI extends AsyncTask<Void, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if(response != null){
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    Log.v("response", jsonObject.toString());
                    dataModels = getDataFromJson(jsonObject);

                    Log.v("리스트", "데이터 모델 사이즈: "+dataModels.size() + "");

                    youtubeDataAdapter = new YoutubeDataAdapter(dataModels, getApplicationContext());
                    recyclerView.setAdapter(youtubeDataAdapter);
                    youtubeDataAdapter.notifyDataSetChanged();

                    //16.
                    // RecyclerView 아이템을 클릭 했을 시
                    youtubeDataAdapter.setOnItemClickListener(new YoutubeDataAdapter.OnItemClickListener() {
                        @Override
                        public void OnItemClick(View v, int position) {
                            String selectedVideoId = dataModels.get(position).getVideoId();

                            Log.v("클릭", "아이템" + position);
                            Intent intent = new Intent(YoutubeSearchActivity.this, YoutubeActivity.class);
                            intent.putExtra("videoId", selectedVideoId);
                            startActivity(intent);

                        }
                    });

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }

        }

        // json 파일을 가지고 옴.
        @Override
        protected String doInBackground(Void... voids) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(CHANNEL_GET_URL);
            Log.v("URL", CHANNEL_GET_URL);

            try{
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                String json = EntityUtils.toString(httpEntity);
                return json;
            }
            catch (IOException e){
                e.printStackTrace();
            }

            return null;
        }
    }

    // 받아온 json 파일에서 원하는 데이터를 추출해서 ArrayList에 넣기
    private ArrayList<YoutubeDataModel> getDataFromJson(JSONObject jsonObject){
        ArrayList<YoutubeDataModel> mList = new ArrayList<>();

        //json 파일에 20개의 동영상이 items라는 리스트 안에 들어 있음.
        if(jsonObject.has("items")){
            try{
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for(int i = 0; i< jsonArray.length(); i++){

                    JSONObject json = jsonArray.getJSONObject(i);

                    if(json.has("id")){
                        JSONObject jsonID = json.getJSONObject("id");

                        if(jsonID.has("kind")){

                            if(jsonID.getString("kind").equals("youtube#video")){
                                String videoId = jsonID.getString("videoId"); // 동영상 id

                                JSONObject jsonSnippet = json.getJSONObject("snippet");
                                String title = jsonSnippet.getString("title"); // 동영상 제목
                                String thumbnail = jsonSnippet.getJSONObject("thumbnails").getJSONObject("high").getString("url"); // 동영상 썸네일

                                YoutubeDataModel youtubeDataModel = new YoutubeDataModel();
                                youtubeDataModel.setThumbnail(thumbnail);
                                youtubeDataModel.setTitle(title);
                                youtubeDataModel.setVideoId(videoId);

                                Log.v("json 데이터 추출", "썸네일 : " +youtubeDataModel.getThumbnail());
                                Log.v("json 데이터 추출", "제목 : " + youtubeDataModel.getTitle());
                                Log.v("json 데이터 추출", "비디오ID : " + youtubeDataModel.getVideoId());

                                mList.add(youtubeDataModel);

                            }
                        }


                    }

                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return mList;
    }
}
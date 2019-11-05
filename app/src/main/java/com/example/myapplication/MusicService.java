package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class MusicService extends Service {

    MediaPlayer mediaPlayer;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        Log.v("서비스", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean message = intent.getExtras().getBoolean("isPlay", false);
        Log.v("서비스", "onStartCommand");

        if(message){
            Log.v("서비스", "음악시작");
            mediaPlayer = MediaPlayer.create(MusicService.this, R.raw.imcallingyou);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                }
            });

        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

package com.example.user.mixtapeupload;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;

import java.io.IOException;

import static android.media.AudioManager.STREAM_MUSIC;

public class MyBackgroundService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener{

    MediaController mediaController;
    MediaPlayer mediaPlayer = new MediaPlayer();
    int notification_ID = 1;
    String musicurl,TAG="service report";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: music player service!");
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.reset();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificaiton_ok();
        musicurl = intent.getExtras().getString("musicLink");
        mediaPlayer.reset();
        if (mediaPlayer.isPlaying()){
            try {
                Log.d(TAG, "onStartCommand: "+musicurl);
                mediaPlayer.setDataSource(musicurl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return START_STICKY;
    }

    private void notificaiton_ok(){
        //notification code
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            stopSelf();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (!mediaPlayer.isPlaying()){
            Log.d(TAG, "onPrepared: "+musicurl);
            mediaPlayer.start();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        switch (i){
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK :
                Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onError: "+"Something went wrong!");
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(getApplicationContext(),"Server died! :(!",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onError: "+"Something went wrong!");
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Toast.makeText(getApplicationContext(),"Server timed out! :(!",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onError: "+"Something went wrong!");
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(getApplicationContext(),"Unknown media! :(!",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onError: "+"Something went wrong!");
                break;
        }
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }
}

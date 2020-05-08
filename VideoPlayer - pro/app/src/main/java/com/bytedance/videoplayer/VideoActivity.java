package com.bytedance.videoplayer;

import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.logging.LogRecord;

public class VideoActivity extends AppCompatActivity {

    private static final int DELAY = 500;
    private static final int INIT = 0;
    private static final int UPDATE = 1;
    private static final int PAUSE = 2;
    private static final int MILPERHUR = 3600000;
    private static final int MILPERMIN = 60000;
    private static final int MILPERSEC = 1000;

    private static String TAG = "wyl";
    private SeekBar seekBar;
    private VideoView videoView;
    private TextView tv;
    private ViewGroup.LayoutParams mVideoViewLayoutParams;
    private int length = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Log.d(TAG, "create");
        seekBar = findViewById(R.id.seekBar);
        videoView = findViewById(R.id.videoView);
        //videoView.setVideoPath(getVideoPath(R.raw.bytedance));
        tv = findViewById(R.id.textView2);
        Uri uri = getIntent().getData();
        if(uri!=null) {
            videoView.setVideoURI(uri);
            Log.d(TAG, "uri: " + uri.toString());
        }
        else
            tv.setText("未开始");
        Button pauseBtn = findViewById(R.id.pauseButton);
        Button playBtn = findViewById(R.id.playButton);




        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();

        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            getSupportActionBar().show();
        }

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.pause();//暂停
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.start();//开始
                handler.sendEmptyMessage(UPDATE);//发送message
                Log.d(TAG, "send update message");
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    videoView.seekTo(length * progress / seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                length = videoView.getDuration();
                tv.setText(millis2string(videoView.getCurrentPosition()) + "/" + millis2string(length));
            }
        });


    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE:
                    try {
                        seekBar.setProgress(videoView.getCurrentPosition() * seekBar.getMax() / videoView.getDuration());
                        tv.setText(millis2string(videoView.getCurrentPosition()) + "/" + millis2string(length));
                        Log.d(TAG, "current position is " + videoView.getCurrentPosition());
                        Log.d(TAG, "duration is " + videoView.getDuration());
                        Log.d(TAG, "progress changed,is " + seekBar.getProgress());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sendEmptyMessageDelayed(UPDATE, DELAY);
                    break;
            }
        }
    };

    private String getVideoPath(int resId) {
        return "android.resource://" + this.getPackageName() + "/" + resId;
    }

    private String millis2string(int millis) {

        String hour = String.valueOf(millis / MILPERHUR);//小时数
        String min = String.valueOf((millis % MILPERHUR) / (MILPERMIN));//分钟数
        String sec = String.valueOf((millis % MILPERHUR % MILPERMIN) / MILPERSEC);//秒数

        if (millis / MILPERHUR != 0)//大于等于一小时
            return hour + ":" + min + ":" + sec;
        else
            return min + ":" + sec;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
            ((RelativeLayout)videoView.getParent()).setBackgroundColor(Color.BLACK);

        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            getSupportActionBar().show();
            ((RelativeLayout)videoView.getParent()).setBackgroundColor(Color.WHITE);
        }

    }

}

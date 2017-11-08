package com.wqj.selectfileandeditimage.selectpicture;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.wqj.selectfileandeditimage.R;
import com.wqj.selectfileandeditimage.selectpicture.utils.Utils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PlayActivity extends Activity {

    private final String TAG = "PlayActivity";
    private SurfaceView sfv;
    private String path, name;
    private SurfaceHolder sfh;
    private MediaPlayer mediaPlayer;
    private TextView tv_play_name, tv_max, tv_progress;
    private ImageView iv_play;
    private SeekBar seekBar;
    private int seek;
    private Thread thread;
    private int jindu, changdu;
    private boolean stopThread;
    private Timer timer;
    private TimerTask task;
    private int nx = 1;// 1 正在播放 0已暂停

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // 设置全屏
        // 隐藏状态栏（电池栏）
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 设置背光常亮
        this.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 隐藏标题栏（必须在view视图完成之前，否则会报错）
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 强制横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.play_view);
        init();

        thread = new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (!stopThread) {
                    long start = System.currentTimeMillis();
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        jindu = mediaPlayer.getCurrentPosition();
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                tv_progress.setText(Utils.getTimeFormat(jindu));
                                seekBar.setProgress(jindu);
                            }
                        });
                    }
                    long end = System.currentTimeMillis();
                    if (end - start < 1000) {
                        try {
                            Thread.sleep(1000 - (end - start));//休眠一秒
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    private void init() {
        // TODO Auto-generated method stub
        seek = 0;
        stopThread = false;
        timer = new Timer();
        seekBar = (SeekBar) findViewById(R.id.seek_jd);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        tv_max = (TextView) findViewById(R.id.tv_playMax);
        tv_progress = (TextView) findViewById(R.id.tv_playProgress);
        sfv = (SurfaceView) findViewById(R.id.sfv);
        tv_play_name = (TextView) findViewById(R.id.tv_play_name);

        sfh = sfv.getHolder();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        path = intent.getStringExtra("path");
        Log.i(TAG, "视频path=" + path);

        tv_play_name.setText(name);
        sfh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        sfh.addCallback(new Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void surfaceCreated(SurfaceHolder arg0) {
                // TODO Auto-generated method stub
                // Paint paint = new Paint();
                // paint.setColor(Color.RED);
                // Canvas canvas = sfh.lockCanvas();
                // canvas.drawText("测试1", 50, 50, 50, 50, paint);
                playVedio();
                changdu = mediaPlayer.getDuration();
                Log.i(TAG, "长度：" + changdu);
                if (changdu < 86400000) {
                    seekBar.setMax(changdu);
                    tv_max.setText(Utils.getTimeFormat(changdu));
                    thread.start();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
                                       int arg3) {
                // TODO Auto-generated method stub
            }
        });

        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekbar) {
                // TODO Auto-generated method stub
                seek = seekbar.getProgress();
                seekTo(seekbar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekbar, int arg1,
                                          boolean arg2) {
                // TODO Auto-generated method stub
                tv_progress.setText(Utils.getTimeFormat(seekbar.getProgress()));
            }
        });

        iv_play.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (nx == 0) {
                    continueToPlay();
                    nx = 1;
                } else if (nx == 1) {
                    pause();
                    nx = 0;
                }
            }
        });

        startTask();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            tv_play_name.setVisibility(View.VISIBLE);
            tv_max.setVisibility(View.VISIBLE);
            tv_progress.setVisibility(View.VISIBLE);
            iv_play.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.VISIBLE);
            startTask();
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        stopThread = true;
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    // 暂停
    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            Log.i(TAG, "暂停" + seek);

            mediaPlayer.pause();
        }
        iv_play.setImageResource(R.drawable.play);
    }

    // 继续1
    private void continueToPlay() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            Log.i(TAG, "继续：" + seek);
            mediaPlayer.start();
        }
        iv_play.setImageResource(R.drawable.pause);
    }

    // 跳转
    private void seekTo(int progress) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(progress);
            seek = progress;
        }
    }

    // 播放
    private void playVedio() {
        iv_play.setImageResource(R.drawable.pause);
        mediaPlayer = new MediaPlayer();
        // 设置音频格式
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            // 一首播放完毕后执行
            @Override
            public void onCompletion(MediaPlayer arg0) {
                // TODO Auto-generated method stub
                iv_play.setImageResource(R.drawable.play);
            }
        });
        // 初始化
        mediaPlayer.reset();
        // 设置数据源
        try {
            mediaPlayer.setDataSource(path);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            Log.i(TAG, "IllegalArgumentException" + e.getMessage());
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            Log.i(TAG, "SecurityException" + e.getMessage());
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            Log.i(TAG, "IllegalStateException" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.i(TAG, "IOException" + e.getMessage());
            e.printStackTrace();
        }
        // 设置视频
        mediaPlayer.setDisplay(sfh);
        // 设置缓冲
        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            showToast("网速太差了，换个网好的地方再试吧");
            e.printStackTrace();
        }
        mediaPlayer.start();

    }

    private void showToast(String prompt) {
        Toast.makeText(this, prompt, Toast.LENGTH_LONG).show();
    }

    private void startTask() {
        if (task != null) {
            task.cancel(); // 将原任务从队列中移除
        }
        task = new Task();
        timer.schedule(task, 5000);
    }

    class Task extends TimerTask {

        @Override
        public void run() {
            // TODO Auto-generated method stub

            runOnUiThread(new Runnable() {
                public void run() {
                    PlayActivity.this.tv_play_name.setVisibility(View.GONE);
                    PlayActivity.this.tv_max.setVisibility(View.GONE);
                    PlayActivity.this.tv_progress.setVisibility(View.GONE);
                    PlayActivity.this.iv_play.setVisibility(View.GONE);
                    PlayActivity.this.seekBar.setVisibility(View.GONE);
                }
            });
        }
    }

}

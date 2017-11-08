package com.wqj.selectfileandeditimage.selectpicture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.wqj.selectfileandeditimage.R;
import com.wqj.selectfileandeditimage.selectpicture.utils.Utils;
import com.wqj.selectfileandeditimage.selectpicture.utils.VoiceUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


public class AudioRecorderActivity extends AppCompatActivity {


    int i = 0;//计时
    private RelativeLayout rlBack;
    private TextView tvToolbarText;
    private RelativeLayout rlSelectItem;
    private Toolbar toolbar;
    private TextView tvTime;
    private ImageView ivVoiceRecorder;
    private TextView tvIsRecorder;
    private LinearLayout llAudioRecorder;
    private RelativeLayout activityAudioEncoder;
    private Timer timer;
    private TimerTask task;
    private VoiceUtils voiceUtils;
    private int isRecorder = 0;//0没有录音，1正在录音

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);
        initUI();
        setSupportActionBar(toolbar);
        tvToolbarText.setText(getResources().getString(R.string.audio_recorder));
        voiceUtils = new VoiceUtils();
        llAudioRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecorder == 0) {
                    isRecorder = 1;
                    tvIsRecorder.setText(getResources().getString(R.string.finish));
                    ivVoiceRecorder.setImageResource(R.mipmap.luyin_finish);
                    voiceUtils.startRecorder();
                    startTask();
                } else {
                    isRecorder = 0;
                    tvIsRecorder.setText(getResources().getString(R.string.audio_recorder));
                    ivVoiceRecorder.setImageResource(R.mipmap.luyin_kaishi);
                    stopTask();
                    File fileVoice = voiceUtils.stopRecorder();
                    Intent intent = new Intent();
                    intent.putExtra("voice", fileVoice);
                    setResult(Activity.RESULT_OK, intent);
                    AudioRecorderActivity.this.finish();
                }
            }
        });

        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        voiceUtils.stopRecorder();
        super.onBackPressed();
    }

    private void startTask() {
        i = 0;
        timer = new Timer();
        task = new Task();
        timer.schedule(task, 1000, 1000);// 开始3秒后执行，之后每隔4秒执行一次
    }

    private void stopTask() {
        timer.cancel();
    }

    public <T extends View> T $(@IdRes int resId) {
        return (T) super.findViewById(resId);
    }

    private void initUI() {
        rlBack = $(R.id.rl_back);
        tvToolbarText = $(R.id.tv_toolbarText);
        rlSelectItem = $(R.id.rl_selectItem);
        toolbar = $(R.id.toolbar);
        tvTime = $(R.id.tv_time);
        ivVoiceRecorder = $(R.id.iv_voiceRecorder);
        tvIsRecorder = $(R.id.tv_isRecorder);
        llAudioRecorder = $(R.id.ll_audioRecorder);
        activityAudioEncoder = $(R.id.activity_audio_encoder);
    }

    class Task extends TimerTask {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {
                public void run() {
                    i++;
                    tvTime.setText(Utils.getTimeFormatByS(i));
                }
            });
        }
    }

}

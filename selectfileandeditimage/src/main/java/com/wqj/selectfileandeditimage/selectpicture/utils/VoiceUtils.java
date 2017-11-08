package com.wqj.selectfileandeditimage.selectpicture.utils;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import com.wqj.selectfileandeditimage.selectpicture.OnVoiceCompletionListener;

import java.io.File;
import java.io.IOException;

/**
 * Created by snsoft on 2016/8/16.
 * 录音、播放录音的工具类
 */
public class VoiceUtils {

    private final String TAG = "VoiceUtils";
    /**
     * MediaRecorder mediaRecorder = new MediaRecorder();
     * // 第1步：设置音频来源（MIC表示麦克风）
     * mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
     * //第2步：设置音频输出格式（默认的输出格式）
     * mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
     * //第3步：设置音频编码方式（默认的编码方式）
     * mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
     * //创建一个临时的音频输出文件
     * audioFile = File.createTempFile("record_", ".amr");
     * //第4步：指定音频输出文件
     * mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
     * //第5步：调用prepare方法
     * mediaRecorder.prepare();
     * //第6步：调用start方法开始录音
     * mediaRecorder.start();
     */
    private MediaPlayer myPlayer;
    private MediaRecorder myRecorder;
    private File file;
    private OnVoiceCompletionListener onVoiceCompletionListener = null;
    private boolean isPlaying;


    public void startRecorder() {
        file = null;
        myRecorder = new MediaRecorder();
        // 从麦克风源进行录音
        myRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        // 设置输出格式
        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        // 设置编码格式
        myRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        String path = Constant.voicePath + File.separator + System.currentTimeMillis() + ".amr";
        file = new File(path);

        myRecorder.setOutputFile(path);
        try {
            File fileFolder = new File(Constant.voicePath);
            if (!fileFolder.exists()) {
                fileFolder.mkdirs();
            }

            file.createNewFile();
            myRecorder.prepare();
            myRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 保存录音，释放资源
     */
    public File stopRecorder() {
        if (myRecorder != null) {
            myRecorder.stop();
            myRecorder.release();
        }
        return file;
    }

    public int getLength(String voicePath) {
        int length = 0;
        myPlayer = new MediaPlayer();
        try {
            myPlayer.setDataSource(voicePath);
            myPlayer.prepare();
//            myPlayer.start();
            length = myPlayer.getDuration() / 1000;
            Log.i(TAG, "录音时长=" + length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        myPlayer.release();
        myPlayer = null;
        return length;
    }

    public void playerVoice(String voicePath) {

        myPlayer = new MediaPlayer();
        try {
            myPlayer.setDataSource(voicePath);
            myPlayer.prepare();
            myPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        myPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            // 播放完毕后执行
            @Override
            public void onCompletion(MediaPlayer arg0) {
                // TODO Auto-generated method stub
                myPlayer.release();
                myPlayer = null;
                isPlaying = false;
                if (onVoiceCompletionListener != null) {
                    onVoiceCompletionListener.onVoiceComletion(isPlaying);
                }

            }
        });
    }

    //停止播放，释放资源
    public void stopPlayVoice() {
        if (myPlayer != null) {
            myPlayer.stop();
            myPlayer.release();
            myPlayer = null;
        }
    }

    public void setOnComletionListener(OnVoiceCompletionListener onVoiceCompletionListener) {
        this.onVoiceCompletionListener = onVoiceCompletionListener;
    }


}

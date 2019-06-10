package com.example.mdkfastprintingapp.voice;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.mdkfastprintingapp.utils.logs;

import java.util.concurrent.TimeoutException;

/**
 * 类说明：语音提示Uilt
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/05/10
 */
public class VoicePromptUtils {
    private static MediaPlayer mediaPlayer = null;
    static final String TAG = "VoicePromptUtils log";

    public static void onPlay(Context context,int raw){
        if (mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(context,raw);
            if (!mediaPlayer.isPlaying() && mediaPlayer != null){
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                });
            }
        }else {
            new TimeoutException("The object created is not empty --MediaPlayer no null");
            logs.d(TAG,"mediaPlayer对象不为空，未能创建成功");
        }
    }
}

package com.olex.audioandvideo;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    // UI
    private VideoView videoView;
    private Button btn, playMusic, pauseMusic;
    private SeekBar seekBar, moveSeekBar;

    private MediaController mediaController;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.dorest);

        // get UI
        videoView = findViewById(R.id.vW);
        btn = findViewById(R.id.button);
        playMusic = findViewById(R.id.buttonPlayMusic);
        pauseMusic = findViewById(R.id.buttonPauseMusic);
        seekBar = findViewById(R.id.seekBar);
        moveSeekBar = findViewById(R.id.moveSeekBar);

        //Create objects
        mediaController = new MediaController(MainActivity.this);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bensound_ukulele);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Setup video
        videoView.setVideoURI(videoUri);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        // Add events listener
        btn.setOnClickListener(MainActivity.this);
        playMusic.setOnClickListener(MainActivity.this);
        pauseMusic.setOnClickListener(MainActivity.this);
        mediaPlayer.setOnCompletionListener(this);

        // Setup move seekBar
        moveSeekBar.setOnSeekBarChangeListener(MainActivity.this);
        moveSeekBar.setMax(mediaPlayer.getDuration());

        // Setup volume seekBar
        int maxValue = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBar.setMax(maxValue);
        seekBar.setProgress(currentValue);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    public void onClick(View buttonView) {
        switch (buttonView.getId()) {
            case R.id.button:
                videoView.start();
                break;
            case R.id.buttonPlayMusic:
                mediaPlayer.start();
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        moveSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }, 0, 1000);
                break;
            case R.id.buttonPauseMusic:
                mediaPlayer.pause();
                timer.cancel();
                break;
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mediaPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mediaPlayer.pause();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mediaPlayer.start();
    }

    // Media player event
    @Override
    public void onCompletion(MediaPlayer mp) {
        timer.cancel();
        moveSeekBar.setProgress(0);
    }
}

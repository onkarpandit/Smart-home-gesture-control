package com.example.smarthomegesturecontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.smarthomegesturecontrol.permissions.OnPermission;
import com.example.smarthomegesturecontrol.permissions.Permission;
import com.example.smarthomegesturecontrol.permissions.XXPermissions;

import java.util.List;

public class GestureView extends AppCompatActivity {
    VideoView videoPlayer;
    Button replayButton;
    Button practiceButton;
    String gestureSelected;
    String GESTURE_TO_PLAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_view);

        videoPlayer = findViewById(R.id.gestureVideo);
        replayButton = findViewById(R.id.replayButton);
        practiceButton = findViewById(R.id.practiceButton);

        Intent intent = getIntent();
        gestureSelected = intent.getStringExtra("gesture_name");
        GESTURE_TO_PLAY = "h_" + gestureSelected.replaceAll(" ", "_").toLowerCase();

        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replayVideo();
            }
        });

        practiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkMyPermission();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            videoPlayer.pause();
        }
    }

    private void initializePlayer() {
        Uri videoUri = getMedia(GESTURE_TO_PLAY);
        videoPlayer.setVideoURI(videoUri);
        videoPlayer.start();
    }

    private Uri getMedia(String mediaName) {
        return Uri.parse("android.resource://" + getPackageName() + "/raw/" + mediaName);
    }

    private void releasePlayer() {
        videoPlayer.stopPlayback();
    }

    public void replayVideo() {
        initializePlayer();
    }

    public void checkMyPermission() {
        XXPermissions.with(this)
                .permission(Permission.Group.STORAGE)
                .permission(Permission.CAMERA)
                .permission(Permission.RECORD_AUDIO)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean all) {
                        if (all) {
                            Intent practiceGestureActivityIntent = new Intent(GestureView.this, PractiveView.class);
                            practiceGestureActivityIntent.putExtra("gesture_name", gestureSelected);
                            startActivity(practiceGestureActivityIntent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Obtain permission successfully, some permissions are not granted normally", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if (quick) {
                            Toast.makeText(getApplicationContext(), "Permanently denied permission. Please grant permission manually", Toast.LENGTH_SHORT).show();

                            XXPermissions.gotoPermissionSettings(GestureView.this);
                        } else {
                            Toast.makeText(getApplicationContext(), "You can't play and record video without permission", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
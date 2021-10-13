package com.example.smarthomegesturecontrol;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PractiveView extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2000;
    public static final int VIDEO_REQUEST_CODE = 99;
    public static final String DIR = "/Download/CapturedVideos/";
    private String gestureFinal ;
    private ArrayList<String> videoPathList = new ArrayList<>();
    VideoView videoView;
    Uri videoUri;
    String gestureFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practive_view);

        gestureFinal = (String)getIntent().getExtras().get("gesture_name");
        gestureFileName = gestureFinal.replaceAll("\\s","_");
        videoView = findViewById(R.id.videoView2);

        File dir = new File(Environment.getExternalStorageDirectory() + DIR);
        if(!dir.exists()) {
            dir.mkdirs();
        }

        Button startVideoRecord = findViewById(R.id.startVideoCapture);
        Button uploadVideo = findViewById(R.id.uploadVideo);

        startVideoRecord.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                startVideoCapture();
            }
        });

        uploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadVideos();
            }
        });

        TextView textView4 = (TextView) findViewById(R.id.textView4);
        textView4.setText(videoPathList.size()+"/3");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startVideoCapture() {
        if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {
            invokeCamera();
        } else {
            String[] permissionRequest = {Manifest.permission.CAMERA};
            requestPermissions(permissionRequest, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                invokeCamera();
            } else {
                Toast.makeText(this,"Unable to invoke camera without permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void invokeCamera() {
        File videoFile = new File(Environment.getExternalStorageDirectory()+DIR+gestureFileName+videoPathList.size()+".mp4");
        if(videoPathList.size()==3){
            //don't update list, keep updating the last video
            videoFile = new File(Environment.getExternalStorageDirectory()+DIR+gestureFileName+(videoPathList.size()-1)+".mp4");
        } else {
            videoPathList.add(Environment.getExternalStorageDirectory() + DIR + gestureFileName + videoPathList.size() + ".mp4");
        }
        videoUri = FileProvider.getUriForFile(this,getApplicationContext().getPackageName()+".provider",videoFile);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,videoUri);
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if(intent.resolveActivity(getPackageManager())!= null) {
            startActivityForResult(intent, VIDEO_REQUEST_CODE);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                TextView textView4 = (TextView) findViewById(R.id.textView4);
                textView4.setText(videoPathList.size()+"/3");
                videoView = findViewById(R.id.videoView2);
                videoView.setVideoURI(videoUri);
                MediaController mediaController = new MediaController(this);
                videoView.setMediaController(mediaController);
                videoView.start();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadVideos() {
        if (videoPathList.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please record video", Toast.LENGTH_SHORT).show();
            return ;
        }
        File file = new File(videoPathList.get(videoPathList.size()-1));

        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData(file.getName(),
                file.getName(), RequestBody.create(MediaType.parse("*/*"), file));

        ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);

        RequestBody body = RequestBody.create("file", MediaType.parse(file.getName()));

        Call<ServerResponse> call = getResponse.uploadFile(fileToUpload, body);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "Files uploaded successfully to server!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    assert serverResponse != null;
                    Log.v("Response", serverResponse.toString());
                }

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                //handle failure
                Log.d("Practice view", "onFailure: "+ t.getMessage());
            }
        });

    }
}
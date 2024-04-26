package com.example.myapplication.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.myapplication.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraViewActivity extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture;

    PreviewView previewView;
    //    TextView textView;
    private int REQUEST_CODE_PERMISSION = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[] {"android.permission.CAMERA"};
    List<String> imagenet_classes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camera_view);
        previewView = findViewById(R.id.cam_view);
//        textView = findViewById(R.id.result_txt);
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION);
        }
//        imagenet_classes = loadClasses("imagenet-classes.txt");
//        loadTorchModule("model.ptl");
        cameraProviderListenableFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderListenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();
                startCamera(cameraProvider);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, ContextCompat.getMainExecutor(this));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_camera), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private boolean checkPermission(){
        for (String per : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, per) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    Executor executor = Executors.newSingleThreadExecutor();
    void startCamera(@NonNull ProcessCameraProvider cameraProvider) {
        Preview view = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        view.setSurfaceProvider(previewView.getSurfaceProvider());
//        ImageAnalysis imageAnalysis = new ImageAnalysis
//                .Builder()
//                .setTargetResolution(new Size(224, 224))
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .build();
//        imageAnalysis.setAnalyzer(executor, image -> {
//            int rotation = image.getImageInfo().getRotationDegrees();
//            analyzeImage(image, rotation);
//            image.close();
//        });
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, view);
    }

    public void takePhoto(View view) {

    }
}
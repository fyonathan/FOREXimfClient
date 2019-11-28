package com.foreximf.quickpro.util;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.foreximf.quickpro.R;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.configuration.CameraConfiguration;
import io.fotoapparat.configuration.UpdateConfiguration;
import io.fotoapparat.log.LoggersKt;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.result.WhenDoneListener;
import io.fotoapparat.selector.FlashSelectorsKt;
import io.fotoapparat.selector.FocusModeSelectorsKt;
import io.fotoapparat.selector.LensPositionSelectorsKt;
import io.fotoapparat.selector.ResolutionSelectorsKt;
import io.fotoapparat.selector.SelectorsKt;
import io.fotoapparat.view.CameraView;

public class TakePhotoActivity extends AppCompatActivity {

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    public static final int TAKE_PHOTO = 2;

    private boolean hasCameraPermission;
    private CameraView cameraView;
    private Fotoapparat fotoapparat;
    private CameraConfiguration cameraConfiguration;
    private ImageButton flash;
    private ImageButton lens;
    private ImageButton circle;

    private int flash_mode;
    private int lens_mode;

    private int currentOrientation;
    private int currentRotation;
    private boolean isAnimate;
    OrientationEventListener orientationEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_take_photo);

        flash_mode = 1; //1. Auto, 2. Off, 3. On
        lens_mode = 2; //1. Front, 2. Back

        cameraView = findViewById(R.id.camera_view);
        flash = findViewById(R.id.flash);
        lens = findViewById(R.id.lens);
        circle = findViewById(R.id.circle);
        cameraConfiguration = CameraConfiguration.builder().photoResolution(ResolutionSelectorsKt.highestResolution())
                .focusMode(SelectorsKt.firstAvailable(
                        FocusModeSelectorsKt. continuousFocusPicture(),
                        FocusModeSelectorsKt.autoFocus(),
                        FocusModeSelectorsKt.fixed()
                )).build();

        fotoapparat = Fotoapparat
            .with(this)
            .into(cameraView)
            .previewScaleType(ScaleType.CenterCrop)
            .lensPosition(LensPositionSelectorsKt.back())
            .flash(FlashSelectorsKt.autoFlash())
            .build();
        fotoapparat.updateConfiguration(cameraConfiguration);

        flash.setOnClickListener(view -> {
            if (lens_mode == 2) {
                switch (flash_mode) {
                    case 1: {
                        fotoapparat.updateConfiguration(UpdateConfiguration.builder().flash(FlashSelectorsKt.on()).build());
                        flash.setImageResource(R.drawable.ic_flash_on);
                        flash_mode = 2;
                        break;
                    }
                    case 2: {
                        fotoapparat.updateConfiguration(UpdateConfiguration.builder().flash(FlashSelectorsKt.off()).build());
                        flash.setImageResource(R.drawable.ic_flash_off);
                        flash_mode = 3;
                        break;
                    }
                    case 3: {
                        fotoapparat.updateConfiguration(UpdateConfiguration.builder().flash(FlashSelectorsKt.autoFlash()).build());
                        flash.setImageResource(R.drawable.ic_flash_auto);
                        flash_mode = 1;
                        break;
                    }
                }
            }
        });
        lens.setOnClickListener(view -> {
            switch (lens_mode) {
                case 1: {
                    fotoapparat.switchTo(LensPositionSelectorsKt.back(), cameraConfiguration);
                    switch (flash_mode) {
                        case 1: {
                            fotoapparat.updateConfiguration(UpdateConfiguration.builder().flash(FlashSelectorsKt.autoFlash()).build());
                            break;
                        }
                        case 2: {
                            fotoapparat.updateConfiguration(UpdateConfiguration.builder().flash(FlashSelectorsKt.on()).build());
                            break;
                        }
                        case 3: {
                            fotoapparat.updateConfiguration(UpdateConfiguration.builder().flash(FlashSelectorsKt.off()).build());
                            break;
                        }
                    }
                    flash.setColorFilter(Color.parseColor("#ffffff"));
                    lens_mode = 2;
                    break;
                }
                case 2: {
                    fotoapparat.switchTo(LensPositionSelectorsKt.front(), cameraConfiguration);
                    flash.setColorFilter(Color.parseColor("#aaaaaa"));
                    lens_mode = 1;
                    break;
                }
            }
        });
        circle.setOnClickListener(view -> {
            ProgressDialog dialog = new ProgressDialog(TakePhotoActivity.this);
            dialog.setMessage("Processing...");
            dialog.show();

            PhotoResult photoResult = fotoapparat.takePicture();

            photoResult.toBitmap().whenDone(bitmapPhoto -> {
                if (bitmapPhoto == null) {
                    Toast.makeText(TakePhotoActivity.this, "Gagal mengambil gambar", Toast.LENGTH_LONG).show();
                } else {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(-bitmapPhoto.rotationDegrees);
                    Bitmap bitmap = Bitmap.createBitmap(bitmapPhoto.bitmap, 0, 0, bitmapPhoto.bitmap.getWidth(), bitmapPhoto.bitmap.getHeight(), matrix, true);
                    File outputDir = this.getFilesDir();
                    File imageFile = new File(outputDir, System.currentTimeMillis() + ".png");
                    OutputStream os;
                    try {
                        os = new FileOutputStream(imageFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                        os.flush();
                        dialog.dismiss();

                        Intent data = new Intent();
                        data.putExtra("uri", imageFile.getAbsolutePath());
                        setResult(RESULT_OK, data);
                        finish();
                    } catch (Exception e) {
                        Log.e(this.getClass().getSimpleName(), "Error writing file", e);
                    }
                }
            });
        });

        currentOrientation = 0;
        currentRotation = 0;
        orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int i) {
                int o = (360 + i) % 360;
                int orientation;
                if (o > 315 || o <= 45) {
                    orientation = 0;
                } else if (o > 45 && o <= 135) {
                    orientation = 3;
                } else if (o > 135 && o <= 225) {
                    orientation = 2;
                } else {
                    orientation = 1;
                }
                if (currentOrientation != orientation && !isAnimate) {
                    int to = currentRotation;
                    int rot = (4 + orientation - currentOrientation) % 4;
                    if (rot == 1) {
                        to += 90;
                    } else if (rot == 3) {
                        to -= 90;
                    } else if (rot == 2) {
                        if (currentRotation >= 180) {
                            to -= 180;
                        } else {
                            to += 180;
                        }
                    }
                    RotateAnimation rotate = new RotateAnimation(currentRotation, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotate.setDuration(1000);
                    rotate.setInterpolator(new LinearInterpolator());
                    rotate.setFillAfter(true);
                    rotate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            isAnimate = true;
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            isAnimate = false;
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) { }
                    });
                    flash.startAnimation(rotate);
                    lens.startAnimation(rotate);
                    currentRotation = to;
                    currentOrientation = orientation;
                }
            }
        };

        hasCameraPermission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            } else {
                hasCameraPermission = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasCameraPermission = true;
                fotoapparat.start();
            } else {
                fotoapparat.stop();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        orientationEventListener.disable();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (hasCameraPermission) {
            fotoapparat.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (hasCameraPermission) {
            fotoapparat.stop();
        }
    }
}

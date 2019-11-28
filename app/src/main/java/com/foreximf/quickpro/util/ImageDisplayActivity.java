package com.foreximf.quickpro.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.support.v7.widget.Toolbar;
import android.widget.ScrollView;

import com.foreximf.quickpro.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.InputStream;

public class ImageDisplayActivity extends AppCompatActivity {
    static PhotoView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_image_display);

        if(Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorBlack));
        }
        imageView = findViewById(R.id.image_view);
        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("image-uri");
//        imageView.setImageBitmap(ImageUtils.getImageBitmap(imageUrl));
        new DownloadImageTask().execute(imageUrl);

        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        getSupportActionBar().setTitle(intent.getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
//                Log.d("ImageDisplayActivity", "Back Button Pressed");
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private static void updateImageView(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        DownloadImageTask() {
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            updateImageView(result);
        }
    }
}

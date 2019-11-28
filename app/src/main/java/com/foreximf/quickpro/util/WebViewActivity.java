package com.foreximf.quickpro.util;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.foreximf.quickpro.R;
import com.skydoves.powermenu.CustomPowerMenu;
import com.skydoves.powermenu.MenuAnimation;

public class WebViewActivity extends AppCompatActivity implements LifecycleOwner {

    Toolbar toolbar;
    ImageButton close;
    TextView _title;
    TextView _url;
    ImageButton more;

    CustomPowerMenu customPowerMenu;
    ImageButton back;
    ImageButton forward;
    ImageButton refresh;
    boolean isLoading;
    boolean fistTime;

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorLightBlue));
        }

        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

//        toolbar.setNavigationOnClickListener(v -> {
//            onBackPressed();
//        });

//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);

        close = toolbar.findViewById(R.id.close);
        close.setOnClickListener(view -> {
            super.onBackPressed();
        });
        _title = toolbar.findViewById(R.id.title);
        _url = toolbar.findViewById(R.id.url);
        more = toolbar.findViewById(R.id.more);
        more.setOnClickListener(view -> {
            customPowerMenu.showAsAnchorRightBottom(more);
        });

        progressBar = findViewById(R.id.progress_bar);

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        fistTime = true;
        webView.loadUrl(getIntent().getStringExtra("url"));

        customPowerMenu = new CustomPowerMenu.Builder<>(WebViewActivity.this, new WebViewDialogMenuAdapter())
            .setHeaderView(R.layout.webview_more_header)
            .addItem(new WebViewMenuItem("Open Another App", R.drawable.ic_open_in_new, "Open in Other App"))
            .setLifecycleOwner(WebViewActivity.this)
            .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
            .setWidth(600)
            .setMenuRadius(10f)
            .setMenuShadow(10f)
            .build();
        back = customPowerMenu.getHeaderView().findViewById(R.id.back);
        forward = customPowerMenu.getHeaderView().findViewById(R.id.forward);
        refresh = customPowerMenu.getHeaderView().findViewById(R.id.refresh);
        back.setOnClickListener(view -> {
            if (webView.canGoBack()) {
                webView.goBack();
            }
        });
        forward.setOnClickListener(view -> {
            if (webView.canGoForward()) {
                webView.goForward();
            }
        });
        refresh.setOnClickListener(view -> {
            if (isLoading) {
                webView.stopLoading();
            } else {
                webView.reload();
            }
        });
        customPowerMenu.setOnMenuItemClickListener((position, item) -> {
            switch (((WebViewMenuItem)item).getKey()) {
                case "Open Another App": {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(webView.getUrl()));
                    startActivity(i);
                    break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            Log.d("Host", Uri.parse(url).getHost());
//            if (Uri.parse(url).getHost().equals("https://client.foreximf.com")) {
//                // This is my website, so do not override; let my WebView load the page
//                return false;
//            }
            return false;
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            startActivity(intent);
//            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            _title.setText(view.getTitle());
            _url.setText(url);
            if (webView.canGoBack()) {
                back.setColorFilter(Color.parseColor("#777777"));
            } else {
                back.setColorFilter(Color.parseColor("#cccccc"));
            }
            if (webView.canGoForward()) {
                forward.setColorFilter(Color.parseColor("#777777"));
            } else {
                forward.setColorFilter(Color.parseColor("#cccccc"));
            }
            isLoading = false;
            refresh.setImageResource(R.drawable.ic_refresh);
            refresh.setColorFilter(Color.parseColor("#777777"));
            if (fistTime) {
                fistTime = false;
                webView.clearHistory();
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setProgress(0);
            _title.setText(view.getTitle());
            _url.setText(url);
            isLoading = true;
            refresh.setImageResource(R.drawable.ic_clear);
            refresh.setColorFilter(Color.parseColor("#ff4444"));
        }
    }
}

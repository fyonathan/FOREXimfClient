package com.foreximf.quickpro.signal;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.foreximf.quickpro.LoginActivity;
import com.foreximf.quickpro.MainActivity;
import com.foreximf.quickpro.R;
import com.foreximf.quickpro.util.ArchLifecycleApp;
import com.foreximf.quickpro.util.DateFormatter;
import com.foreximf.quickpro.util.F;
import com.foreximf.quickpro.util.ImageDisplayActivity;
import com.foreximf.quickpro.util.ImageUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple class used to display the detail of
 * signal, called when an item in recycler view
 * in {@link SignalFragment} is clicked.
 */
public class SignalDetailActivity extends AppCompatActivity {

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorLightBlue));
        }
        Intent intent = getIntent();
        final Signal signal = getExtra(intent);

        ArchLifecycleApp application = (ArchLifecycleApp) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Detail Signal" + signal.getTitle());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String url = "https://client.foreximf.com/api-track";
        Map<String, String> jparams = new HashMap<>();
        jparams.put("signal", ""+signal.getServerId());
        jparams.put("token", preferences.getString("login-token", ""));
        F.JSONRequest(this, url, jparams, new F.JSONRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
            }

            @Override
            public void onError(VolleyError error) {
            }
        });

        TextView title = findViewById(R.id.signal_detail_date);
        String dateString = DateFormatter.format(signal.getCreatedTime(), "dd/MM/yyyy HH:mm") + " WIB";
        title.setText(dateString);
        TextView body = findViewById(R.id.signal_detail_body);
        if(signal.getContent() != null) {
            Spannable html = ImageUtils.getSpannableHtmlWithImageGetter(body, signal.getContent());
            final Activity activity = this;
            ImageUtils.setClickListenerOnHtmlImageGetter(html, imageUrl -> {
                Intent intent1 = new Intent(activity, ImageDisplayActivity.class);
                intent1.putExtra("image-uri", imageUrl);
                intent1.putExtra("title", signal.getTitle());
                if(Build.VERSION.SDK_INT >= 21) {
                    startActivity(intent1, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                }
            });
            body.setText(html);
            body.setMovementMethod(LinkMovementMethod.getInstance());
        }
//        body.setText(signal.getContent());
        TextView result = findViewById(R.id.signal_detail_result);
        if(signal.getStatus() == 4) {
            result.setText(new StringBuilder("Hasil Trading : ").append(signal.getResult()).append(" pips"));
            TextView keterangan = findViewById(R.id.signal_detail_keterangan);
            keterangan.setText(signal.getKeterangan());
        }

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
//        toolbar.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        getSupportActionBar().setTitle(signal.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
//            Log.d("ImageDisplayActivity", "Back Button Pressed");
        });
    }

    private Signal getExtra(Intent intent) {
        Signal item = new Signal();
        item.setServerId(intent.getIntExtra("server-id", 0));
        item.setTitle(intent.getStringExtra("title"));
        item.setContent(intent.getStringExtra("content"));
        Date date = new Date();
        date.setTime(intent.getLongExtra("last-update", 0));
        item.setCreatedTime(date);
        item.setCurrencyPair(intent.getIntExtra("currency-pair", 0));
        item.setResult(intent.getIntExtra("result", 0));
        item.setStatus(intent.getIntExtra("status", 0));
        item.setSignalGroup(intent.getIntExtra("group", 0));
        item.setOrderType(intent.getIntExtra("order-type", 0));
        item.setKeterangan(intent.getStringExtra("keterangan"));
        return item;
    }
}

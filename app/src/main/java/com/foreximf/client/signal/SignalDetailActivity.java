package com.foreximf.client.signal;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.foreximf.client.R;
import com.foreximf.client.util.DateFormatter;
import com.foreximf.client.util.ImageDisplayActivity;
import com.foreximf.client.util.ImageUtils;

import java.util.Date;

/**
 * A simple class used to display the detail of
 * signal, called when an item in recycler view
 * in {@link SignalFragment} is clicked.
 */
public class SignalDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal_detail);
        Intent intent = getIntent();
        final Signal signal = getExtra(intent);
        TextView title = findViewById(R.id.signal_detail_date);
        String dateString = DateFormatter.format(signal.getLastUpdate(), "dd/MM/yyyy HH:mm") + " WIB";
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
        if(signal.getStatus() == 3) {
            result.setText(new StringBuilder("Hasil Trading : ").append(signal.getResult()).append(" pips"));
        }

        Toolbar toolbar = findViewById(R.id.toolbar_main);
//        toolbar.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        getSupportActionBar().setTitle(signal.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Log.d("ImageDisplayActivity", "Back Button Pressed");
            }
        });
    }

    private Signal getExtra(Intent intent) {
        Signal item = new Signal();
        item.setTitle(intent.getStringExtra("title"));
        item.setContent(intent.getStringExtra("content"));
        Date date = new Date();
        date.setTime(intent.getLongExtra("last-update", 0));
        item.setLastUpdate(date);
        item.setCurrencyPair(intent.getIntExtra("currency-pair", 0));
        item.setResult(intent.getIntExtra("result", 0));
        item.setStatus(intent.getIntExtra("status", 0));
        item.setSignalGroup(intent.getIntExtra("group", 0));
        item.setOrderType(intent.getIntExtra("order-type", 0));
        return item;
    }
}

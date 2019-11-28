package com.foreximf.quickpro.news;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.foreximf.quickpro.R;
import com.foreximf.quickpro.util.DateConverter;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class NewsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        TextView title = findViewById(R.id.news_detail_title);
        title.setText(getIntent().getStringExtra("title"));

        TextView content = findViewById(R.id.news_detail_content);
        content.setText(Html.fromHtml(getIntent().getStringExtra("content")));

        TextView type = findViewById(R.id.news_detail_type);
        type.setText(getIntent().getStringExtra("type"));

        TextView author = findViewById(R.id.news_detail_author);
        author.setText(new StringBuilder().append("by ").append(getIntent().getStringExtra("author")));
//
        TextView lastUpdateTime = findViewById(R.id.news_detail_time);
        SimpleDateFormat sdfOutput = new SimpleDateFormat("dd/MM/yy, HH:mm", Locale.ENGLISH);
        sdfOutput.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        lastUpdateTime.setText(new StringBuilder().append(sdfOutput.format(DateConverter.toDate(getIntent().getLongExtra("last-update-time", 0)))).append(" WIB / ").toString());
    }

    public void closeActivity(View v) {
        finish();
    }
}

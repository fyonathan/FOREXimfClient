package com.foreximf.client;

import android.os.Build;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.foreximf.client.assistant.AssistantFragment;
import com.foreximf.client.news.News;
import com.foreximf.client.news.NewsRecyclerViewAdapter;
import com.foreximf.client.news.NewsViewModel;
import com.foreximf.client.signal.Signal;
import com.foreximf.client.signal.SignalFragment;
import com.foreximf.client.signal.SignalViewModel;
import com.foreximf.client.util.DateFormatter;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class MainActivity extends AppCompatActivity implements SignalFragment.OnFragmentInteractionListener {
    final String URL = "https://client.foreximf.com/update-news";

    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    BottomNavigationView bottomNavigationView;
    Fragment currentFragment;
    FragmentTransaction ft;
    AppCompatActivity activity = this;
    SlidingUpPanelLayout slidingUpPanelLayout;

    RecyclerView dailyListView;
    NewsRecyclerViewAdapter dailyAdapter;
    RecyclerView.LayoutManager layoutManager;
    Badge badge;

    private NewsViewModel newsViewModel;
    private SignalViewModel signalViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        NotificationManagerCompat.from(this).cancelAll();

        if(Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            window.setExitTransition(new Fade());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("signal99");
        FirebaseMessaging.getInstance().subscribeToTopic("news");

        //final ForexImfAppDatabase appDatabase = ForexImfAppDatabase.getDatabase(this.getApplication());
//        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
//        long lastUpdate = preferences.getLong("last-update-time", 0);
//        if(lastUpdate + 86400000 < System.currentTimeMillis()) {
//            /*  Get updated news from server
//                Update DB
//                Update RecyclerView
//             */
//            RequestQueue queue = Volley.newRequestQueue(this);
//
//            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
//
//                @Override
//                public void onResponse(JSONObject response) {
//                    try {
//                        JSONArray result = response.getJSONArray("result");
//                        JSONObject signal = result.getJSONObject(0);
//                        JSONObject news = result.getJSONObject(1);
//                        JSONObject analysis = result.getJSONObject(2);
//                        Log.i("Response Server 1", signal.getString("type"));
//                        Log.i("Response Server 2", news.getString("type"));
//                        Log.i("Response Server 3", analysis.getString("type"));
////                        appDatabase.newsModel().updateNews();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, new Response.ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e("Error response", "Error gan!");
//                }
//            });
//
//            queue.add(request);
//        }

        ft = getSupportFragmentManager().beginTransaction();
        currentFragment = SignalFragment.newInstance();
        ft.replace(R.id.fragment_container, currentFragment, "SIGNAL");
        ft.commit();

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        slidingUpPanelLayout = findViewById(R.id.sliding_layout);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        getSupportActionBar().setIcon(R.mipmap.ic_main);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

//        bottomNavigationView = findViewById(R.id.navigation_view);
//        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavListener);

        dailyListView = findViewById(R.id.daily_list_view);
        dailyAdapter = new NewsRecyclerViewAdapter(this);
        dailyListView.setAdapter(dailyAdapter);
        layoutManager = new LinearLayoutManager(this);
        dailyListView.setLayoutManager(layoutManager);
        dailyListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        newsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
        newsViewModel.getAllNews().observe(this, newsObserver);

        signalViewModel = ViewModelProviders.of(this).get(SignalViewModel.class);
        signalViewModel.getAllSignal().observe(this, signalObserver);

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.d("Main Activity", "Offset : " + slideOffset);
                ImageView icon = findViewById(R.id.slide_icon);
                float deg = slideOffset * 180F;
                icon.animate().rotation(deg).setDuration(0).start();
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        });

        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        boolean firstVisit = preferences.getBoolean("first-visit", true);
        if(firstVisit) {
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray result = response.getJSONArray("result");
                        JSONObject signal = result.getJSONObject(0);
                        JSONObject news = result.getJSONObject(1);
                        JSONObject analysis = result.getJSONObject(2);
                        String dateString = signal.getString("updated_at");
                        Date date = DateFormatter.format(dateString);

                        News signalItem = new News(News.typeConverter(signal.getInt("type")), signal.getString("title"), signal.getString("content"), signal.getString("author"), date);
                        News newsItem = new News(News.typeConverter(news.getInt("type")), news.getString("title"), news.getString("content"), news.getString("author"), date);
                        News analysisItem = new News(News.typeConverter(analysis.getInt("type")), analysis.getString("title"), analysis.getString("content"), analysis.getString("author"), date);
//                        NewsRepository newsRepository = new NewsRepository(getApplication());
                        newsViewModel.addNews(signalItem);
                        newsViewModel.addNews(newsItem);
                        newsViewModel.addNews(analysisItem);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, error -> Log.e("Error response", "Error gan!"));

            queue.add(request);
            preferences.edit().putBoolean("first-visit", false).apply();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    Observer<List<News>> newsObserver = new Observer<List<News>>() {

        @Override
        public void onChanged(@Nullable final List<News> newsList) {
            dailyAdapter.setNews(newsList);
        }
    };

    final Observer<List<Signal>> signalObserver = new Observer<List<Signal>>() {

        @Override
        public void onChanged(@Nullable List<Signal> signalList) {
            //Check unread count
            if(signalList != null) {
                int unreadCount = 0;
                for (Signal signal : signalList) {
                    if (signal.getRead() == 0) {
                        unreadCount++;
                    }
                }
                if(unreadCount > 0) {
                    //Pass order to add badge using unread count value
                    changeBadgeStatus(unreadCount);
                }
            }
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch(item.getItemId()) {
                case R.id.action_signal : {
                    ft = getSupportFragmentManager().beginTransaction();
                    currentFragment = SignalFragment.newInstance();
                    ft.replace(R.id.fragment_container, currentFragment, "SIGNAL");
                    ft.commit();
                    signalViewModel.updateSignalRead();
                    changeBadgeStatus(0);
                    return true;
                }
                case R.id.action_assistant : {
                    ft = getSupportFragmentManager().beginTransaction();
                    currentFragment = new AssistantFragment();
                    ft.replace(R.id.fragment_container, currentFragment);
                    ft.commit();
                    return true;
                }
                case R.id.action_chat : {
                    Toast.makeText(MainActivity.this, "Chat clicked", Toast.LENGTH_LONG).show();
                    return true;
                }
            }
            return false;
        }
    };

    private void changeBadgeStatus(int unreadCount) {
//        BottomNavigationMenuView bottomNavigationMenuView =
//                (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
//        View v = bottomNavigationMenuView.getChildAt(0);
//        if(unreadCount > 0) {
//            badge = new QBadgeView(getApplicationContext()).bindTarget(v).setBadgeNumber(unreadCount).setBadgeGravity(Gravity.END | Gravity.TOP).setGravityOffset(35, 0, true);
//        }else{
//            try {
//                badge.hide(false);
//            } catch (Exception e) {
//                Log.e("Badge status", "Badge is null");
//            }
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_favorite) {
//            Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(int unreadCount) {
        changeBadgeStatus(unreadCount);
    }

    public void getClientStatus() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
            try {
                response.getJSONArray("lead_status");
                JSONArray result = response.getJSONArray("result");
                JSONObject signal = result.getJSONObject(0);
                JSONObject news = result.getJSONObject(1);
                JSONObject analysis = result.getJSONObject(2);
                String dateString = signal.getString("updated_at");
                Date date = DateFormatter.format(dateString);

                News signalItem = new News(News.typeConverter(signal.getInt("type")), signal.getString("title"), signal.getString("content"), signal.getString("author"), date);
                News newsItem = new News(News.typeConverter(news.getInt("type")), news.getString("title"), news.getString("content"), news.getString("author"), date);
                News analysisItem = new News(News.typeConverter(analysis.getInt("type")), analysis.getString("title"), analysis.getString("content"), analysis.getString("author"), date);
//                        NewsRepository newsRepository = new NewsRepository(getApplication());
                newsViewModel.addNews(signalItem);
                newsViewModel.addNews(newsItem);
                newsViewModel.addNews(analysisItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("Error response", "Error gan!"));

        queue.add(request);
    }
}

package com.foreximf.quickpro;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.foreximf.quickpro.camarilla.CamarillaFragment;
import com.foreximf.quickpro.chat.ChatFragment;
import com.foreximf.quickpro.chat.model.ChatThread;
import com.foreximf.quickpro.database.ForexImfAppDatabase;
import com.foreximf.quickpro.news.News;
import com.foreximf.quickpro.news.NewsViewModel;
import com.foreximf.quickpro.signal.SignalFragment;
import com.foreximf.quickpro.signal.SignalViewModel;
import com.foreximf.quickpro.util.ArchLifecycleApp;
import com.foreximf.quickpro.util.DateFormatter;
import com.foreximf.quickpro.util.F;
import com.foreximf.quickpro.util.ImageDisplayActivity;
import com.foreximf.quickpro.util.ImageUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import q.rorbin.badgeview.Badge;

public class MainActivity extends AppCompatActivity implements SignalFragment.OnFragmentInteractionListener, CamarillaFragment.OnFragmentInteractionListener {
    final String URL = "https://client.foreximf.com/update-news";

    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    BottomNavigationView bottomNavigationView;
    Fragment currentFragment;
    FragmentTransaction ft;
    AppCompatActivity activity = this;

    SharedPreferences loginPreferences;
//    SlidingUpPanelLayout slidingUpPanelLayout;

//    RecyclerView dailyListView;
//    NewsRecyclerViewAdapter dailyAdapter;
//    RecyclerView.LayoutManager layoutManager;
    Badge badge;

    private NewsViewModel newsViewModel;
    private SignalViewModel signalViewModel;

    private Tracker mTracker;

    private class test extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            List<ChatThread> ct = ForexImfAppDatabase.getDatabase(MainActivity.this).chatDao().getActiveThread();
            Log.d("ChatThread2", ""+ct.size());
            for(int i = 0; i < ct.size(); i++) {
                Log.d("ChatThread2", ct.get(i).toJSON().toString());
                ForexImfAppDatabase.getDatabase(MainActivity.this).chatDao().delete(ct.get(i));
            }
            return null;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        loginPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        loginPreferences.edit().remove("login-token").apply();
        String token = loginPreferences.getString("login-token", "");
//        Log.d("MainActivity", "Token : " + token);
        
        if(token.isEmpty()) {
            moveToLoginActivity();
        } else {
            F.checkUpdate(this);
        }

        ArchLifecycleApp application = (ArchLifecycleApp) getApplication();
        mTracker = application.getDefaultTracker();
//        mTracker.setScreenName("Detail Signal" + signal.getTitle());
//        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
//        new test().execute();

        NotificationManagerCompat.from(this).cancelAll();

        if(Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            window.setExitTransition(new Fade());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check Eligibility
        checkSubscriptionEligibility(token);
//        FirebaseMessaging.getInstance().subscribeToTopic("signal99");
        FirebaseMessaging.getInstance().subscribeToTopic("news");

        ft = getSupportFragmentManager().beginTransaction();
        currentFragment = SignalFragment.newInstance();
        ft.replace(R.id.fragment_container, currentFragment, "SIGNAL");
        ft.commit();

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

        drawer = findViewById(R.id.drawer);
//        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

//        slidingUpPanelLayout = findViewById(R.id.sliding_layout);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorLightBlue)));
        getSupportActionBar().setIcon(R.mipmap.ic_main_white);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if(!token.isEmpty()) {
            Token.updateToken(this);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }

        bottomNavigationView = findViewById(R.id.navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavListener);

        String fragmentIntent = getIntent().getStringExtra("fragment");
        if(fragmentIntent != null) {
            if(fragmentIntent.equals("camarilla")) {
                ft = getSupportFragmentManager().beginTransaction();
                currentFragment = CamarillaFragment.newInstance();
                ft.replace(R.id.fragment_container, currentFragment, "CAMARILLA");
                ft.commit();
                bottomNavigationView.setSelectedItemId(R.id.action_camarilla);
            }
        }

//        dailyListView = findViewById(R.id.daily_list_view);
//        dailyAdapter = new NewsRecyclerViewAdapter(this);
//        dailyListView.setAdapter(dailyAdapter);
//        layoutManager = new LinearLayoutManager(this);
//        dailyListView.setLayoutManager(layoutManager);
//        dailyListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

//        newsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
//        newsViewModel.getAllNews().observe(this, newsObserver);

//        signalViewModel = ViewModelProviders.of(this).get(SignalViewModel.class);
//        signalViewModel.getAllSignal().observe(this, signalObserver);

//        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
//            @Override
//            public void onPanelSlide(View panel, float slideOffset) {
//                Log.d("Main Activity", "Offset : " + slideOffset);
//                ImageView icon = findViewById(R.id.slide_icon);
//                float deg = slideOffset * 180F;
//                icon.animate().rotation(deg).setDuration(0).start();
//            }
//
//            @Override
//            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
//
//            }
//        });

//        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
//        boolean firstVisit = preferences.getBoolean("first-visit", true);
//        if(firstVisit) {
//            RequestQueue queue = Volley.newRequestQueue(this);
//            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
//
//                @Override
//                public void onResponse(JSONObject response) {
//                    try {
//                        JSONArray result = response.getJSONArray("result");
//                        JSONObject signal = result.getJSONObject(0);
//                        JSONObject news = result.getJSONObject(1);
//                        JSONObject analysis = result.getJSONObject(2);
//                        String dateString = signal.getString("updated_at");
//                        Date date = DateFormatter.format(dateString);
//
//                        News signalItem = new News(News.typeConverter(signal.getInt("type")), signal.getString("title"), signal.getString("content"), signal.getString("author"), date);
//                        News newsItem = new News(News.typeConverter(news.getInt("type")), news.getString("title"), news.getString("content"), news.getString("author"), date);
//                        News analysisItem = new News(News.typeConverter(analysis.getInt("type")), analysis.getString("title"), analysis.getString("content"), analysis.getString("author"), date);
////                        NewsRepository newsRepository = new NewsRepository(getApplication());
//                        newsViewModel.addNews(signalItem);
//                        newsViewModel.addNews(newsItem);
//                        newsViewModel.addNews(analysisItem);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, error -> Log.e("Error response", "Error gan!"));
//
//            queue.add(request);
//            preferences.edit().putBoolean("first-visit", false).apply();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NavigationView navigationView = findViewById(R.id.sidebar);
        View headerView = navigationView.getHeaderView(0);
        ImageView headerProfileIcon = headerView.findViewById(R.id.header_profile_icon);
        ImageUtils.loadAvatarPicasso(this, loginPreferences.getString("user-avatar", ""), headerProfileIcon);
        headerProfileIcon.setOnClickListener(view -> {
            Intent intent1 = new Intent(activity, ImageDisplayActivity.class);
            intent1.putExtra("image-uri", loginPreferences.getString("user-avatar", ""));
            intent1.putExtra("title", loginPreferences.getString("user-name", ""));
            if(Build.VERSION.SDK_INT >= 21) {
                startActivity(intent1, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
            }
        });
        TextView headerNameText = headerView.findViewById(R.id.header_name_text);
        headerNameText.setText(loginPreferences.getString("user-name", ""));
        TextView headerEmailText = headerView.findViewById(R.id.header_email_text);
        headerEmailText.setText(loginPreferences.getString("user-email", ""));
        navigationView.setNavigationItemSelectedListener(drawerListener);
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

//    Observer<List<News>> newsObserver = new Observer<List<News>>() {
//
//        @Override
//        public void onChanged(@Nullable final List<News> newsList) {
//            dailyAdapter.setNews(newsList);
//        }
//    };

//    final Observer<List<Signal>> signalObserver = new Observer<List<Signal>>() {
//
//        @Override
//        public void onChanged(@Nullable List<Signal> signalList) {
//            //Check unread count
//            if(signalList != null) {
//                int unreadCount = 0;
//                for (Signal signal : signalList) {
//                    if (signal.getRead() == 0) {
//                        unreadCount++;
//                    }
//                }
//                if(unreadCount > 0) {
//                    //Pass order to add badge using unread count value
//                    changeBadgeStatus(unreadCount);
//                }
//            }
//        }
//    };

    private NavigationView.OnNavigationItemSelectedListener drawerListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if(item.getItemId() == R.id.nav_logout) {
                mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("Logout")
                    .build());
                SharedPreferences loginPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                loginPreferences.edit().remove("login-token").apply();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    CookieManager.getInstance().removeAllCookies(null);
                    CookieManager.getInstance().flush();
                } else {
                    CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(MainActivity.this);
                    cookieSyncMngr.startSync();
                    CookieManager cookieManager=CookieManager.getInstance();
                    cookieManager.removeAllCookie();
                    cookieManager.removeSessionCookie();
                    cookieSyncMngr.stopSync();
                    cookieSyncMngr.sync();
                }
                moveToLoginActivity();
            }
            if (item.getItemId() == R.id.nav_profile) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }
    };

    void moveToLoginActivity() {
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch(item.getItemId()) {
                case R.id.action_signal : {
                    ft = getSupportFragmentManager().beginTransaction();
                    currentFragment = SignalFragment.newInstance();
                    ft.replace(R.id.fragment_container, currentFragment, "SIGNAL");
                    ft.commit();
//                    signalViewModel.updateSignalRead();
//                    changeBadgeStatus(0);
                    return true;
                }
                case R.id.action_camarilla : {
                    ft = getSupportFragmentManager().beginTransaction();
                    currentFragment = CamarillaFragment.newInstance();
                    ft.replace(R.id.fragment_container, currentFragment, "CAMARILLA");
                    ft.commit();
                    return true;
                }
//                case R.id.action_chat: {
//                    ft = getSupportFragmentManager().beginTransaction();
//                    currentFragment = ChatFragment.newInstance();
//                    ft.replace(R.id.fragment_container, currentFragment, "CHAT");
//                    ft.commit();
//                    return true;
//                }
//                case R.id.action_assistant : {
//                    ft = getSupportFragmentManager().beginTransaction();
//                    currentFragment = new AssistantFragment();
//                    ft.replace(R.id.fragment_container, currentFragment);
//                    ft.commit();
//                    return true;
//                }
//                case R.id.action_chat : {
//                    Toast.makeText(MainActivity.this, "Chat clicked", Toast.LENGTH_LONG).show();
//                    return true;
//                }
            }
            return false;
        }
    };

    private void checkSubscriptionEligibility(String token) {
        String url = "https://client.foreximf.com/eligibility-check";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // response
//                    Log.d("MainActivity", "Response : " + response);
                    if(response.equals("true")) {
                        FirebaseMessaging.getInstance().subscribeToTopic("signal");
                        FirebaseMessaging.getInstance().subscribeToTopic("camarilla");
//                        FirebaseMessaging.getInstance().subscribeToTopic("signal-switching");
                    }else{
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("signal");
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("camarilla");
                    }
//                    Log.d("MainActivity", response);
                },
                error -> {
                    // error
//                    Log.d("Error.Response", "" + error.getMessage());
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("token", token);

                return params;
            }
        };
        queue.add(postRequest);
    }

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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

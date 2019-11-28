package com.foreximf.quickpro;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.foreximf.quickpro.util.F;
import com.foreximf.quickpro.util.ImageUtils;
import com.foreximf.quickpro.util.TakePhotoActivity;
import com.foreximf.quickpro.util.WebViewActivity;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements LifecycleOwner {

    private static final int GET_FROM_GALLERY = 3;

    static SharedPreferences preferences;
    private CircleImageView avatar;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorLightBlue));
        }

        if (F.isNetworkAvailable(this)) {
            F.checkProfile(this, new F.JSONRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    name.setText(preferences.getString("user-name", ""));
                    Fragment currentFragment = getFragmentManager().findFragmentById(R.id.container_settings);
                    if (currentFragment instanceof ProfileFragment) {
                        ((ProfileFragment) currentFragment).reload();
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        layoutParams.setScrollFlags(0);

        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        name = findViewById(R.id.name);
        name.setText(preferences.getString("user-name", ""));
        avatar = findViewById(R.id.avatar);
        ImageUtils.loadAvatarPicasso(this, preferences.getString("user-avatar", ""), avatar);
        avatar.setOnClickListener(view -> {
            PowerMenu powerMenu = new PowerMenu.Builder(this)
                .addItem(new PowerMenuItem("Camera", false))
                .addItem(new PowerMenuItem("Select Photo", false))
                .addItem(new PowerMenuItem("Delete", false))
                .setAnimation(MenuAnimation.SHOW_UP_CENTER)
                .setDividerHeight(1)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .setLifecycleOwner(this)
                .build();
            powerMenu.setOnMenuItemClickListener((position, item) -> {
                switch (item.getTitle()) {
                        case "Camera": {
                            powerMenu.dismiss();
                            startActivityForResult(new Intent(ProfileActivity.this, TakePhotoActivity.class), TakePhotoActivity.TAKE_PHOTO);
                            break;
                        }
                    case "Select Photo": {
                        powerMenu.dismiss();
                        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                        break;
                    }
                    case "Delete": {
                        String url = "https://client.foreximf.com/api-profile";
                        Map<String, String> params = new HashMap<>();
                        params.put("token", preferences.getString("login-token", ""));
                        params.put("mode", "profile-picture-delete");

                        F.JSONRequest(this, url, params, new F.JSONRequestCallback() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                powerMenu.dismiss();
                                try {
                                    boolean error = response.getBoolean("error");
                                    if (!error) {
                                        F.setLoginPreferences(preferences, response);
                                        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.container_settings);
                                        if (currentFragment instanceof ProfileFragment) {
                                            ((ProfileFragment) currentFragment).reload();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(VolleyError error) {
                                powerMenu.dismiss();
                                Toast.makeText(ProfileActivity.this, "Muncul error saat mencoba menghapus profile picture dari server, silakan coba beberapa saat lagi", Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    }
                }
            });
            powerMenu.showAtCenter(avatar);
        });

        getFragmentManager().beginTransaction()
            .replace(R.id.container_settings, new ProfileFragment())
            .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == GET_FROM_GALLERY || requestCode == TakePhotoActivity.TAKE_PHOTO) && resultCode == Activity.RESULT_OK) {
            ProgressDialog dialog = new ProgressDialog(ProfileActivity.this);
            dialog.setMessage("Uploading...");
            dialog.show();
            String url = "https://client.foreximf.com/api-profile";
            Map<String, String>  params = new HashMap<>();
            params.put("token", preferences.getString("login-token", ""));
            params.put("mode", "profile-picture");
            Bitmap bmp = null;
            if (requestCode == GET_FROM_GALLERY) {
                Uri selectedImage = data.getData();
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == TakePhotoActivity.TAKE_PHOTO) {
                if (data.hasExtra("uri")) {
                    String filePath = data.getStringExtra("uri");
                    File file = new File(filePath);
                    bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                    file.deleteOnExit();
                }
            }

            if (bmp != null) {
                int limit_size = 960;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap;
                if (bmp.getWidth() > limit_size && bmp.getWidth() >= bmp.getHeight()) {
                    bitmap = Bitmap.createScaledBitmap(bmp, limit_size, (int) Math.floor(limit_size * bmp.getHeight() / bmp.getWidth()), false);
                } else if (bmp.getHeight() > limit_size && bmp.getHeight() > bmp.getWidth()) {
                    bitmap = Bitmap.createScaledBitmap(bmp, (int) Math.floor(limit_size * bmp.getWidth() / bmp.getHeight()), limit_size, false);
                } else {
                    bitmap = bmp;
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                params.put("content", Base64.encodeToString(imageBytes, Base64.DEFAULT));

                F.JSONRequest(this, url, params, new F.JSONRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        dialog.dismiss();
                        try {
                            boolean error = response.getBoolean("error");
                            if (!error) {
                                F.setLoginPreferences(preferences, response);
                                Fragment currentFragment = getFragmentManager().findFragmentById(R.id.container_settings);
                                if (currentFragment instanceof ProfileFragment) {
                                    ((ProfileFragment) currentFragment).reload();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Muncul error saat mencoba upload gambar, silakan coba beberapa saat lagi", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    public static class ProfileFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        public static String TAG = ProfileFragment.class.getSimpleName();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_profile);
            reload();
        }

        public void reload() {
            if (preferences.getBoolean("user-email-verified", false)) {
                Spannable summary = new SpannableString(preferences.getString("user-email", ""));
                summary.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLightBlue)), 0, summary.length(), 0);
                findPreference("user-email").setSummary(summary);
            } else {
                Spannable summary = new SpannableString("Unverified");
                summary.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLightGrey)), 0, summary.length(), 0);
                findPreference("user-email").setSummary(summary);
            }
            if (preferences.getBoolean("user-phone-verified", false)) {
                Spannable summary = new SpannableString(preferences.getString("user-phone", ""));
                summary.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLightBlue)), 0, summary.length(), 0);
                findPreference("user-phone").setSummary(summary);
            } else {
                Spannable summary = new SpannableString("Unverified");
                summary.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLightGrey)), 0, summary.length(), 0);
                findPreference("user-phone").setSummary(summary);
            }
            if (preferences.getBoolean("set-password", false)) {
                findPreference("client-area").setTitle("Client Area");
                Spannable summary = new SpannableString("Access your client area here");
                summary.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLightBlue)), 0, summary.length(), 0);
                findPreference("client-area").setSummary(summary);
            } else {
                findPreference("client-area").setTitle("Set Password");
                Spannable summary = new SpannableString("Please set password to access your Client Area");
                summary.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLightGrey)), 0, summary.length(), 0);
                findPreference("client-area").setSummary(summary);
            }
            ((ProfileActivity)getActivity()).name.setText(preferences.getString("user-name", ""));
            ImageUtils.loadAvatarPicasso(getActivity(), preferences.getString("user-avatar", ""), ((ProfileActivity)getActivity()).avatar);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return true;
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            switch(preference.getKey()) {
                case "user-email": {
                    Intent intent = new Intent(getActivity(), ForgotActivity.class);
                    intent.putExtra("mode", "change_email");
                    intent.putExtra("email", preferences.getString("user-email", "null"));
                    startActivity(intent);
                    break;
                }
                case "user-phone": {
                    Intent intent = new Intent(getActivity(), ForgotActivity.class);
                    intent.putExtra("mode", "change_phone");
                    intent.putExtra("phone", preferences.getString("user-phone", "null"));
                    startActivity(intent);
                    break;
                }
                case "client-area": {
                    if (F.isNetworkAvailable(getActivity())) {
                        ProgressDialog dialog = new ProgressDialog(getActivity());
                        dialog.setMessage("Authenticating...");
                        dialog.show();
                        Map<String, String> params = new HashMap<>();
                        params.put("mode", "client-area");
                        params.put("token", preferences.getString("login-token", ""));
                        F.JSONRequest(getActivity(), "https://client.foreximf.com/api-profile", params, new F.JSONRequestCallback() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                try {
                                    boolean error = response.getBoolean("error");
                                    if (error) {
                                        Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_LONG).show();
                                    } else {
                                        Intent intent = new Intent(getActivity(), WebViewActivity.class);
                                        intent.putExtra("url", response.getString("url"));
                                        startActivity(intent);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }

                            @Override
                            public void onError(VolleyError error) {
                                Toast.makeText(getActivity(), "Muncul error saat mengakses client area, silakan coba beberapa saat lagi", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Anda tidak terhubung dengan internet, silahkan periksa kembali koneksi anda.", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
            }
            return false;
//            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }
}

package com.foreximf.quickpro;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.foreximf.quickpro.util.F;
import com.mukesh.OtpView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerifyActivity extends AppCompatActivity {

    private String mode;
    private String ver;
    private String reg;
    private String email;
    private String phone;
    private String otp;

    private View mProgressView;
    private View mLoginFormView;
    private TextView phone_text;
    private TextView otp_text;
    private TextView otp_request;
    private OtpView otpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_verify);

        if (getIntent().hasExtra("reg")) {
            reg = getIntent().getStringExtra("reg");
        } else if (getIntent().hasExtra("ver")) {
            ver = getIntent().getStringExtra("ver");
        } else if (getIntent().hasExtra("mode")) {
            mode = getIntent().getStringExtra("mode");
        }
        if (getIntent().hasExtra("email")) {
            email = getIntent().getStringExtra("email");
        }
        if (getIntent().hasExtra("phone")) {
            phone = getIntent().getStringExtra("phone");
        }
        otp = getIntent().getStringExtra("otp");

        phone_text = findViewById(R.id.text_phone);
        otp_text = findViewById(R.id.text_otp);
        otp_request = findViewById(R.id.request_code);
        phone_text.setText(phone == null || phone.isEmpty() ? email : phone);
        otp_text.setText(otp);

        otpView = findViewById(R.id.otp_view);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        otp_request.setOnClickListener(view -> {
            if(F.isNetworkAvailable(VerifyActivity.this)) {
                requestCode();
            } else {
                Toast.makeText(this, "Anda tidak terhubung dengan internet, silahkan periksa kembali koneksi anda.", Toast.LENGTH_LONG).show();
            }
        });

        Button mEmailSignInButton = findViewById(R.id.verify_button);
        mEmailSignInButton.setOnClickListener(view -> {
            if(F.isNetworkAvailable(VerifyActivity.this)) {
                attemptVerify();
            } else {
                Toast.makeText(this, "Anda tidak terhubung dengan internet, silahkan periksa kembali koneksi anda.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private PointF _swipeCoor;
    private Long _swipeTime;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            _swipeCoor = new PointF(ev.getRawX(), ev.getRawY());
            _swipeTime = System.currentTimeMillis();
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();
            if (v != null && v instanceof EditText && System.currentTimeMillis() < _swipeTime + 350 && Math.abs(ev.getRawY() - _swipeCoor.y) > 250) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void attemptVerify() {
        // Close Keyboard
        View v = getCurrentFocus();
        if (v == null) {
            v = new View(this);
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        // Reset errors.
        otpView.setError(null);
        String code = otpView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(code)) {
            otpView.setError(getString(R.string.error_field_required));
            focusView = otpView;
            cancel = true;
        } else if (!isCodeValid(code)) {
            otpView.setError(getString(R.string.error_invalid_code));
            focusView = otpView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            verify(code);
        }
    }

    private boolean isCodeValid(String code) {
        return code.length() == 6;
    }

    private void requestCode() {
        showProgress(true);
        String url = "https://client.foreximf.com/api-verify";
        Map<String, String> params = new HashMap<>();
        if (phone == null || phone.isEmpty()) {
            params.put("email", email);
        } else {
            params.put("phone", phone);
        }
        params.put("request-code", "1");
        F.JSONRequest(this, url, params, new F.JSONRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    boolean error = response.getBoolean("error");
                    if (error) {
                        Toast.makeText(VerifyActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    } else {
                        otp_text.setText(response.getString("otp"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showProgress(false);
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(VerifyActivity.this, "Muncul error saat mencoba verify, silakan coba beberapa saat lagi", Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
    }

    private void verify(String code) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        showProgress(true);
        String url = "https://client.foreximf.com/api-verify";
        Map<String, String> params = new HashMap<>();
        if (phone == null || phone.isEmpty()) {
            params.put("email", email);
        } else {
            params.put("phone", phone);
        }
        if (reg != null) {
            params.put("reg", reg);
            params.put("email", email);
        } else if (ver != null) {
            params.put("ver", ver);
        } else if (mode != null) {
            params.put("mode", mode);
            params.put("token", preferences.getString("login-token", ""));
        }
        params.put("code", code);
        F.JSONRequest(this, url, params, new F.JSONRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(VerifyActivity.this);
                try {
                    boolean error = response.getBoolean("error");
                    if (error) {
                        otpView.setError(response.getString("message"));
                        Toast.makeText(VerifyActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    } else {
                        F.setLoginPreferences(preferences, response);
                        if (response.has("mode")) {
                            finish();
                            Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Intent intent2 = new Intent(VerifyActivity.this, ProfileActivity.class);
                            startActivity(intent2);
                        } else {
                            Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showProgress(false);
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(VerifyActivity.this, "Muncul error saat mencoba verify, silakan coba beberapa saat lagi", Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
    }
}

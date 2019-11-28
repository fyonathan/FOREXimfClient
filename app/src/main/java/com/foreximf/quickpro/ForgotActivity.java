package com.foreximf.quickpro;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.foreximf.quickpro.util.F;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotActivity extends AppCompatActivity {

    private String mode;

    private View mProgressView;
    private View mLoginFormView;
    private TextView mHintView;
    private AutoCompleteTextView mInputView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_forgot);

        Intent i = getIntent();
        if (!i.hasExtra("mode")) {
            onBackPressed();
        }
        mode = i.getStringExtra("mode");
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mHintView = findViewById(R.id.hint);

        mInputView = findViewById(R.id.input);
        mInputView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptForgot();
                return true;
            }
            return false;
        });

        if (mode.equals("change_email")) {
            mHintView.setText(R.string.change_email_description);
            mInputView.setHint(R.string.prompt_email);
            if (!i.getStringExtra("email").equals("null")) {
                mInputView.setText(i.getStringExtra("email"));
            }
        } else if (mode.equals("change_phone")) {
            mHintView.setText(R.string.change_phone_description);
            mInputView.setHint(R.string.prompt_phone);
            if (!i.getStringExtra("phone").equals("null")) {
                mInputView.setText(i.getStringExtra("phone"));
            }
        } else {
            mHintView.setText(R.string.forgot_description);
            mInputView.setHint(R.string.prompt_email_phone);
        }

        Button mEmailSignInButton = findViewById(R.id.email_forgot_button);
        mEmailSignInButton.setOnClickListener(view -> {
            if(F.isNetworkAvailable(ForgotActivity.this)) {
                attemptForgot();
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

    private void attemptForgot() {
        // Close Keyboard
        View v = getCurrentFocus();
        if (v == null) {
            v = new View(this);
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        // Reset errors.
        mInputView.setError(null);

        // Store values at the time of the login attempt.
        String input = mInputView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(input)) {
            mInputView.setError(getString(R.string.error_field_required));
            focusView = mInputView;
            cancel = true;
        } else if (mode.equals("change_email") && !isEmailValid(input)) {
            mInputView.setError(getString(R.string.error_invalid_email));
            focusView = mInputView;
            cancel = true;
        } else if (mode.equals("change_phone") && !isPhoneValid(input)) {
            mInputView.setError(getString(R.string.error_invalid_phone));
            focusView = mInputView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            forgot();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPhoneValid(String phone) {
        return phone.length() >= 7;
    }

    private void forgot() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        showProgress(true);
        String url = "https://client.foreximf.com/api-forgot";
        String input = mInputView.getText().toString();
        Map<String, String> params = new HashMap<>();
        params.put("mode", mode);
        params.put("input", input);
        if (!mode.equals("forgot")) {
            params.put("token", preferences.getString("login-token", ""));
        }
        F.JSONRequest(this, url, params, new F.JSONRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    boolean error = response.getBoolean("error");
                    if (error) {
                        if (response.has("action")) {
                            String action = response.getString("action");
                            if (action.equals("login")) {
                                Intent intent = new Intent(ForgotActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                if (response.has("message")) {
                                    intent.putExtra("message", response.getString("message"));
                                }
                                startActivity(intent);
                                finish();
                            } else if (action.equals("back")) {
                                ForgotActivity.this.onBackPressed();
                            }
                        } else if (response.has("message")) {
                            Toast.makeText(ForgotActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ForgotActivity.this, "No Handphone tidak terdaftar", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Intent verifyIntent = new Intent(ForgotActivity.this, VerifyActivity.class);
                        if (mode.equals("change_email")) {
                            verifyIntent.putExtra("mode", response.getString("mode"));
                            verifyIntent.putExtra("email", response.getString("email"));
                        } else if (mode.equals("change_phone")) {
                            verifyIntent.putExtra("mode", response.getString("mode"));
                            verifyIntent.putExtra("phone", response.getString("phone"));
                        } else {
                            verifyIntent.putExtra("ver", response.getString("ver"));
                            if (response.has("phone")) {
                                verifyIntent.putExtra("phone", response.getString("phone"));
                            } else {
                                verifyIntent.putExtra("email", response.getString("email"));
                            }
                        }
                        verifyIntent.putExtra("otp", response.getString("otp"));
                        startActivity(verifyIntent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showProgress(false);
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(ForgotActivity.this, "Muncul error saat mencoba sign up, silakan coba beberapa saat lagi", Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
    }
}
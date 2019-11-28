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
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.foreximf.quickpro.util.F;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mNameView;
    private AutoCompleteTextView mPhoneView;
    private View mProgressView;
    private View mLoginFormView;

    private String via;
    private String name;
    private String email;
    private boolean name_email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_signup);

        mNameView = findViewById(R.id.name);
        mEmailView = findViewById(R.id.email);
        mPhoneView = findViewById(R.id.phone);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        Intent intent = getIntent();
        if (intent.hasExtra("name")) {
            name = intent.getStringExtra("name");
        }
        if (intent.hasExtra("email")) {
            email = intent.getStringExtra("email");
        }
        if (intent.hasExtra("via")) {
            via = intent.getStringExtra("via");
        }
        if (name != null && email != null) {
            mNameView.setVisibility(View.GONE);
            mEmailView.setVisibility(View.GONE);
            name_email = true;
        } else {
            name_email = false;
        }

        mPhoneView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptSignUp();
                return true;
            }
            return false;
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_up_button);
        mEmailSignInButton.setOnClickListener(view -> {
            if(F.isNetworkAvailable(SignupActivity.this)) {
                attemptSignUp();
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

    private void attemptSignUp() {
        // Close Keyboard
        View v = getCurrentFocus();
        if (v == null) {
            v = new View(this);
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        // Reset errors.
        mNameView.setError(null);
        mEmailView.setError(null);
        mPhoneView.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String phone = mPhoneView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!name_email) {
            if (TextUtils.isEmpty(name)) {
                mNameView.setError(getString(R.string.error_field_required));
                focusView = mNameView;
                cancel = true;
            }
            if (TextUtils.isEmpty(email)) {
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                cancel = true;
            } else if (!isEmailValid(email)) {
                mEmailView.setError(getString(R.string.error_invalid_email));
                focusView = mEmailView;
                cancel = true;
            }
        }
        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError(getString(R.string.error_field_required));
            focusView = mPhoneView;
            cancel = true;
        } else if (!isPhoneValid(phone)) {
            mPhoneView.setError(getString(R.string.error_invalid_phone));
            focusView = mPhoneView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            signup();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPhoneValid(String phone) {
        return phone.length() >= 7;
    }

    private void signup() {
        showProgress(true);
        Map<String, String> params = new HashMap<>();
        String url = "https://client.foreximf.com/api-register";
        if (!name_email) {
            name = mNameView.getText().toString();
            email = mEmailView.getText().toString();
        }
        if (name_email) {
            params.put("via", via);
        }
        params.put("name", name);
        params.put("email", email);
        params.put("phone", mPhoneView.getText().toString());

        F.JSONRequest(this, url, params, new F.JSONRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    boolean error = response.getBoolean("error");
                    if (error) {
                        if (response.has("action")) {
                            String action = response.getString("action");
                            if (action.equals("login")) {
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                if (response.has("message")) {
                                    intent.putExtra("message", response.getString("message"));
                                }
                                startActivity(intent);
                                finish();
                            }
                        } else if (response.has("message")) {
                            Toast.makeText(SignupActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignupActivity.this, "Email atau No Handphone sudah terdaftar", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Intent verifyIntent = new Intent(SignupActivity.this, VerifyActivity.class);
                        verifyIntent.putExtra("reg", response.getString("reg"));
                        verifyIntent.putExtra("email", response.getString("email"));
                        verifyIntent.putExtra("phone", response.getString("phone"));
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
                Toast.makeText(SignupActivity.this, "Muncul error saat mencoba sign up, silakan coba beberapa saat lagi", Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
    }
}

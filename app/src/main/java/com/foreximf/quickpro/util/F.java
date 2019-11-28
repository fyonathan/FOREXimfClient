package com.foreximf.quickpro.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.foreximf.quickpro.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class F {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void JSONRequest(Context context, String url, Map params, JSONRequestCallback jsonRequestCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject parameters = new JSONObject(params);
        if (!BuildConfig.BUILD_TYPE.equals("release")) {
            Log.d("Param", url + ": " + parameters.toString());
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, parameters,
            response -> {
                if (!BuildConfig.BUILD_TYPE.equals("release")) {
                    Log.d("JSON", url + ": " + response.toString());
                }
                jsonRequestCallback.onSuccess(response);
            }, error -> {
                VolleyLog.d("Error:", error.getMessage());
                if (!BuildConfig.BUILD_TYPE.equals("release")) {
                    Log.e("Error.Response", "" + error.getMessage());
                }
                jsonRequestCallback.onError(error);
            }
        );
        queue.add(postRequest);
    }

    public interface JSONRequestCallback {
        void onSuccess(JSONObject response);
        void onError(VolleyError error);
    }

    public static void setLoginPreferences(SharedPreferences preferences, JSONObject response) throws JSONException {
        preferences.edit().putString("user-name", response.getString("name")).apply();
        preferences.edit().putString("user-avatar", response.getString("avatar")).apply();
        preferences.edit().putString("user-email", response.getString("email")).apply();
        preferences.edit().putBoolean("user-email-verified", response.getBoolean("email_verified")).apply();
        preferences.edit().putString("user-phone", response.getString("phone")).apply();
        preferences.edit().putBoolean("user-phone-verified", response.getBoolean("phone_verified")).apply();
        preferences.edit().putString("login-token", response.getString("token")).apply();
        preferences.edit().putBoolean("set-password", response.getBoolean("is_password")).apply();
        preferences.edit().putString("user-id", response.getString("user_id")).apply();
    }

    public static void checkProfile(Context context, JSONRequestCallback callback) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String url = "https://client.foreximf.com/api-profile";
        Map<String, String>  params = new HashMap<>();
        params.put("token", preferences.getString("login-token", ""));
        JSONRequest(context, url, params, new JSONRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    boolean error = response.getBoolean("error");
                    if (!error) {
                        setLoginPreferences(preferences, response);
                        callback.onSuccess(response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                callback.onError(error);
            }
        });
    }

    public static void checkUpdate(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String url = "https://client.foreximf.com/api-version";
        Map<String, String>  params = new HashMap<>();
        params.put("build", BuildConfig.BUILD_TYPE);
        params.put("version", ""+preferences.getInt("version_"+BuildConfig.BUILD_TYPE, BuildConfig.VERSION_CODE));
        JSONRequest(context, url, params, new JSONRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    boolean error = response.getBoolean("error");
                    if (!error) {
                        int version = response.getInt("version");
                        preferences.edit().putInt("version_"+BuildConfig.BUILD_TYPE, version).apply();
                        boolean update = response.getBoolean("update");
                        if (update) {
                            new AlertDialog.Builder(context)
                                    .setTitle("Newest Version of QuickPro is Available")
                                    .setMessage("Update QuickPro Now ?")
                                    .setPositiveButton(android.R.string.ok, (dialog, which) ->
                                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID))))
                                    .setNegativeButton(android.R.string.no, null)
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }
}

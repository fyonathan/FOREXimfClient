package com.foreximf.quickpro;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.foreximf.quickpro.services.CloudMessagingService;

import java.util.HashMap;
import java.util.Map;

public class Token {
    public static void updateToken(Context context) {
        String url = "https://client.foreximf.com/update-fcm-token";
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // response
//                    Log.d("FcmToken", "Server Response : " + response);
//                    Log.d("FcmToken", response);
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
//                Log.d("FcmToken", "Fcm Token : " + CloudMessagingService.getToken(context));
//                Log.d("FcmToken", "Login Token : " + PreferenceManager.getDefaultSharedPreferences(context).getString("login-token", ""));
                params.put("token", PreferenceManager.getDefaultSharedPreferences(context).getString("login-token", ""));
                params.put("fcm-registration-token", CloudMessagingService.getToken(context));

                return params;
            }
        };
        queue.add(postRequest);
    }
}

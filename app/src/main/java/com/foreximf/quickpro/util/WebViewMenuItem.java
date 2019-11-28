package com.foreximf.quickpro.util;

import android.graphics.drawable.Drawable;

class WebViewMenuItem {
    String key;
    int resource;
    String text;

    public WebViewMenuItem(String k, int i, String s) {
        this.key = k;
        this.resource = i;
        this.text = s;
    }

    public String getKey() {
        return key;
    }
}

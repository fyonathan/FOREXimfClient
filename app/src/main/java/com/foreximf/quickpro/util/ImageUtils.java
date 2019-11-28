package com.foreximf.quickpro.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.foreximf.quickpro.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ImageUtils {
    /*
    This method will create a spannable which is a displayable styled text. It
    also has a custom ImageGetter based on Picasso for loading <img> tags inside the html.
    We use this for rendering formulas in challenges
     */
    private static boolean picasso_log = false;

    public static Spannable getSpannableHtmlWithImageGetter(TextView view, String value) {
        PicassoImageGetter imageGetter = new PicassoImageGetter(view.getContext(), view);
        Spannable html;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            html = (Spannable) Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
        } else {
            html = (Spannable) Html.fromHtml(value, imageGetter, null);
        }
        return html;
    }

    /*
    Used for setting click listener on the formulas loaded in textview / html. This is
    done using ImageSpan which detects the Image content inside the spannable.
    After that it sets a onClick listener using URLSpan. This is done for all the <img> inside
    the html.
     */
    public static void setClickListenerOnHtmlImageGetter(Spannable html, final Callback callback) {
        for (final ImageSpan span : html.getSpans(0, html.length(), ImageSpan.class)) {
            int flags = html.getSpanFlags(span);
            int start = html.getSpanStart(span);
            int end = html.getSpanEnd(span);

            html.setSpan(new URLSpan(span.getSource()) {
                @Override
                public void onClick(View v) {
                    callback.onImageClick(span.getSource());
                }
            }, start, end, flags);
        }
    }

    private  static final String domain = "https://chat2.foreximf.com";

    public static void loadImagePicasso(Context context, String uri, ImageView imageView) {
        if (uri == null || uri.isEmpty()) {
//            Picasso.with(context)
//                    .load(error)
//                    .into(imageView);
        } else {
            final String url = uri.charAt(0) == '/' ? domain + uri : uri;
            Picasso.with(context).setLoggingEnabled(picasso_log);
            Picasso.with(context)
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE, NetworkPolicy.NO_CACHE)
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(context)
                                .load(url)
//                                .placeholder()
//                                .error()
                                .into(imageView);
                    }
                });
        }
    }

    public static void loadAvatarPicasso(Context context, String uri, ImageView imageView) {
        if (uri == null || uri.isEmpty()) {
            Picasso.with(context).setLoggingEnabled(picasso_log);
            Picasso.with(context)
                .load(R.mipmap.ic_user)
                .into(imageView);
        } else {
            final String url = uri.charAt(0) == '/' ? domain + uri : uri;
            Picasso.with(context).setLoggingEnabled(picasso_log);
            Picasso.with(context)
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE, NetworkPolicy.NO_CACHE)
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(context)
                            .load(url)
                            .placeholder(R.mipmap.ic_user)
                            .error(R.mipmap.ic_user)
                            .into(imageView);
                    }
                });
        }
    }

//    public static Bitmap getImageBitmap(String url) {
//        Bitmap bm = null;
//        try {
//            URL aURL = new URL(url);
//            URLConnection conn = aURL.openConnection();
//            conn.connect();
//            InputStream is = conn.getInputStream();
//            BufferedInputStream bis = new BufferedInputStream(is);
//            bm = BitmapFactory.decodeStream(bis);
//            bis.close();
//            is.close();
//        } catch (IOException e) {
//            Log.e("Error", "Error getting bitmap", e);
//        }
//        return bm;
//    }

    public interface Callback {
        void onImageClick(String imageUrl);
    }
}

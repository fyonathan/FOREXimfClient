package com.foreximf.quickpro.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foreximf.quickpro.R;
import com.skydoves.powermenu.MenuBaseAdapter;

@SuppressWarnings("ConstantConditions")
class WebViewDialogMenuAdapter extends MenuBaseAdapter<WebViewMenuItem> {

    public WebViewDialogMenuAdapter() {
        super();
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();

        if (view == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.webview_more, viewGroup, false);
        }
        WebViewMenuItem item = (WebViewMenuItem)getItem(index);
        final ImageView icon = view.findViewById(R.id.icon);
        icon.setImageResource(item.resource);
        final TextView text = view.findViewById(R.id.text);
        text.setText(item.text);
        return super.getView(index, view, viewGroup);
    }
}

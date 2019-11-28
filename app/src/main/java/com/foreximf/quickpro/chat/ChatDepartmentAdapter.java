package com.foreximf.quickpro.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foreximf.quickpro.R;
import com.foreximf.quickpro.chat.model.ChatDepartment;
import com.foreximf.quickpro.util.ImageUtils;
import com.skydoves.powermenu.MenuBaseAdapter;

public class ChatDepartmentAdapter extends MenuBaseAdapter<ChatDepartment> {

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.chat_department_item, viewGroup, false);
        }
        ChatDepartment chatDepartment = (ChatDepartment) getItem(index);
        final ImageView avatar = view.findViewById(R.id.department_avatar);
        ImageUtils.loadImagePicasso(context, chatDepartment.getAvatar(), avatar);
        final TextView name = view.findViewById(R.id.department_name);
        name.setText(chatDepartment.getName());
        final TextView description = view.findViewById(R.id.department_description);
        description.setText(chatDepartment.getDescription());

        return super.getView(index, view, viewGroup);
    }
}

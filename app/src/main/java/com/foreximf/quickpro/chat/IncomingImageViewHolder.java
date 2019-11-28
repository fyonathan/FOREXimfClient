package com.foreximf.quickpro.chat;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.foreximf.quickpro.R;
import com.foreximf.quickpro.chat.model.ChatMessageComplete;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.utils.RoundedImageView;

import java.text.SimpleDateFormat;

public class IncomingImageViewHolder extends MessageHolders.IncomingTextMessageViewHolder<ChatMessageComplete> {

    RoundedImageView roundedImageView;
    public IncomingImageViewHolder(View view, Object payload) { super(view, payload); }

    @Override
    public void onBind(ChatMessageComplete message) {
        super.onBind(message);
        float scale = itemView.getResources().getDisplayMetrics().density;
        bubble.setPadding((int)(1 * scale + 0.5f), (int)(1 * scale + 0.5f), (int)(1 * scale + 0.5f), (int)(12 * scale + 0.5f));
        time.setTextColor(Color.parseColor("#999999"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        time.setText(simpleDateFormat.format(message.getCreatedAt()));
        roundedImageView = itemView.findViewById(R.id.image);
        if (roundedImageView != null) {
            roundedImageView.setCorners(40f, 40f, 10f, 10f);
            imageLoader.loadImage(roundedImageView, message.getImage(), payload);
        }
    }
}
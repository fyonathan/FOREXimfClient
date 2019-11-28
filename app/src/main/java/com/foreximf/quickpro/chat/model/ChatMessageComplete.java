package com.foreximf.quickpro.chat.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;
import android.graphics.Color;
import android.support.annotation.Nullable;

import com.foreximf.quickpro.services.WebSocketChat;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;
import java.util.List;

public class ChatMessageComplete implements IMessage, MessageContentType.Image, MessageContentType {

    @Embedded
    public ChatMessage chatMessage;

    @Relation(parentColumn = "id_chat_user", entityColumn = "id", entity = ChatUser.class)
    public List<ChatUser> chatUser;

    public ChatMessageComplete() { }

    @Nullable
    @Override
    public String getImageUrl() { return null; }

    public String getImage() { return this.chatMessage.getImage(); }

    @Override
    public String getId() {
        return this.chatMessage.id;
    }

    @Override
    public String getText() {
        return this.chatMessage.getLabelMessage();
    }

    @Override
    public IUser getUser() {
        if (chatUser == null || chatUser.isEmpty()) {
            WebSocketChat.requestUser(this.chatMessage.getId_chat_user());
            return new ChatUser(this.chatMessage.getId_chat_user());
        }
        return this.chatUser.get(0);
    }

    @Override
    public Date getCreatedAt() {
        return this.chatMessage.getTime();
    }

    public int getReadColor() {
        if (this.chatMessage.getRead_count() > 0) {
            return Color.parseColor("#ffbc25");
        } else if (this.chatMessage.getSent_count() > 0) {
            return Color.parseColor("#cacaca");
        } else {
            return Color.parseColor("#ff8888");
        }
    }

    public boolean isTemporary() {
        return this.chatMessage.getType().startsWith("Temporary");
    }
}

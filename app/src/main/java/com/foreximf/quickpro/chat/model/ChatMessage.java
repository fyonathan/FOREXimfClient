package com.foreximf.quickpro.chat.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.foreximf.quickpro.util.DateConverter;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;

@Entity(tableName = "chat_message")
public class ChatMessage implements IMessage, MessageContentType.Image {

    @PrimaryKey @NonNull
    public String id;
    private String id_chat_thread;
    private String id_chat_user;
    @Ignore
    private IUser user;
    private String type;
    private String message;
    private int is_read;
    private int sent_count;
    private int read_count;
    @TypeConverters(DateConverter.class)
    private Date time;

    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return message;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    public void setUser(IUser user) {
        this.user = user;
    }

    @Override
    public Date getCreatedAt() {
        return time;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_chat_thread() {
        return id_chat_thread;
    }

    public void setId_chat_thread(String id_chat_thread) {
        this.id_chat_thread = id_chat_thread;
    }

    public String getId_chat_user() {
        return id_chat_user;
    }

    public void setId_chat_user(String id_chat_user) {
        this.id_chat_user = id_chat_user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public int getSent_count() {
        return sent_count;
    }

    public void setSent_count(int sent_count) {
        this.sent_count = sent_count;
    }

    public int getRead_count() {
        return read_count;
    }

    public void setRead_count(int read_count) {
        this.read_count = read_count;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Nullable
    @Override
    public String getImageUrl() {
        return null;
    }
}

package com.foreximf.quickpro.chat.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.foreximf.quickpro.util.DateConverter;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import org.json.JSONObject;

import java.util.Date;

@Entity(tableName = "chat_message")
public class ChatMessage {

    @PrimaryKey @NonNull
    public String id;
    private String id_chat_thread;
    private String id_chat_user;
    private String type;
    private String message;
    private int is_read;
    private int sent_count;
    private int read_count;
    @TypeConverters(DateConverter.class)
    private Date time;

    public ChatMessage() { }

    public ChatMessage(JSONObject obj) {
        try {
            this.id = obj.getString("id");
            this.id_chat_thread = obj.optString("thread");
            this.id_chat_user = obj.optString("user");
            this.type = obj.optString("type");
            this.message = obj.optString("message");
            this.is_read = obj.optInt("is_read");
            if (this.is_read == 1) {
                this.is_read = 2;
            }
            this.sent_count = obj.optInt("sent_count");
            this.read_count = obj.optInt("read_count");
            this.time = new Date(obj.optLong("utime"));
        } catch (Exception ex) {

        }
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
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

    public String getLabelMessage() {
        if (type.equals("Image") || type.equals("Temporary:Image")) {
            try {
                JSONObject obj = new JSONObject(message);
                return obj.getString("label");
            } catch (Exception ex) {}
        }
        return message;
    }

    public String getImage() {
        if (type.equals("Image") || type.equals("Temporary:Image")) {
            try {
                JSONObject obj = new JSONObject(message);
                return obj.getString("image");
            } catch (Exception ex) {}
        }
        return null;
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
}

package com.foreximf.quickpro.chat.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.foreximf.quickpro.util.DateConverter;
import com.stfalcon.chatkit.commons.models.IUser;

import org.json.JSONObject;

import java.util.Date;

@Entity(tableName = "chat_user")
public class ChatUser implements IUser {

    @PrimaryKey @NonNull
    public String id;
    private String name;
    private String avatar;
    private String status;
    @TypeConverters(DateConverter.class)
    private Date last_online;

    public ChatUser() { }

    public ChatUser(String id) {
        this.id = id;
    }

    public ChatUser(JSONObject obj) {
        try {
            this.id = obj.getString("id");
            this.name = obj.optString("name");
            this.avatar = obj.optString("avatar");
            this.status = obj.optString("status");
            this.last_online = new Date(obj.optLong("last_online"));
        } catch (Exception ex) {

        }
    }

    @Override
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLast_online() {
        return last_online;
    }

    public void setLast_online(Date last_online) {
        this.last_online = last_online;
    }
}

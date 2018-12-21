package com.foreximf.quickpro.chat.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.foreximf.quickpro.util.DateConverter;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

@Entity(tableName = "chat_user")
public class ChatUser implements IUser {

    @PrimaryKey @NonNull
    public String id;
    private String nama;
    private String avatar;
    private int status;
    @TypeConverters(DateConverter.class)
    private Date last_online;

    @Override
    public String getName() {
        return nama;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getLast_online() {
        return last_online;
    }

    public void setLast_online(Date last_online) {
        this.last_online = last_online;
    }
}

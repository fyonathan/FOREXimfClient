package com.foreximf.quickpro.chat.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;
import android.util.Log;

import com.foreximf.quickpro.database.ForexImfAppDatabase;
import com.foreximf.quickpro.util.DateConverter;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

@Entity(tableName = "chat_thread")
public class ChatThread {

    @PrimaryKey @NonNull
    public String id;
    private String id_chat_department;
    private String id_last_message;
    private String type;
    private String name;
    private String avatar;
    private int status;
    private int unread;
    @TypeConverters(DateConverter.class)
    private Date updated_at;

    public ChatThread() {}

    public ChatThread(JSONObject obj) {
        try {
            this.id = obj.getString("id");
            this.id_chat_department = obj.optString("department");
            this.type = obj.optString("type");
            this.name = obj.optString("name");
            this.avatar = obj.optString("avatar");
            this.status = obj.optInt("status");
            this.updated_at = new Date(obj.optLong("updated_at"));
            this.unread = obj.optInt("unread");
        } catch (Exception ex) {

        }
        Log.d("ChatThread", this.toJSON().toString());
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", this.id);
            obj.put("department", this.id_chat_department);
            obj.put("last_message", this.id_last_message);
            obj.put("type", this.type);
            obj.put("name", this.name);
            obj.put("avatar", this.avatar);
            obj.put("status", this.status);
            obj.put("updated_at", this.updated_at.getTime());
            obj.put("unread", this.unread);
        } catch (Exception ex) { }
        return obj;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getId_chat_department() {
        return id_chat_department;
    }

    public void setId_chat_department(String id_chat_department) {
        this.id_chat_department = id_chat_department;
    }

    public String getId_last_message() {
        return id_last_message;
    }

    public void setId_last_message(String id_last_message) {
        this.id_last_message = id_last_message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }
}

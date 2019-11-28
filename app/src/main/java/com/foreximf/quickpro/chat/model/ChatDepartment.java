package com.foreximf.quickpro.chat.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.json.JSONObject;

@Entity(tableName = "chat_department")
public class ChatDepartment {

    @PrimaryKey @NonNull
    public String id;
    private String name;
    private String avatar;
    private String description;

    public ChatDepartment() {

    }

    public ChatDepartment(JSONObject obj) {
        try {
            this.id = obj.getString("id");
            this.name = obj.optString("name");
            this.avatar = obj.optString("avatar");
            this.description = obj.optString("description");
        } catch (Exception ex) {

        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

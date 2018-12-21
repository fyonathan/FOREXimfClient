package com.foreximf.quickpro.chat.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "chat_department")
public class ChatDepartment implements IDialog {

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
            this.name = obj.getString("name");
            this.avatar = obj.getString("avatar");
            this.description = obj.getString("description");
        } catch (Exception ex) {

        }
    }

    public String getId() {
        return id;
    }

    @Override
    public String getDialogPhoto() {
        return this.avatar;
    }

    @Override
    public String getDialogName() {
        return this.name;
    }

    @Override
    public List<? extends IUser> getUsers() {
        return new ArrayList<>();
    }

    @Override
    public IMessage getLastMessage() {
        return null;
    }

    @Override
    public void setLastMessage(IMessage message) {

    }

    @Override
    public int getUnreadCount() {
        return 0;
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

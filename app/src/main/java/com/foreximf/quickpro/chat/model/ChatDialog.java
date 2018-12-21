package com.foreximf.quickpro.chat.model;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.List;

public class ChatDialog implements IDialog {

    private String id;
    private String avatar;
    private String name;
    private List<IUser> users;
    private IMessage last_message;
    private int unread;

    public ChatDialog(String id, String name) {
        this.id = id;
        this.name = name;
        users = new ArrayList<IUser>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDialogPhoto() {
        return avatar;
         
    }

    public void setDialogPhoto(String url) {
        this.avatar = url;
    }

    @Override
    public String getDialogName() {
        return name;
    }

    @Override
    public List<? extends IUser> getUsers() {
        return users;
    }

    public void addUser(IUser user) {
        this.users.add(user);
    }

    @Override
    public IMessage getLastMessage() {
        return last_message;
    }

    @Override
    public void setLastMessage(IMessage message) {
        this.last_message = message;
    }

    @Override
    public int getUnreadCount() {
        return unread;
    }

    public void setUnreadCount(int unread) {
        this.unread = unread;
    }
}

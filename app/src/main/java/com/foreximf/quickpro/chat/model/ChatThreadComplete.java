package com.foreximf.quickpro.chat.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;
import android.util.Log;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.List;

public class ChatThreadComplete implements IDialog {

    @Embedded
    public ChatThread chatThread;

    @Relation(parentColumn = "id_chat_department", entityColumn = "id", entity = ChatDepartment.class)
    public List<ChatDepartment> chatDepartment;

    @Relation(parentColumn = "id_last_message", entityColumn = "id", entity = ChatMessage.class)
    public List<ChatMessageComplete> chatMessage;

    public ChatThreadComplete() { }

    @Override
    public String getId() {
        return chatThread.getId();
    }

    @Override
    public String getDialogPhoto() {
        return chatThread.getAvatar();
    }

    @Override
    public String getDialogName() {
        return chatThread.getName();
    }

    @Override
    public List<? extends IUser> getUsers() {
        return new ArrayList<>();
    }

    @Override
    public IMessage getLastMessage() {
        if (chatMessage == null || chatMessage.isEmpty()) {
            return null;
        }
        return chatMessage.get(0);
    }

    @Override
    public void setLastMessage(IMessage message) {

    }

    @Override
    public int getUnreadCount() {
        return chatThread.getUnread();
    }
}

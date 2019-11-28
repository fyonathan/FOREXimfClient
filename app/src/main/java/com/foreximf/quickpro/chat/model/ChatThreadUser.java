package com.foreximf.quickpro.chat.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

@Entity(tableName = "chat_thread_user",
        primaryKeys = {"id_chat_thread", "id_chat_user"},
        foreignKeys = {
            @ForeignKey(entity = ChatThread.class,
                        parentColumns = "id",
                        childColumns = "id_chat_thread"),
            @ForeignKey(entity = ChatUser.class,
                        parentColumns = "id",
                        childColumns = "id_chat_user")
        })
public class ChatThreadUser {

    public final int id_chat_thread;
    public final int id_chat_user;

    public ChatThreadUser(final int id_chat_thread, final int id_chat_user) {
        this.id_chat_thread = id_chat_thread;
        this.id_chat_user = id_chat_user;
    }
}

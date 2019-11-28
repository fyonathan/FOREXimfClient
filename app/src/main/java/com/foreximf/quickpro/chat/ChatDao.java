package com.foreximf.quickpro.chat;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.foreximf.quickpro.chat.model.ChatDepartment;
import com.foreximf.quickpro.chat.model.ChatMessage;
import com.foreximf.quickpro.chat.model.ChatMessageComplete;
import com.foreximf.quickpro.chat.model.ChatThread;
import com.foreximf.quickpro.chat.model.ChatThreadComplete;
import com.foreximf.quickpro.chat.model.ChatUser;

import java.util.Date;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public abstract class ChatDao {

    @Query("SELECT * FROM chat_department")
    public abstract LiveData<List<ChatDepartment>> getAllDepartment();

    //id AS t_id, id_chat_department AS t_id_chat_department, id_last_message AS t_id_last_message, type AS t_type, name AS t_name, avatar AS t_avatar, status AS t_status, updated_at AS t_updated_at, unread AS t_unread

    @Transaction
    @Query("SELECT * FROM chat_thread ORDER BY updated_at DESC")
    public abstract LiveData<List<ChatThreadComplete>> getAllThread();

    @Query("SELECT * FROM chat_message WHERE id_chat_thread=:id ORDER BY time DESC")
    public abstract LiveData<List<ChatMessageComplete>> getAllMessage(String id);

    @Query("SELECT * FROM chat_thread WHERE status >= 0")
    public abstract List<ChatThread> getActiveThread();

    @Query("SELECT * FROM chat_thread WHERE type='Temporary'")
    public abstract List<ChatThread> getTemporaryThread();

    @Query("UPDATE chat_thread SET id=:new_id, type=:type, name=:name, avatar=:avatar, status=:status, unread=:unread, updated_at=:updated_at WHERE id=:id")
    public abstract void updateTemporaryThread(String id, String new_id, String type, String name, String avatar, int status, int unread, Date updated_at);

    @Query("SELECT * FROM chat_department")
    public abstract List<ChatDepartment> getDepartments();

    @Query("UPDATE chat_thread SET id_last_message=(SELECT id FROM chat_message WHERE id_chat_thread=chat_thread.id ORDER BY time DESC LIMIT 1), unread=(SELECT COUNT(DISTINCT id) FROM chat_message WHERE id_chat_thread=chat_thread.id AND id_chat_user!=:my_id AND is_read=0), updated_at=(SELECT time FROM chat_message WHERE id_chat_thread=chat_thread.id ORDER BY time DESC LIMIT 1)")
    public abstract void syncLastMessage(String my_id);

    @Query("UPDATE chat_message SET sent_count=1 WHERE id IN (:ids)")
    public abstract void setSentMessage(String... ids);

    @Query("UPDATE chat_message SET sent_count=1, read_count=1, is_read=:read WHERE id IN (:ids)")
    public abstract void setReadMessage(int read, String... ids);

    @Query("SELECT * FROM chat_message WHERE id_chat_user != :id_user AND id IN (:ids) AND is_read <= 1")
    public abstract List<ChatMessage> getIsReadMessage(String id_user, String... ids);

    @Query("UPDATE chat_message SET is_read=1 WHERE id IN (:ids)")
    public abstract void setIsReadMessage(String... ids);

    @Query("SELECT * FROM chat_message WHERE id=:id")
    public abstract ChatMessageComplete getMessage(String id);

    @Query("SELECT * FROM chat_message WHERE id_chat_thread=:id ORDER BY time DESC LIMIT :limit OFFSET :offset")
    public abstract List<ChatMessageComplete> getMessagesByThread(String id, int limit, int offset);

    @Query("SELECT * FROM chat_thread WHERE status >= 0 AND id_chat_department=:id LIMIT 1")
    public abstract ChatThread findActiveThreadByDepartment(String id);

    @Query("SELECT * FROM chat_department WHERE id=:id")
    public abstract ChatDepartment findDepartment(String id);

    @Query("SELECT * FROM chat_message WHERE id=:id")
    public abstract ChatMessage findMessage(String id);

    @Query("SELECT * FROM chat_thread WHERE id=:id")
    public abstract ChatThread findThread(String id);

    @Query("SELECT * FROM chat_user WHERE id=:id")
    public abstract ChatUser findUser(String id);

    @Insert(onConflict = REPLACE)
    public abstract void add(ChatDepartment chatDepartment);

    @Insert(onConflict = REPLACE)
    public abstract void add(ChatMessage chatMessage);

    @Insert(onConflict = REPLACE)
    public abstract void add(ChatThread chatThread);

    @Insert(onConflict = REPLACE)
    public abstract void add(ChatUser chatUser);

    @Update(onConflict = REPLACE)
    public abstract void update(ChatDepartment chatDepartment);

    @Update(onConflict = REPLACE)
    public abstract void update(ChatMessage chatMessage);

    @Update(onConflict = REPLACE)
    public abstract void update(ChatThread chatThread);

    @Update(onConflict = REPLACE)
    public abstract void update(ChatUser chatUser);

    @Delete
    public abstract void delete(ChatDepartment chatDepartment);

    @Delete
    public abstract void delete(ChatMessage chatMessage);

    @Delete
    public abstract void delete(ChatThread chatThread);

    @Delete
    public abstract void delete(ChatUser chatUser);
}

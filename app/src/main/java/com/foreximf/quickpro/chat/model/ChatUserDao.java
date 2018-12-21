package com.foreximf.quickpro.chat.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ChatUserDao {
    @Query("select * from chat_user")
    LiveData<List<ChatUser>> getAll();

    @Insert(onConflict = REPLACE)
    void add(ChatUser chatUser);

    @Update
    void update(ChatUser chatUser);

    @Delete
    void delete(ChatUser chatUser);
}

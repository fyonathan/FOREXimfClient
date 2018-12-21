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
public interface ChatThreadDao {

    @Query("select * from chat_thread")
    LiveData<List<ChatThread>> getAll();

    @Insert(onConflict = REPLACE)
    void add(ChatThread chatThread);

    @Update
    void update(ChatThread chatThread);

    @Delete
    void delete(ChatThread chatThread);
}

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
public interface ChatDepartmentDao {
    @Query("select * from chat_department")
    LiveData<List<ChatDepartment>> getAll();

    @Insert(onConflict = REPLACE)
    void add(ChatDepartment chatDepartment);

    @Update
    void update(ChatDepartment chatDepartment);

    @Delete
    void delete(ChatDepartment chatDepartment);
}

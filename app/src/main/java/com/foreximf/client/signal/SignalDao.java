package com.foreximf.client.signal;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface SignalDao {

    @Query("select * from Signal")
    LiveData<List<Signal>> getAllSignal();

    @Query("select * from (select * from Signal order by lastUpdate desc limit :number) order by lastUpdate asc")
    LiveData<List<Signal>> getSignalByCount(int number);

    @Query("select count(id) from Signal where read = 0")
    int getUnreadCount();

    @Insert(onConflict = REPLACE)
    void addSignal(Signal signal);

    @Query("update Signal set title = :title, content = :content where id = :id")
    void updateSignal(String title, String content, int id);

    @Query("update Signal set read = 1 where read = 0")
    void updateSignal();

    @Update
    void updateSignal(Signal signal);

    @Delete
    void deleteSignal(Signal signal);
}
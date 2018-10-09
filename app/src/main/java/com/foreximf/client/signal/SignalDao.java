package com.foreximf.client.signal;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
//import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * A data access object class, responsible in managing the query
 * of SQLite Database, method are called via {@link SignalRepository}
 */
@Dao
public interface SignalDao {

    @Query("select * from Signal")
    DataSource.Factory<Integer, Signal> getAllSignal();

    @Query("select * from Signal where serverId = :serverId")
    Signal getSignalByServerId(int serverId);

//    @Query("select * from (select * from Signal order by lastUpdate desc limit :number) order by lastUpdate asc")
//    LiveData<List<Signal>> getSignalByCount(int number);
//
//    @Query("select * from Signal where status in (:status)")
//    LiveData<List<Signal>> getSignalByStatus(List<Integer> status);

    @Query("select * from Signal where status in (:status) and currencyPair in (:pair) and signalGroup in (:group) order by status desc, lastUpdate asc")
//    LiveData<List<Signal>> getSignalByCondition(List<Integer> status, List<Integer> pair, List<Integer> group);
    DataSource.Factory<Integer, Signal> getSignalByCondition(List<Integer> status, List<Integer> pair, List<Integer> group);
//    LivePagedListBuilder<Integer, Signal> getSignalByCondition(List<Integer> status, List<Integer> pair, List<Integer> group);

    @Query("select count(id) from Signal where read = 0")
    int getUnreadCount();

    @Insert(onConflict = REPLACE)
    void addSignal(Signal signal);

//    @Query("update Signal set title = :title, content = :content where id = :id")
//    void updateSignal(String title, String content, int id);

    @Query("update Signal set read = 1 where read = 0")
    void updateSignal();

    @Update
    void updateSignal(Signal signal);
//
//    @Delete
//    void deleteSignal(Signal signal);
}

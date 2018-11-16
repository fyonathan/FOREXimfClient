package com.foreximf.quickpro.news;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface NewsDao {

    @Query("select * from News order by id asc")
    LiveData<List<News>> getAllNews();

    @Query("select count(*) from News")
    int getNewsCount();

    @Query("select max(id) as id, title, content, type, lastUpdate from News group by type")
    LiveData<List<News>> getCurrentNews();

    @Query("select * from News where type = :type")
    News getNewsByType(String type);

    @Insert(onConflict = REPLACE)
    void addNews(News news);

    @Query("update News set title = :title, content = :content, lastUpdate = :date where type = :type")
    void updateNews(String title, String content, Date date, String type);

    @Update
    void updateNews(News news);

    @Delete
    void deleteNews(News news);

    @Query("delete from News")
    void deleteAll();
}

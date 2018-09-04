package com.foreximf.client.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.foreximf.client.news.News;
import com.foreximf.client.news.NewsDao;
import com.foreximf.client.signal.Signal;
import com.foreximf.client.signal.SignalDao;
import com.foreximf.client.util.DateConverter;

@Database(entities = {News.class, Signal.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class ForexImfAppDatabase extends RoomDatabase {

    private static ForexImfAppDatabase INSTANCE;

    public static ForexImfAppDatabase getDatabase(Context context) {
        if(INSTANCE == null) {
            synchronized (ForexImfAppDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ForexImfAppDatabase.class, "foreximf_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract NewsDao newsModel();
    public abstract SignalDao signalModel();
}

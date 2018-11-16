package com.foreximf.quickpro.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.foreximf.quickpro.camarilla.Camarilla;
import com.foreximf.quickpro.camarilla.CamarillaDao;
import com.foreximf.quickpro.news.News;
import com.foreximf.quickpro.news.NewsDao;
import com.foreximf.quickpro.signal.Signal;
import com.foreximf.quickpro.signal.SignalDao;
import com.foreximf.quickpro.util.DateConverter;

@Database(entities = {News.class, Signal.class, Camarilla.class}, version = 2)
@TypeConverters({DateConverter.class})
public abstract class ForexImfAppDatabase extends RoomDatabase {

    private static ForexImfAppDatabase INSTANCE;

    public static ForexImfAppDatabase getDatabase(Context context) {
        if(INSTANCE == null) {
            synchronized (ForexImfAppDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ForexImfAppDatabase.class, "foreximf_db")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE signal "
                    + " ADD COLUMN keterangan VARCHAR");
        }
    };



    public abstract NewsDao newsModel();
    public abstract SignalDao signalModel();
    public abstract CamarillaDao camarillaModel();
}

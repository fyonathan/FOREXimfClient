package com.foreximf.quickpro.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.foreximf.quickpro.camarilla.Camarilla;
import com.foreximf.quickpro.camarilla.CamarillaDao;
import com.foreximf.quickpro.chat.ChatDao;
import com.foreximf.quickpro.chat.model.ChatDepartment;
import com.foreximf.quickpro.chat.model.ChatMessage;
import com.foreximf.quickpro.chat.model.ChatThread;
import com.foreximf.quickpro.chat.model.ChatUser;
import com.foreximf.quickpro.news.News;
import com.foreximf.quickpro.news.NewsDao;
import com.foreximf.quickpro.signal.Signal;
import com.foreximf.quickpro.signal.SignalDao;
import com.foreximf.quickpro.util.DateConverter;

@Database(entities = {News.class, Signal.class, Camarilla.class, ChatDepartment.class, ChatMessage.class, ChatThread.class, ChatUser.class}, version = 4)
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
                            .addMigrations(MIGRATION_2_3)
                            .addMigrations(MIGRATION_3_4)
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

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("UPDATE signal "
                    + " SET status = 4 WHERE status = 3");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE camarilla "
                    + " ADD COLUMN open REAL NOT NULL DEFAULT 0");
            database.execSQL("CREATE TABLE IF NOT EXISTS `chat_department` (`id` TEXT NOT NULL, `name` TEXT, `avatar` TEXT, `description` TEXT, PRIMARY KEY(`id`))");
            database.execSQL("CREATE TABLE IF NOT EXISTS `chat_message` (`id` TEXT NOT NULL, `id_chat_thread` TEXT, `id_chat_user` TEXT, `type` TEXT, `message` TEXT, `is_read` INTEGER NOT NULL, `sent_count` INTEGER NOT NULL, `read_count` INTEGER NOT NULL, `time` INTEGER, PRIMARY KEY(`id`))");
            database.execSQL("CREATE TABLE IF NOT EXISTS `chat_thread` (`id` TEXT NOT NULL, `id_chat_department` TEXT, `id_last_message` TEXT, `type` TEXT, `name` TEXT, `avatar` TEXT, `status` INTEGER NOT NULL, `unread` INTEGER NOT NULL, `updated_at` INTEGER, PRIMARY KEY(`id`))");
            database.execSQL("CREATE TABLE IF NOT EXISTS `chat_user` (`id` TEXT NOT NULL, `name` TEXT, `avatar` TEXT, `status` TEXT, `last_online` INTEGER, PRIMARY KEY(`id`))");
        }
    };

    public abstract NewsDao newsModel();
    public abstract SignalDao signalModel();
    public abstract CamarillaDao camarillaModel();

    public abstract ChatDao chatDao();
}

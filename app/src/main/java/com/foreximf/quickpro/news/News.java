package com.foreximf.quickpro.news;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.foreximf.quickpro.util.DateConverter;

import java.util.Date;

@Entity(tableName = "news")
public class News {

    @PrimaryKey(autoGenerate = true)
    public int id;
    private String type;
    private String title;
    private String content;
    private String author;
    @TypeConverters(DateConverter.class)
    private Date lastUpdate;

    public News(String type, String title, String content, String author, Date lastUpdate) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.author = author;
        this.lastUpdate = lastUpdate;
    }

    @Ignore
    public News(int id, @NonNull String type, String title, String content, String author, Date lastUpdate) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.content = content;
        this.author = author;
        this.lastUpdate = lastUpdate;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public static String typeConverter(int type) {
        switch(type) {
            case 0 : {
                return "Signal Trading";
            }
            case 1 : {
                return "Breaking News";
            }
            default : {
                return "Analisa Harian";
            }
        }
    }
}

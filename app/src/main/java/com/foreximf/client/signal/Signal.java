package com.foreximf.client.signal;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.foreximf.client.util.DateConverter;

import java.util.Date;

@Entity(tableName = "signal")
public class Signal {

    @PrimaryKey(autoGenerate = true)
    public int id;
    private String type;
    private String title;
    private String content;
    @TypeConverters(DateConverter.class)
    private Date lastUpdate;
    private int read;

    public Signal() {

    }

    public Signal(String title, String content, String type, Date lastUpdate, int read) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.lastUpdate = lastUpdate;
        this.read = read;
    }

    @Ignore
    public Signal(int id, String title, String content, String type, Date lastUpdate, int read) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
        this.lastUpdate = lastUpdate;
        this.read = read;
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

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public int getRead() {
        return read;
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

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setRead(int read) {
        this.read = read;
    }
}

package com.foreximf.client.assistant;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.foreximf.client.util.DateConverter;

import java.util.Date;

@Entity(tableName = "alarm")
public class Alarm {
    @PrimaryKey(autoGenerate = true)
    public int id;
    private String name;
    @TypeConverters(DateConverter.class)
    private Date reminderTime;
    private int alarmType;
    private int status;
    private String notes;

    public Alarm() {

    }

    public Alarm(String name, Date reminderTime, int alarmType, int status, String notes) {
        this.name = name;
        this.reminderTime = reminderTime;
        this.alarmType = alarmType;
        this.status = status;
        this.notes = notes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(Date reminderTime) {
        this.reminderTime = reminderTime;
    }

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

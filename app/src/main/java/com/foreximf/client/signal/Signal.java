package com.foreximf.client.signal;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.foreximf.client.R;
import com.foreximf.client.util.DateConverter;

import java.util.Date;

/**
 * A basic entity class used as container for data
 * received from {@link SignalDao} and used for
 * element in array list in {@link SignalRecyclerViewAdapter}
 */
@Entity(tableName = "signal")
public class Signal {

    @PrimaryKey(autoGenerate = true)
    public int id;
    private int currencyPair;
    private String title;
    private String content;
    @TypeConverters(DateConverter.class)
    private Date lastUpdate;
    private int orderType;
    private int result;
    private int status;
    private int signalGroup;
    private int read;
    private int serverId;

    public Signal() {

    }

    public Signal(String title, String content, Date lastUpdate, int read, int currencyPair, int orderType, int result, int status, int signalGroup, int serverId) {
        this.title = title;
        this.content = content;
        this.lastUpdate = lastUpdate;
        this.read = read;
        this.currencyPair = currencyPair;
        this.orderType = orderType;
        this.result = result;
        this.status = status;
        this.signalGroup = signalGroup;
        this.serverId = serverId;
    }

    @Ignore
    public Signal(int id, String title, String content, Date lastUpdate, int read, int result, int status, int signalGroup, int serverId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.lastUpdate = lastUpdate;
        this.read = read;
        this.result = result;
        this.status = status;
        this.signalGroup = signalGroup;
        this.serverId = serverId;
    }

    public int getId() {
        return id;
    }

    public int getServerId() {
        return serverId;
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

    public int getCurrencyPair() {
        return currencyPair;
    }

    public String getCurrencyPairString() {
        switch(currencyPair) {
            case 1 : {
                return "GBPUSD";
            }
            case 2 : {
                return "EURUSD";
            }
            case 3 : {
                return "AUDUSD";
            }
            case 4 : {
                return "USDCAD";
            }
            case 5 : {
                return "USDCHF";
            }
            case 6 : {
                return "USDJPY";
            }
            case 7 : {
                return "Emas";
            }
            case 8 : {
                return "Perak";
            }
            default : {
                return "Oil";
            }
        }
    }

    public int getOrderType() {
        return orderType;
    }

//    public String getOrderTypeString() {
//        switch(orderType) {
//            case 1 : {
//                return "Buy";
//            }
//            default : {
//                return "Sell";
//            }
//        }
//    }

    public int getOrderTypeDrawable() {
        switch(orderType) {
            case 1 : {
                return R.mipmap.ic_up_arrow_green;
            }
            default : {
                return R.mipmap.ic_down_arrow_red;
            }
        }
    }

    public int getResult() {
        return result;
    }

    public int getStatus() {
        return status;
    }

    public int getSignalGroup() {
        return signalGroup;
    }

    public String getSignalGroupString() {
        switch(signalGroup) {
            case 1 : return "MSARS";
            case 2 : return "Atrium";
            default : return "EMAStoch";
        }
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

    public void setCurrencyPair(int currencyPair) {
        this.currencyPair = currencyPair;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setSignalGroup(int signalGroup) {
        this.signalGroup = signalGroup;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }
}

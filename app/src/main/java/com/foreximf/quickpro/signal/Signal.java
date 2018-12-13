package com.foreximf.quickpro.signal;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.foreximf.quickpro.R;
import com.foreximf.quickpro.util.DateConverter;

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
    private Date createdTime;
    private int orderType;
    private int result;
    private String keterangan;
    private int status;
    private int signalGroup;
    private int read;
    private int serverId;

    public Signal() {

    }

    public Signal(int currencyPair, String title, String content, Date createdTime, int orderType, int result, String keterangan, int status, int signalGroup, int read, int serverId) {
        this.currencyPair = currencyPair;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.orderType = orderType;
        this.result = result;
        this.keterangan = keterangan;
        this.status = status;
        this.signalGroup = signalGroup;
        this.read = read;
        this.serverId = serverId;
    }

    @Ignore
    public Signal(int id, int currencyPair, String title, String content, Date createdTime, int orderType, int result, String keterangan, int status, int signalGroup, int read, int serverId) {
        this.id = id;
        this.currencyPair = currencyPair;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.orderType = orderType;
        this.result = result;
        this.keterangan = keterangan;
        this.status = status;
        this.signalGroup = signalGroup;
        this.read = read;
        this.serverId = serverId;
    }

//    public Signal(String title, String content, Date createdTime, int read, int currencyPair, int orderType, int result, int status, int signalGroup, int serverId) {
//        this.title = title;
//        this.content = content;
//        this.createdTime = createdTime;
//        this.read = read;
//        this.currencyPair = currencyPair;
//        this.orderType = orderType;
//        this.result = result;
//        this.status = status;
//        this.signalGroup = signalGroup;
//        this.serverId = serverId;
//    }
//
//    @Ignore
//    public Signal(int id, String title, String content, Date createdTime, int read, int currencyPair, int orderType, int result, int status, int signalGroup, int serverId) {
//        this.id = id;
//        this.title = title;
//        this.content = content;
//        this.createdTime = createdTime;
//        this.read = read;
//        this.currencyPair = currencyPair;
//        this.orderType = orderType;
//        this.result = result;
//        this.status = status;
//        this.signalGroup = signalGroup;
//        this.serverId = serverId;
//    }

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

    public Date getCreatedTime() {
        return createdTime;
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
                return "AUDJPY";
            }
            case 2 : {
                return "AUDNZD";
            }
            case 3 : {
                return "AUDUSD";
            }
            case 4 : {
                return "CHFJPY";
            }
            case 5 : {
                return "EURAUD";
            }
            case 6 : {
                return "EURCAD";
            }
            case 7 : {
                return "EURGBP";
            }
            case 8 : {
                return "EURJPY";
            }
            case 9 : {
                return "EURUSD";
            }
            case 10 : {
                return "GBPAUD";
            }
            case 11 : {
                return "GBPCHF";
            }
            case 12 : {
                return "GBPJPY";
            }
            case 13 : {
                return "GBPUSD";
            }
            case 14 : {
                return "NZDUSD";
            }
            case 15 : {
                return "USDCAD";
            }
            case 16 : {
                return "USDCHF";
            }
            case 17 : {
                return "USDJPY";
            }
            case 18 : {
                return "XAG";
            }
            case 19 : {
                return "XUL";
            }
            default : {
                return "CLSK";
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

    public String getKeterangan() {
        return keterangan;
    }

    public int getStatus() {
        return status;
    }

    public int getSignalGroup() {
        return signalGroup;
    }

    public String getSignalGroupString() {
        switch(signalGroup) {
            case 1 : return "FiboStoch";
            case 2 : return "Atrium";
            case 3 : return "EMAStoch";
            case 4 : return "Price Pattern";
            default : return "Counter Trend";
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
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

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}

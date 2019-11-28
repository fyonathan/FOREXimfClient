package com.foreximf.quickpro.camarilla;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.foreximf.quickpro.util.DateConverter;

import java.util.Date;

@Entity(tableName = "camarilla")
public class Camarilla {

    @PrimaryKey(autoGenerate = true)
    public int id;
    private String pairName;
    private float pivot;
    private float open;
    private float sellArea;
    private float sellTp1;
    private float sellTp2;
    private float sellSl;
    private float buyArea;
    private float buyTp1;
    private float buyTp2;
    private float buySl;
    private float buyBreakoutArea;
    private float buyBreakoutTp1;
    private float buyBreakoutTp2;
    private float buyBreakoutSl;
    private float sellBreakoutArea;
    private float sellBreakoutTp1;
    private float sellBreakoutTp2;
    private float sellBreakoutSl;
    @TypeConverters(DateConverter.class)
    private Date lastUpdate;
    private int serverId;

    public Camarilla(String pairName, float pivot, float open, float sellArea, float sellTp1, float sellTp2, float sellSl, float buyArea, float buyTp1, float buyTp2, float buySl, float buyBreakoutArea, float buyBreakoutTp1, float buyBreakoutTp2, float buyBreakoutSl, float sellBreakoutArea, float sellBreakoutTp1, float sellBreakoutTp2, float sellBreakoutSl, Date lastUpdate, int serverId) {
        this.pairName = pairName;
        this.pivot = pivot;
        this.open = open;
        this.sellArea = sellArea;
        this.sellTp1 = sellTp1;
        this.sellTp2 = sellTp2;
        this.sellSl = sellSl;
        this.buyArea = buyArea;
        this.buyTp1 = buyTp1;
        this.buyTp2 = buyTp2;
        this.buySl = buySl;
        this.buyBreakoutArea = buyBreakoutArea;
        this.buyBreakoutTp1 = buyBreakoutTp1;
        this.buyBreakoutTp2 = buyBreakoutTp2;
        this.buyBreakoutSl = buyBreakoutSl;
        this.sellBreakoutArea = sellBreakoutArea;
        this.sellBreakoutTp1 = sellBreakoutTp1;
        this.sellBreakoutTp2 = sellBreakoutTp2;
        this.sellBreakoutSl = sellBreakoutSl;
        this.lastUpdate = lastUpdate;
        this.serverId = serverId;
    }

    @Ignore
    public Camarilla(int id, String pairName, float pivot, float open, float sellArea, float sellTp1, float sellTp2, float sellSl, float buyArea, float buyTp1, float buyTp2, float buySl, float buyBreakoutArea, float buyBreakoutTp1, float buyBreakoutTp2, float buyBreakoutSl, float sellBreakoutArea, float sellBreakoutTp1, float sellBreakoutTp2, float sellBreakoutSl, Date lastUpdate, int serverId) {
        this.id = id;
        this.pairName = pairName;
        this.pivot = pivot;
        this.open = open;
        this.sellArea = sellArea;
        this.sellTp1 = sellTp1;
        this.sellTp2 = sellTp2;
        this.sellSl = sellSl;
        this.buyArea = buyArea;
        this.buyTp1 = buyTp1;
        this.buyTp2 = buyTp2;
        this.buySl = buySl;
        this.buyBreakoutArea = buyBreakoutArea;
        this.buyBreakoutTp1 = buyBreakoutTp1;
        this.buyBreakoutTp2 = buyBreakoutTp2;
        this.buyBreakoutSl = buyBreakoutSl;
        this.sellBreakoutArea = sellBreakoutArea;
        this.sellBreakoutTp1 = sellBreakoutTp1;
        this.sellBreakoutTp2 = sellBreakoutTp2;
        this.sellBreakoutSl = sellBreakoutSl;
        this.lastUpdate = lastUpdate;
        this.serverId = serverId;
    }

    public String getPairName() {
        return pairName;
    }

    public void setPairName(String pairName) {
        this.pairName = pairName;
    }

    public float getPivot() {
        return pivot;
    }

    public void setPivot(float pivot) {
        this.pivot = pivot;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.pivot = open;
    }

    public float getSellArea() {
        return sellArea;
    }

    public void setSellArea(float sellArea) {
        this.sellArea = sellArea;
    }

    public float getSellTp1() {
        return sellTp1;
    }

    public void setSellTp1(float sellTp1) {
        this.sellTp1 = sellTp1;
    }

    public float getSellTp2() {
        return sellTp2;
    }

    public void setSellTp2(float sellTp2) {
        this.sellTp2 = sellTp2;
    }

    public float getSellSl() {
        return sellSl;
    }

    public void setSellSl(float sellSl) {
        this.sellSl = sellSl;
    }

    public float getBuyArea() {
        return buyArea;
    }

    public void setBuyArea(float buyArea) {
        this.buyArea = buyArea;
    }

    public float getBuyTp1() {
        return buyTp1;
    }

    public void setBuyTp1(float buyTp1) {
        this.buyTp1 = buyTp1;
    }

    public float getBuyTp2() {
        return buyTp2;
    }

    public void setBuyTp2(float buyTp2) {
        this.buyTp2 = buyTp2;
    }

    public float getBuySl() {
        return buySl;
    }

    public void setBuySl(float buySl) {
        this.buySl = buySl;
    }

    public float getBuyBreakoutArea() {
        return buyBreakoutArea;
    }

    public void setBuyBreakoutArea(float buyBreakoutArea) {
        this.buyBreakoutArea = buyBreakoutArea;
    }

    public float getBuyBreakoutTp1() {
        return buyBreakoutTp1;
    }

    public void setBuyBreakoutTp1(float buyBreakoutTp1) {
        this.buyBreakoutTp1 = buyBreakoutTp1;
    }

    public float getBuyBreakoutTp2() {
        return buyBreakoutTp2;
    }

    public void setBuyBreakoutTp2(float buyBreakoutTp2) {
        this.buyBreakoutTp2 = buyBreakoutTp2;
    }

    public float getBuyBreakoutSl() {
        return buyBreakoutSl;
    }

    public void setBuyBreakoutSl(float buyBreakoutSl) {
        this.buyBreakoutSl = buyBreakoutSl;
    }

    public float getSellBreakoutArea() {
        return sellBreakoutArea;
    }

    public void setSellBreakoutArea(float sellBreakoutArea) {
        this.sellBreakoutArea = sellBreakoutArea;
    }

    public float getSellBreakoutTp1() {
        return sellBreakoutTp1;
    }

    public void setSellBreakoutTp1(float sellBreakoutTp1) {
        this.sellBreakoutTp1 = sellBreakoutTp1;
    }

    public float getSellBreakoutTp2() {
        return sellBreakoutTp2;
    }

    public void setSellBreakoutTp2(float sellBreakoutTp2) {
        this.sellBreakoutTp2 = sellBreakoutTp2;
    }

    public float getSellBreakoutSl() {
        return sellBreakoutSl;
    }

    public void setSellBreakoutSl(float sellBreakoutSl) {
        this.sellBreakoutSl = sellBreakoutSl;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getServerId() {
        return serverId;
    }
}

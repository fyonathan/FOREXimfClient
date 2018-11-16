package com.foreximf.quickpro.camarilla;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface CamarillaDao {

    @Query("select * from Camarilla")
    LiveData<List<Camarilla>> getAllCamarilla();

    @Insert(onConflict = REPLACE)
    void addCamarilla(Camarilla camarilla);

    @Update
    void updateCamarilla(Camarilla camarilla);

    @Query("select * from Camarilla where serverId = :serverId")
    Camarilla getCamarillaByServerId(int serverId);
}

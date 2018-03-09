package com.example.m.myfirstapp.Data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;


@Dao
public interface SensorSampleDao {
    @Query("SELECT * FROM sensorsample")
    List<SensorSample> getAll();

    @Query("SELECT * FROM sensorsample WHERE uid IN (:userIds)")
    List<SensorSample> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM sensorsample WHERE timestamp LIKE :unixtimestamp LIMIT 1")
    SensorSample findByTime(int unixtimestamp);

    @Insert
    void insertAll(SensorSample... samples);

    @Delete
    void delete(SensorSample sample);
}
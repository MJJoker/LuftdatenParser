package com.example.m.myfirstapp.Data;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class SensorSample {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "pm2_5_raw")
    private String pm2_5_raw;

    @ColumnInfo(name = "pm10_raw")
    private String pm10_raw;

    @ColumnInfo(name = "humidity_raw")
    private String humidity_raw;

    @ColumnInfo(name = "temperatur_raw")
    private String temperatur_raw;

    @ColumnInfo(name = "wifidb_raw")
    private String wifidb_raw;

    @ColumnInfo(name = "wifipercent_raw")
    private String wifipercent_raw;

    @ColumnInfo(name = "pm2_5")
    private double pm2_5;

    @ColumnInfo(name = "pm10")
    private double pm10;

    @ColumnInfo(name = "humidity")
    private double humidity;

    @ColumnInfo(name = "temperatur")
    private double temperatur;

    @ColumnInfo(name = "wifidb")
    private double wifidb;

    @ColumnInfo(name = "wifipercent")
    private double wifipercent;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    @ColumnInfo(name = "sensorid")
    private int sensorid;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getPm2_5_raw() {
        return pm2_5_raw;
    }

    public void setPm2_5_raw(String pm2_5_raw) {
        this.pm2_5_raw = pm2_5_raw;
    }

    public String getPm10_raw() {
        return pm10_raw;
    }

    public void setPm10_raw(String pm10_raw) {
        this.pm10_raw = pm10_raw;
    }

    public String getHumidity_raw() {
        return humidity_raw;
    }

    public void setHumidity_raw(String humidity_raw) {
        this.humidity_raw = humidity_raw;
    }

    public String getTemperatur_raw() {
        return temperatur_raw;
    }

    public void setTemperatur_raw(String temperatur_raw) {
        this.temperatur_raw = temperatur_raw;
    }

    public String getWifidb_raw() {
        return wifidb_raw;
    }

    public void setWifidb_raw(String wifidb_raw) {
        this.wifidb_raw = wifidb_raw;
    }

    public String getWifipercent_raw() {
        return wifipercent_raw;
    }

    public void setWifipercent_raw(String wifipercent_raw) {
        this.wifipercent_raw = wifipercent_raw;
    }

    public double getPm2_5() {
        return pm2_5;
    }

    public void setPm2_5(double pm2_5) {
        this.pm2_5 = pm2_5;
    }

    public double getPm10() {
        return pm10;
    }

    public void setPm10(double pm10) {
        this.pm10 = pm10;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getTemperatur() {
        return temperatur;
    }

    public void setTemperatur(double temperatur) {
        this.temperatur = temperatur;
    }

    public double getWifidb() {
        return wifidb;
    }

    public void setWifidb(double wifidb) {
        this.wifidb = wifidb;
    }

    public double getWifipercent() {
        return wifipercent;
    }

    public void setWifipercent(double wifipercent) {
        this.wifipercent = wifipercent;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getSensorid() {
        return sensorid;
    }

    public void setSensorid(int sensorid) {
        this.sensorid = sensorid;
    }


    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.
}
package com.example.embedded.data.dto;

import java.io.Serializable;
import java.sql.Timestamp;

public class Data implements Serializable {
    private Long _ID ;
    private String userId;
    private String location;
    private String temperature;
    private String aroundInjection;
    private Timestamp time;
    public Data(long _ID ,String userId, String location, String temperature, String aroundInjection, Timestamp time){
        this._ID=_ID;
        this.userId=userId;
        this.location=location;
        this.temperature=temperature;
        this.aroundInjection=aroundInjection;
        this.time=time;
    }

    public long get_ID() {
        return _ID;
    }

    public void set_ID(long _ID) {
        this._ID = _ID;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getAroundInjection() {
        return aroundInjection;
    }

    public void setAroundInjection(String aroundInjection) {
        this.aroundInjection = aroundInjection;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}

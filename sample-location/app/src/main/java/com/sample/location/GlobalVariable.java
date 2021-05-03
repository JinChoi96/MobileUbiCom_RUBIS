package com.sample.location;

import android.app.Application;

public class GlobalVariable extends Application {

    private String gpsState;
    private String beaconState;

    @Override
    public void onCreate() {
        //전역 변수 초기화
        gpsState = "";
        beaconState = "";
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void setGpsState(String gpsState){
        this.gpsState = gpsState;
    }

    public String getGpsState(){
        return gpsState;
    }

    public void setBeaconState(String beaconState){
        this.beaconState = beaconState;
    }

    public String getBeaconState(){
        return beaconState;
    }
}
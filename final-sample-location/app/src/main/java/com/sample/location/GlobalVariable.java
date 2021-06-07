package com.sample.location;

import android.app.Application;

public class GlobalVariable extends Application {

    private String gpsState;
    private String beaconState;
    private double gpsPose_X;
    private double gpsPose_Y;
    private int beaconVal;
    private double distanceVal;

    @Override
    public void onCreate() {
        //전역 변수 초기화
        gpsState = "";
        beaconState = "";
        gpsPose_X = 0.0;
        gpsPose_Y = 0.0;
        beaconVal = 0;
        distanceVal = 0.0;
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

    public double getGpsPose_X() { return gpsPose_X; }

    public double getGpsPose_Y() { return gpsPose_Y; }

    public void setGpsPose_X(double gps_x) { this.gpsPose_X = gps_x;}

    public void setGpsPose_Y(double gps_y) { this.gpsPose_Y = gps_y;}

    public int getBeaconVal(){return beaconVal;}

    public void setBeaconVal(int beaconVal){ this.beaconVal = beaconVal;}

    public double getDistanceVal(){return distanceVal;}

    public void setDistanceVal(double distanceVal){ this.distanceVal = distanceVal;}

}
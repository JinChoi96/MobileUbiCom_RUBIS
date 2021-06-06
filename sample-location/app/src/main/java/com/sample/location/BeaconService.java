package com.sample.location;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.minew.beacon.BeaconValueIndex;
import com.minew.beacon.BluetoothState;
import com.minew.beacon.MinewBeacon;
import com.minew.beacon.MinewBeaconManager;
import com.minew.beacon.MinewBeaconManagerListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BeaconService extends Service {

    private List<MinewBeacon> mMinewBeacons = new ArrayList<>();
    private MinewBeacon lastBeacon;
    private MinewBeacon currentBeacon;
    String beaconStatus = "ERR";

    private static final String TAG = "BeaconService";
    private static final long SLEEP_TIME = 500L;
    private MinewBeaconManager mMinewBeaconManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMinewBeaconManager = MinewBeaconManager.getInstance(this);
        mMinewBeaconManager.startScan();
        Log.d(TAG, "onCreate BeaconService");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "start BeaconService");
        mMinewBeaconManager.setDeviceManagerDelegateListener(new MinewBeaconManagerListener() {
            /**
             *   if the manager find some new beacon, it will call back this method.
             *
             *  @param minewBeacons  new beacons the manager scanned
             */
            @Override
            public void onAppearBeacons(List<MinewBeacon> minewBeacons) {

            }

            /**
             *  if a beacon didn't update data in 10 seconds, we think this beacon is out of rang, the manager will call back this method.
             *
             *  @param minewBeacons beacons out of range
             */
            @Override
            public void onDisappearBeacons(List<MinewBeacon> minewBeacons) {
                /*for (MinewBeacon minewBeacon : minewBeacons) {
                    String deviceName = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                    Toast.makeText(getApplicationContext(), deviceName + "  out range", Toast.LENGTH_SHORT).show();
                }*/
            }

            /**
             *  the manager calls back this method every 1 seconds, you can get all scanned beacons.
             *
             *  @param minewBeacons all scanned beacons
             */
            @Override
            public void onRangeBeacons(final List<MinewBeacon> minewBeacons) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        lastBeacon = currentBeacon;
                        Log.d("BeaconSize", String.valueOf(minewBeacons.size()));
                        mMinewBeacons.clear();
                        mMinewBeacons.addAll(minewBeacons);
                        if(mMinewBeacons.size() != 0)
                            for(int i = 0; i < mMinewBeacons.size(); i++) {
                                if(mMinewBeacons.get(i).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue().equals("MBeacon")){
                                    currentBeacon = mMinewBeacons.get(i);
                                }
                            }
                    }
                }).start();
            }

            /**
             *  the manager calls back this method when BluetoothStateChanged.
             *
             *  @param state BluetoothState
             */
            @Override
            public void onUpdateState(BluetoothState state) {
                switch (state) {
                    case BluetoothStatePowerOn:
                        Toast.makeText(getApplicationContext(), "BluetoothStatePowerOn", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothStatePowerOff:
                        Toast.makeText(getApplicationContext(), "BluetoothStatePowerOff", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                    if(currentBeacon != null){
                        Log.d("Beacon Value", currentBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_RSSI).getStringValue());
                        checkBeaconState();
                    }

                    SystemClock.sleep(SLEEP_TIME);
                }
            }
        });
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public void checkBeaconState() {

        // compare last beacon and current beacon's rssi value

        // warn user if beacon is too close

        Log.d("Beacon Value", currentBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_RSSI).getStringValue());
        if(currentBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_RSSI).getIntValue() > -65){
            beaconStatus = "Close";
        }
        else{
            beaconStatus = "Far";
        }
        GlobalVariable globalVariable = (GlobalVariable)getApplicationContext();
        globalVariable.setBeaconState(beaconStatus);

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "call on destroy");
        super.onDestroy();
    }
    private final IBinder mBinder = new BeaconServiceBinder();
    private ICallback mCallback;

    public class BeaconServiceBinder extends Binder {
        BeaconService getService() {
            return BeaconService.this;
        }
    }

    public interface ICallback { public void remoteCall(); }

    public void registerCallback(BeaconService.ICallback cb) {
        mCallback = cb;
    }

    public String getBeaconDistance() {
        return beaconStatus;
    }
}

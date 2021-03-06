
package com.sample.location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.minew.beacon.BluetoothState;
import com.minew.beacon.MinewBeacon;
import com.minew.beacon.MinewBeaconManager;
import com.minew.beacon.MinewBeaconManagerListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "54.180.135.134";
    private static String TAG_PHP = "phptest";
    private List<MinewBeacon> mMinewBeacons = new ArrayList<>();
    private MinewBeaconManager mMinewBeaconManager;
    private static final int REQUEST_ENABLE_BT = 2;
    private boolean isScanning;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int GPS_UTIL_LOCATION_RESOLUTION_REQUEST_CODE = 101;

    public static final int DEFAULT_LOCATION_REQUEST_PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    public static final long DEFAULT_LOCATION_REQUEST_INTERVAL = 5000L; // 5s, max interval
    public static final long DEFAULT_LOCATION_REQUEST_FAST_INTERVAL = 3000L; // 3s, min interval

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private double longitude, latitude;

    SwitchCompat switchCompat_gps, switchCompat_beacon;
    Spinner spinner;
    Button btn_activate, btn_terminate;

    int gps_flag = 0;
    int beacon_flag = 0;
    static int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMinewBeaconManager = MinewBeaconManager.getInstance(this);

        switchCompat_gps = findViewById(R.id.switchButton_gps);
        switchCompat_beacon = findViewById(R.id.switchButton_beacon);

        //spinner = findViewById(R.id.spinner);

        btn_activate = (Button)findViewById(R.id.btn_activate);
        btn_terminate = (Button)findViewById(R.id.btn_terminate);

        /*
        String[] frequency = new String [] {"0", "1", "10", "20"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, frequency);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        */

        //GPS ?????????
        switchCompat_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchCompat_gps.isChecked()){

                    gps_flag = 1;
                    Log.d(TAG, "gps_flag to 1");

                }else {

                    gps_flag = 0;
                    Log.d(TAG, "gps_flag to 0");


                }
            }
        });

        //Beacon ?????????
        switchCompat_beacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchCompat_beacon.isChecked()){

                    beacon_flag = 1;


                }else {

                    beacon_flag = 0;

                }
            }

        });
        /*
        //Frequency ?????????
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                }
                if (position == 1){
                }
                if (position == 2){
                }
                if (position == 3){
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/

        //????????? activate
        btn_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //startService(new Intent(getApplicationContext(), GPS_Service.class));
                //startService(new Intent(getApplicationContext(), Beacon_Service.class));


                if (gps_flag == 1 && beacon_flag == 1) {
                    Log.d(TAG, "activate gps 1, beacon 1");
//                    startService(new Intent(getApplicationContext(), AccidentOccurService.class));
                    checkLocationPermission();
                    checkBluetooth();
                    senseBeacon();
                    startService(new Intent(getApplicationContext(), BeaconService.class));
                    startService(new Intent(getApplicationContext(), AccidentOccurService_fore.class));
                    startService(new Intent(getApplicationContext(), AlarmService.class));
                }
                if (gps_flag == 1 && beacon_flag == 0) {
                    Log.d(TAG, "activate gps 1, beacon 0");

                    //startService(new Intent(getApplicationContext(), AccidentOccurService.class));
                    checkLocationPermission();
                    startService(new Intent(getApplicationContext(), AccidentOccurService_fore.class));
                    startService(new Intent(getApplicationContext(), AlarmService.class));
                }
                if (gps_flag == 0 && beacon_flag == 1) {
                    Log.d(TAG, "activate gps 0, beacon 0");
                    checkLocationPermission();
                    checkBluetooth();
                    senseBeacon();
                    startService(new Intent(getApplicationContext(), BeaconService.class));
                    startService(new Intent(getApplicationContext(), AlarmService.class));
                }
            }
        });

        //????????? terminate
        btn_terminate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "terminate service");
                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//                stopService(new Intent(MainActivity.this, AccidentOccurService.class));
                stopService(new Intent(MainActivity.this, AccidentOccurService_fore.class));
                stopService(new Intent(MainActivity.this, BeaconService.class));
                stopService(new Intent(MainActivity.this, AlarmService.class));

                gps_flag = 0;
                beacon_flag = 0;
                Log.d(TAG, "gps flag: " + gps_flag);
                GlobalVariable globalVariable = (GlobalVariable)getApplicationContext();
                globalVariable.setBeaconVal(0);
                globalVariable.setGpsPose_X(0);
                globalVariable.setGpsPose_Y(0);
            }
        });

        startTimerTask();
    }

    private void checkLocationPermission() { // ???????????? ???????????? ??????
            int accessLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION); // ?????? ?????? API
            if (accessLocation == PackageManager.PERMISSION_GRANTED) {
                checkLocationSetting();
            } else { // ?????? ?????? API
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE);
            }
        }


    // ActivityCompat.requestPermissions??? ??????
    // ???????????? -> GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) { // ???????????? ????????? ????????? ????????? ?????? ??????
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        checkLocationSetting();
                    } else { // ??????
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                        builder.setTitle("?????? ????????? ??????????????????.");
                        builder.setMessage("[??????] ???????????? ?????? ????????? ???????????? ?????????.");
                        builder.setPositiveButton("???????????? ??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(); // ?????????????????? ???????????? intent
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent); // ?????????????????? ??????
                            }
                        }).setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        androidx.appcompat.app.AlertDialog alert = builder.create();
                        alert.show();
                    }
                    break;
                }
            }
        }
    }

    private void checkBluetooth() {
        BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
        switch (bluetoothState) {
            case BluetoothStateNotSupported:
                Toast.makeText(this, "Not Support BLE", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case BluetoothStatePowerOff:
                showBLEDialog();
                break;
            case BluetoothStatePowerOn:
                break;
        }
    }

    private void showBLEDialog() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }

    private void senseBeacon() {
        if (mMinewBeaconManager != null) {
            BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
            switch (bluetoothState) {
                case BluetoothStateNotSupported:
                    Toast.makeText(MainActivity.this, "Not Support BLE", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case BluetoothStatePowerOff:
                    showBLEDialog();
                    return;
                case BluetoothStatePowerOn:
                    break;
            }
        }
        if (isScanning) {
            isScanning = false;
            if (mMinewBeaconManager != null) {
                // mMinewBeaconManager.stopScan();
            }
        } else {
            isScanning = true;
            try {
                // mMinewBeaconManager.startScan();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void checkLocationSetting() {
        Log.d(TAG, "check location setting");
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(DEFAULT_LOCATION_REQUEST_PRIORITY);
            locationRequest.setInterval(DEFAULT_LOCATION_REQUEST_INTERVAL);
            locationRequest.setFastestInterval(DEFAULT_LOCATION_REQUEST_FAST_INTERVAL);

            SettingsClient settingsClient = LocationServices.getSettingsClient(this);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).setAlwaysShow(true);
            settingsClient.checkLocationSettings(builder.build())
                    .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            // ?????? ???????????? ????????????, ????????? ?????? ????????? ????????? ?????? ??? ???????????? ???????????? ???
                            // build.gradle?????? google service, location ??????????????? ???.
                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                            // locationCallback
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                        }
                    })
                    .addOnFailureListener(MainActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) { // ????????? ???????????? ?????? ??????
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        // activity ????????? onactivityresult??? ?????? ?????? ?????? code ??????
                                        rae.startResolutionForResult(MainActivity.this, GPS_UTIL_LOCATION_RESOLUTION_REQUEST_CODE);
                                    } catch (IntentSender.SendIntentException sie) {
                                        Log.w(TAG, "unable to start resolution for result due to " + sie.getLocalizedMessage());
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE: // ????????? ???????????? ?????? ?????????, gps sensor ????????????
                                    String errorMessage = "location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                                    Log.e(TAG, errorMessage);
                            }
                        }
                    });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_UTIL_LOCATION_RESOLUTION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                checkLocationSetting();
            } else {
                finish();
            }
        }
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            longitude = locationResult.getLastLocation().getLongitude();
            latitude = locationResult.getLastLocation().getLatitude();
//            fusedLocationProviderClient.removeLocationUpdates(locationCallback);

//            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//            Intent intent = new Intent(SplashActivity.this, AccidentOccurActivity.class);
//            Intent intent = new Intent(MainActivity.this, AccidentOccurService.class);
            Intent intent = new Intent(MainActivity.this, AccidentOccurService_fore.class);

            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            Log.d(TAG, "start service");
            startService(intent);
//            finish();
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            Log.i(TAG, "onLocationAvailability - " + locationAvailability);
        }
    };

    private void startTimerTask()
    {
        SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");

        TimerTask timerTask = new TimerTask(){
            @Override
            public void run() {
                GlobalVariable globalVariable = (GlobalVariable)getApplicationContext();

                String name = String.valueOf(globalVariable.getGpsPose_X());
                String country = String.valueOf(globalVariable.getGpsPose_Y());
                String beacon = String.valueOf(globalVariable.getBeaconVal());
                String format_time1 = format1.format (System.currentTimeMillis());
                System.out.println(format_time1);

                String serverURL = "http://54.180.135.134/insert2.php";
                String postParameters = "name=" + name + "&country=" + country + "&beacon=" + beacon;


                try {

                    URL url = new URL(serverURL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setReadTimeout(5000);
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.connect();

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(postParameters.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    int responseStatusCode = httpURLConnection.getResponseCode();
                    Log.d(TAG_PHP, "POST response code - " + responseStatusCode);

                    InputStream inputStream;
                    if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = httpURLConnection.getInputStream();
                    }
                    else{
                        inputStream = httpURLConnection.getErrorStream();
                    }


                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while((line = bufferedReader.readLine()) != null){
                        sb.append(line);
                    }


                    bufferedReader.close();


                } catch (Exception e) {

                    Log.d(TAG_PHP, "InsertData: Error ", e);

                }
                Log.d("TimerTask", ", " + String.valueOf(counter++));
                queryServer("??????", "??????");
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, 0, 3000);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stop scan
        if (isScanning) {
            mMinewBeaconManager.stopScan();
        }
    }

    public void queryServer(String param1, String param2)
    {

        String searchKeyword1 = param1;
        String searchKeyword2 = param2;

        String serverURL = "http://54.180.135.134/query.php";
        String postParameters = "country=" + searchKeyword1 + "&name=" + searchKeyword2;


        try {

            URL url = new URL(serverURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();


            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();


            int responseStatusCode = httpURLConnection.getResponseCode();
            Log.d(TAG, "response code - " + responseStatusCode);

            InputStream inputStream;
            if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            }
            else{
                inputStream = httpURLConnection.getErrorStream();
            }


            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line;

            double x1, y1;
            double x2 = 0, y2 = 0;

            GlobalVariable globalVariable = (GlobalVariable)getApplicationContext();
            x1 = globalVariable.getGpsPose_X();
            y1 = globalVariable.getGpsPose_Y();


            while((line = bufferedReader.readLine()) != null){
                sb.append(line);
                if(line.contains("gpsx")) {
                    Log.d(TAG, "line: "+line.split("\"")[3]);
                    x2 = Double.parseDouble(line.split("\"")[3]);
                }
                else if(line.contains("gpsy")) {
                    Log.d(TAG, "line: "+line.split("\"")[3]);
                    y2 = Double.parseDouble(line.split("\"")[3]);
                }
            }

            double distance = 0;
            distance = Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2);
            distance = Math.sqrt(distance);
            Log.d("distance :", String.valueOf(distance));
            globalVariable.setDistanceVal(distance);
            Log.d(TAG, "distance : " + String.valueOf(distance));


            bufferedReader.close();
            String result = sb.toString().trim();
            Log.d("Query Result", result);

        } catch (Exception e) {

            Log.d(TAG, "InsertData: Error ", e);
        }
    }
}


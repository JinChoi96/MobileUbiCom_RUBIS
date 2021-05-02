package com.example.safe_guide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

public class  MainActivity extends AppCompatActivity {

    SwitchCompat switchCompat_gps, switchCompat_beacon;
    Spinner spinner;
    Button btn_activate, btn_terminate;

    int gps_flag = 0;
    int beacon_flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //GPS 스위치
        switchCompat_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchCompat_gps.isChecked()){

                    gps_flag = 1;

                }else {

                    gps_flag = 0;

                }
            }
        });

        //Beacon 스위치
        switchCompat_beacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchCompat_gps.isChecked()){

                    beacon_flag = 1;


                }else {

                    beacon_flag = 0;

                 }
            }


        });
        /*
        //Frequency 스피너
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

        //서비스 activate
        btn_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //startService(new Intent(getApplicationContext(), GPS_Service.class));
                //startService(new Intent(getApplicationContext(), Beacon_Service.class));


                if (gps_flag == 1 && beacon_flag == 1) {
                    startService(new Intent(getApplicationContext(), GPS_Service.class));
                    startService(new Intent(getApplicationContext(), Beacon_Service.class));
                }

                if (gps_flag == 1 && beacon_flag == 0) {
                    startService(new Intent(getApplicationContext(), GPS_Service.class));
                }

                if (gps_flag == 0 && beacon_flag == 1) {
                    startService(new Intent(getApplicationContext(), Beacon_Service.class));
                }
            }
        });

        //서비스 terminate
        btn_terminate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), GPS_Service.class));
                stopService(new Intent(getApplicationContext(), Beacon_Service.class));

                gps_flag = 0;
                beacon_flag = 0;

            }
        });

    }
}
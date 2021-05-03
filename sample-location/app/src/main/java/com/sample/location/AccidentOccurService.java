package com.sample.location;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AccidentOccurService extends Service {
    // Flag that indicates it's test
    private static final boolean TEST_ALERT = true;
    private static final boolean TEST_API = false;
    // Variables used when testing
    private double initial_latitude;
    private double initial_longitude;

    private static final String TAG = "AccidentOccurService";

    private String APIKeys = "LydaYiDETwYmI2UMH5Ncugmv9Hd4LEjUx39foqGvQ%2F3wiW4b%2FjlCnJW%2B43o%2BpZ8aYciwE5rDRoOGqRhQd1bJ4g%3D%3D";
    private String requestParameter = "&searchYearCd=2017&siDo=SIDO&guGun=GUGUN&numOfRows=10&pageNo=1";
    private String requests[] = {"http://apis.data.go.kr/B552061/frequentzoneOldman/getRestFrequentzoneOldman?ServiceKey=",
            "http://apis.data.go.kr/B552061/schoolzoneChild/getRestSchoolzoneChild?ServiceKey=",
            "http://apis.data.go.kr/B552061/frequentzoneChild/getRestFrequentzoneChild?ServiceKey="};

    int requestCodes[] = new int[2];
    String responses[] = new String[3];
    String jsonResponse[] = new String[3];

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate AccidentOccurService");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d(TAG, "start AccidentOccurService");
            double latitude = intent.getDoubleExtra("latitude", 0);
            double longitude = intent.getDoubleExtra("longitude", 0);

            if (TEST_ALERT) {
                Log.d(TAG, "initial_latitude : " + latitude);
                Log.d(TAG, "initial_longitude : " + longitude);
                initial_latitude = latitude;
                initial_longitude = longitude;
            }

            checkAccidentOccurrence(latitude, longitude);
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "call on destroy");
        super.onDestroy();
    }

    private void checkAccidentOccurrence(double latitude, double longitude) {
        // TODO: 현재 (위도, 경도)로부터 시도코드, 시군구코드 뽑기
        requestCodes = getRequestParameter(latitude, longitude);
        requestParameter.replace("SIDO", Integer.toString(requestCodes[0])); // 시도코드
        requestParameter.replace("GUGUN", Integer.toString(requestCodes[1])); // 시구군코드

        // 사용할 API 개수만큼 request 보내고 json 결과 받기
        for (int i = 0; i < 3; i++) {
            requests[i] += APIKeys;
            requests[i] += requestParameter;
        }
        for (int i = 0; i < 3 ; i++) {
            jsonResponse[i] = getAPIResponse((requests[i]));
        }

        if(TEST_API) {
            String testURL = "http://apis.data.go.kr/B552061/frequentzoneOldman/getRestFrequentzoneOldman?ServiceKey=LydaYiDETwYmI2UMH5Ncugmv9Hd4LEjUx39foqGvQ%2F3wiW4b%2FjlCnJW%2B43o%2BpZ8aYciwE5rDRoOGqRhQd1bJ4g%3D%3D&searchYearCd=2017&siDo=11&guGun=680&numOfRows=10&pageNo=1";
            String test = getAPIResponse(testURL);
            Log.d(TAG, "API response : "+test);
        }

        // TODO : json에서 폴리곤 결과 파싱
        Map<double[][], String> polygons = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            polygons.putAll(parseJson(jsonResponse[i]));
        }

        // TODO : 폴리곤 안에 현재 (위도, 경도)가 포함되는지 판단
        String alert = null;
        for (Map.Entry<double[][], String> elem : polygons.entrySet()) {
            if(hasAccidentOccured(latitude, longitude, elem.getKey())) {
                alert = elem.getValue();
                break;
            }
        }

        // TEST
        if (TEST_ALERT) {
            Log.d(TAG, "current_latitude : " + latitude);
            Log.d(TAG, "current_longitude : " + longitude);
            if (hasAccidentOccured_stub(latitude, longitude)) {
                alert = "보행자 사고다발 지역입니다. 주변 움직이는 차량을 주의하세요.";
            }
        }

        // 사고다발지역인 경우 해당 alert string 을 결과값으로 반환
        String alert_message = null;
        if (alert != null) {
            alert_message = "보행자 사고다발 지역입니다. 주변 움직이는 차량을 주의하세요.";
            Log.d(TAG, "alert_message set");

            // make and start new intent for passing alert_message
//            Intent intent = new Intent(AccidentOccurService.this, /*modify*/AlertService.class);
//            intent.putExtra("alert_msg", alert_message);
//            startService(intent);
        }
    }

    // TODO
    private int[] getRequestParameter(double latitude, double longitude) {
        // 현재 (위도, 경도)로부터 시도코드, 시군구코드 뽑기

        int param[] = new int[2];

        // fill param[] using Geolocation API

        return param;
    }

    private String getAPIResponse(String request) {
        String jsonString = new ThreadTask<String, String>() {
            @Override
            protected String doInBackground(String arg) {
//                Log.d("doInBackground", "arg: " + arg);
                String result = null;
                try{
                    URL url = new URL(arg);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("GET");
                    InputStream is = conn.getInputStream();

                    StringBuilder builder = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
//                        Log.d("doInBackground", "readline(): " + line);
                        builder.append(line);
                    }
                    result = builder.toString();
//                    Log.d("doInBackground", "result: " + result);
                } catch (Exception e) {
                    Log.e("REST_API", "GET method failed: " + e.getMessage());
                    e.printStackTrace();
                }
                return result;
            }
        }.execute(request);

        return jsonString;
    }

    // TODO
    private HashMap<double[][], String> parseJson(String jsonResponse) {
        HashMap<double[][], String> polygons = new HashMap<>();
        // parse json and return a list of (polygon coordinates, kind of accident)
//        JSONObject jObject = new JSONObject(jsonResponse);

        return polygons;
    }

    // TODO
    private boolean hasAccidentOccured(double latitude, double longitude, double[][] coords) {
        boolean ret = false;
        return ret;
    }

    // stub
    // returns true if the distance between initial position and current position is [min_d, max_d]
    private boolean hasAccidentOccured_stub(double latitude, double longitude) {
        boolean ret = false;
        int min_d = 10; // minimal distance that causes an alert (m)
        int max_d = 20; // maximum distance that causes an alert (m)
        double theta = initial_longitude - longitude;
        double dist = Math.sin(deg2rad(initial_latitude)) * Math.sin(deg2rad(latitude)) +
                Math.cos(deg2rad(initial_latitude)) * Math.cos(deg2rad(latitude)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1609.344; // to meter

        Log.d(TAG, "hasAccidentOccured_stub: dist(m) = " + dist);

        if (dist >= min_d && dist <= max_d) {
            ret = true;
        }
        return ret;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

package com.sample.location;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Locale;

public class AlarmService extends Service {
    SoundPool mPool;
    SoundPool mPool2;
    int mBeepSound;
    int mWarningSound;

    private static final String TAG = "AlarmService";
    private TextToSpeech tts;
    private boolean DESTROY_SIGNAL = false;
    private static final long SLEEP_TIME_SPEECH = 5000L;
    private static final long SLEEP_TIME_BEEP_FAST = 500L;
    private static final long SLEEP_TIME_ALARM = 2000L;
    private static final long SLEEP_TIME_BEEP_SLOW = 1000L;
    Thread mThreadBeep;
    Thread mThreadAlarm;
//    Thread mThreadSpeech;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.KOREAN);
                } else {
                    showState("TTS 객체 초기화 중 에러가 발생했습니다.");
                }
            }
        });

        mPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mBeepSound = mPool.load(this, R.raw.beep, 1);
        mPool2 = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mWarningSound = mPool2.load(this, R.raw.alarm, 1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mThreadBeep = new Thread(new Runnable() {

            @Override
            public void run() {
                while (DESTROY_SIGNAL == false){
//                    Intent service = new Intent(getApplicationContext(), BeaconService.class);
//                    bindService(service, mConnection, getApplicationContext().BIND_AUTO_CREATE);
                    String mBeaconDistance = "";
                    GlobalVariable globalVariable = (GlobalVariable)getApplicationContext();
                    mBeaconDistance = globalVariable.getBeaconState();
                    Log.d("Alarm Check",globalVariable.getBeaconState() + globalVariable.getBeaconVal());
                    if(mBeaconDistance.equals("Close")) {
                        SystemClock.sleep(SLEEP_TIME_BEEP_FAST);
                        beepSoundAlarm();
                    }
                    else if(mBeaconDistance.equals("Far"))
                    {
                        SystemClock.sleep(SLEEP_TIME_BEEP_SLOW);
                        beepSoundAlarm();
                    }
                    else if(mBeaconDistance.equals("None")){

                    }
                }
            }
        });
        mThreadBeep.start();

        mThreadAlarm = new Thread(new Runnable() {
            @Override
            public void run() {
                while (DESTROY_SIGNAL == false){
//                    alarmSoundAlarm();
                    GlobalVariable globalVariable = (GlobalVariable)getApplicationContext();
                    double distance;
                    distance = globalVariable.getDistanceVal();
                    Log.d("Alarm Check", String.valueOf(globalVariable.getDistanceVal()));
                    if(distance > 0.001 && distance < 0.0021 ) {
                        Log.d(TAG, "alarming?");
                        SystemClock.sleep(SLEEP_TIME_ALARM);
                        alarmSoundAlarm();
                    }

                }
            }
        });
        mThreadAlarm.start();

//        mThreadSpeech = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                SystemClock.sleep(SLEEP_TIME_SPEECH);
//                while (DESTROY_SIGNAL == false){
//                    String mGpsStatus = "";
//                    GlobalVariable globalVariable = (GlobalVariable)getApplicationContext();
//                    mGpsStatus = globalVariable.getGpsState();
//                    if(mGpsStatus ==null || mGpsStatus.equals("")){
//
//                    }
//                    else {
//                        speechAlarm(mGpsStatus);
//                    }
//                    SystemClock.sleep(SLEEP_TIME_SPEECH);
//                }
//            }
//        });
//        mThreadSpeech.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "call on destroy");

        DESTROY_SIGNAL = true;

        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void showState(final String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void speechAlarm(String text){
        whichSpeech(text);
    }

    public void beepSoundAlarm(){
        mPool.play(mBeepSound, 1, 0, -1, 0, 1);
    }
    public void alarmSoundAlarm(){
        mPool2.play(mWarningSound, 1, 0, -1, 0, 1);
    }


    public void whichSpeech(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String utteranceId=this.hashCode() + "";
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);

        } else {
            HashMap<String, String> map = new HashMap<>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
        }
    }
    private BeaconService mBeaconService;
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BeaconService.BeaconServiceBinder binder = (BeaconService.BeaconServiceBinder) service;
            mBeaconService = binder.getService(); // get service.
            mBeaconService.registerCallback(mCallback); // callback registration
        }
        // Called when the connection with the service disconnects unexpectedly
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBeaconService = null;
        }
    };
    // call below callback in service. it is running in Activity.
    private BeaconService.ICallback mCallback = new BeaconService.ICallback() {
        @Override
        public void remoteCall() {
            Log.d("MainActivity","called by service");
        }
    };

}

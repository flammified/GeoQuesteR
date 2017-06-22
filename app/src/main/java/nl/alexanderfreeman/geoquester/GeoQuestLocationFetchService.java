package nl.alexanderfreeman.geoquester;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

public class GeoQuestLocationFetchService extends Service
{
    public static final int TWO_MINUTES = 120000; // 120 seconds
    public static Boolean isRunning = false;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    Handler mHandler = new Handler();
    Runnable mHandlerTask = new Runnable(){
        @Override
        public void run() {
            if (!isRunning) {
                startListening();
            }
            mHandler.postDelayed(mHandlerTask, TWO_MINUTES);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandlerTask.run();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopListening();
        mHandler.removeCallbacks(mHandlerTask);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startListening() {
        isRunning = true;
    }

    private void stopListening() {
        isRunning = false;
    }
}
package com.najin.dogdiary.location;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.najin.dogdiary.R;
import com.naver.maps.geometry.LatLng;

import org.greenrobot.eventbus.EventBus;

public class MyBackgroundService extends Service {

    private static final String CHANNEL_ID = "my_channel";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = "com.najin.dogdiary" + ".started_from_notification";
    private final IBinder mBinder = new LocalBinder();
    private static final long UPDATE_INTEERVAL_IN_MUL = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MUL = UPDATE_INTEERVAL_IN_MUL/2;
    private static final int NOTI_ID = 1223;
    private boolean mChangingConfiguration = false;
    private NotificationManager mNotificationManager;

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Handler mServiceHandler;
    private Location mLocation;

    public MyBackgroundService() {
    }

    @Override
    public void onCreate() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        CreateLocationRequest();
        getLastLocation();

        HandlerThread handlerThread = new HandlerThread("EDMTDev");
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);

            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //산책 시작하자마자 알림창 생성
        mNotificationManager.notify(NOTI_ID,getNotification());

        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,false);
        if(startedFromNotification) {
            removeLocationUpdates();
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    public void removeLocationUpdates() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            CommonLocation.setRequestingLocationUpdates(this,false);
            //notification 파괴
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTI_ID);
            stopSelf();
        } catch (SecurityException ex) {
            CommonLocation.setRequestingLocationUpdates(this,true);
            Log.e("EDMT_DEV","Lost location permission. could not remove");
        }
    }

    private void getLastLocation() {
        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if(task.isSuccessful() && task.getResult() != null)
                                mLocation = task.getResult();
                            else
                                Log.e("EDMT_DEV","Failed to get location");
                        }
                    });
        } catch(SecurityException ex) {
            Log.e("EDMT_DEV","Lost location permission.  "+ex);
        }
    }

    private void CreateLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTEERVAL_IN_MUL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MUL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //위치가 변할 때 마다 호출됨
    private void onNewLocation(Location lastLocation) {
        mLocation = lastLocation;
        EventBus.getDefault().postSticky(new SendLocationToActivity(mLocation));

        //현재 위치 받아오기
        double distance = 0;
        LatLng beforeLocation = WalkInformationModel.getInstance().getBeforeLocation();
        LatLng currentLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());

        if (beforeLocation == null) {
            WalkInformationModel.getInstance().setBeforeLocation(currentLocation);
        } else {
            distance = (beforeLocation).distanceTo(currentLocation);
        }

        if (distance > 20) {
            WalkInformationModel.getInstance().setBeforeLocation(currentLocation);
            WalkInformationModel.getInstance().addTotalDistance(distance);
            WalkInformationModel.getInstance().addCoords(currentLocation);
        }
    }

    private Notification getNotification() {
        Intent intent = new Intent(this, MyBackgroundService.class);
        String text = CommonLocation.getLocationText(mLocation);
        
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION,true);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentText(text)
                //.setContentTitle(CommonLocation.getLocationTitle(this))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(text)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setWhen(System.currentTimeMillis());

        //set the channel id for android o
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }
        return builder.build();
    }

    private boolean serviceIsRunningInForeGround(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service:manager.getRunningServices(Integer.MAX_VALUE))
            if(getClass().getName().equals(service.service.getClassName()))
                if(service.foreground)
                    return true;
        return false;
    }

    public void requestLocationupdates() {
        CommonLocation.setRequestingLocationUpdates(this,true);
        startService(new Intent(getApplicationContext(), com.najin.dogdiary.location.MyBackgroundService.class));
        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
        } catch (SecurityException ex) {
            Log.e("EDMT_DEV","lost locaion permission.could not request it "+ex);
        }
    }

    public class LocalBinder extends Binder {
        public com.najin.dogdiary.location.MyBackgroundService getService() {return MyBackgroundService.this;}
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(!mChangingConfiguration && CommonLocation.requestingLocationUpdates(this))
            startForeground(NOTI_ID,getNotification());
        return true;
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacks(null);
        super.onDestroy();
    }
}

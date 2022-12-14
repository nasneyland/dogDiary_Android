package com.najin.dogdiary.information;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.najin.dogdiary.R;
import com.najin.dogdiary.calendar.CalendarSet;
import com.najin.dogdiary.location.GpsTracker;
import com.najin.dogdiary.location.MyBackgroundService;
import com.najin.dogdiary.location.SendLocationToActivity;
import com.najin.dogdiary.location.WalkInformationModel;
import com.najin.dogdiary.model.HomeVO;
import com.najin.dogdiary.model.WalkVO;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.LocationButtonView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WalkActivity extends AppCompatActivity implements OnMapReadyCallback,
        SharedPreferences.OnSharedPreferenceChangeListener{

    MyBackgroundService mService = null;
    boolean mBound = false;

    //?????? ?????? ?????? ??????
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final String[] PERMISSIONS = {
        Manifest.permission.ACCESS_FINE_LOCATION
    };

    //?????? ?????? ??????
    GpsTracker gpsTracker;
    double latitude;
    double longitude;
    LatLng location;
    LatLng currentLocation;

    //????????? ??????
    private FusedLocationSource locationSource; //navermap ?????? ??????
    private NaverMap walkMap;

    //????????????
    private Thread timeThread = null;
    Boolean walkType = false;
    Date startDate;
    String startTime;
    Integer walkSecond;
    Button walkStartButton;
    String walkDate;

    //????????????
    PathOverlay pathOverlay;

    ConstraintLayout walkLoadingView;
    ConstraintLayout walkPopUpView;
    SpinKitView walkingView;
    TextView walkTime;
    TextView walkDistance;
    TextView insertWalkStartTime;
    TextView insertWalkTime;
    TextView insertWalkDistance;
    Button goMapButton;
    Button goHomeButton;

    //background service ??????
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            MyBackgroundService.LocalBinder binder = (MyBackgroundService.LocalBinder)iBinder;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_walk);

        walkPopUpView = (ConstraintLayout) findViewById(R.id.walkPopUpView);
        walkLoadingView = (ConstraintLayout) findViewById(R.id.walkLoadingView);
        walkPopUpView.setVisibility(View.GONE);
        walkLoadingView.setVisibility(View.GONE);

        walkTime = (TextView) findViewById(R.id.walkTime);
        walkDistance = (TextView) findViewById(R.id.walkDistance);
        walkingView = (SpinKitView) findViewById(R.id.walkingView);
        walkingView.setVisibility(View.GONE);
        insertWalkStartTime = (TextView) findViewById(R.id.insertWalkStartTime);
        insertWalkTime = (TextView) findViewById(R.id.insertWalkTime);
        insertWalkDistance = (TextView) findViewById(R.id.insertWalkDistance);
        goMapButton = (Button) findViewById(R.id.goMapButton);
        goHomeButton = (Button) findViewById(R.id.goHomeButton);

        //status bar ????????????
        getWindow().setStatusBarColor(Color.parseColor("#dd695d"));

        //????????????
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(new Date());
        walkDate = today;

        //?????? ?????? ?????? ????????????
        gpsTracker = new GpsTracker(WalkActivity.this);
        latitude = gpsTracker.getLatitude(); // ??????
        longitude = gpsTracker.getLongitude(); //??????

        if (latitude == 0.0 && longitude == 0.0) {
            location = new LatLng(37.542578658412445,127.03804799945983);
        } else {
            location = new LatLng(latitude,longitude);
        }

        //naver map ??????
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.naverMap);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.naverMap, mapFragment).commit();
        }
        mapFragment.getMapAsync(naverMap -> {
            LocationButtonView locationButtonView = findViewById(R.id.locationButtonView);
            locationButtonView.setMap(naverMap);
        });
        mapFragment.getMapAsync(this);
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        //????????????
        Dexter.withActivity(this)
                .withPermissions(PERMISSIONS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        walkStartButton = (Button) findViewById(R.id.walkStartButton);
                        walkStartButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(WalkActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                                        // ???????????? ?????? ????????? ????????????
                                        //?????? ?????? ?????? ????????????
                                        requestPermissions(PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE);
                                    } else {
                                        //???????????? ?????? ????????? ????????? ?????? ???????????? ?????? ????????? ????????? ?????? ?????? ?????? ??????????????? ????????? ?????? ??????
                                        //?????? ???????????? ??????
                                        AlertDialog.Builder builder = new AlertDialog.Builder(WalkActivity.this);
                                        builder.setTitle("???????????? ????????? ??????");
                                        builder.setMessage("?????? ?????? ????????? ???????????????.\n?????????????????? ?????????????????????????");
                                        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                                startActivity(intent);
                                            }
                                        });
                                        builder.setNeutralButton("??????", null);
                                        builder.create().show();
                                    }
                                } else {
                                    if (!walkType) {
                                        //????????????
                                        walkingView.setVisibility(View.VISIBLE);
                                        walkStartButton.setText("????????????");
                                        walkType = true;
                                        //backgroundService ???????????? ??????
                                        mService.requestLocationupdates();
                                        //???????????? ??????
                                        timeThread = new Thread(new timeThread());
                                        timeThread.start();
                                        //?????????????????? ??????
                                        String ampm;
                                        startDate = new Date();
                                        int hour;
                                        int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                                        int minute = Calendar.getInstance().get(Calendar.MINUTE);
                                        if (hourOfDay == 12) {
                                            ampm = "??????";
                                            hour = 12;
                                        } else if (hourOfDay > 12) {
                                            ampm = "??????";
                                            hour = hourOfDay - 12;
                                        } else {
                                            ampm = "??????";
                                            hour = hourOfDay;
                                        }
                                        if (minute == 0) {
                                            startTime = ampm + " " + hour + "???";
                                        } else {
                                            startTime = ampm + " " + hour + "??? " + minute + "???";
                                        }
                                    } else {
                                        //????????????
                                        if (walkSecond < 300 || WalkInformationModel.getInstance().getTotalDistance() < 100) {
                                            Snackbar snack = Snackbar.make(v, "?????? ????????? ???????????????.\n?????? 5??? ??????, 0.1km ????????? ????????? ???????????????.", Snackbar.LENGTH_LONG);
                                            View view = snack.getView();
                                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                                            params.gravity = Gravity.BOTTOM;
                                            view.setLayoutParams(params);
                                            snack.show();
                                        } else {
                                            walkStartButton.setEnabled(false);
                                            walkLoadingView.setVisibility(View.VISIBLE);
                                            sendWalk();
                                        }
                                    }
                                }
                            }
                        });
                        bindService(new Intent(WalkActivity.this, MyBackgroundService.class),
                                mServiceConnection, Context.BIND_AUTO_CREATE);
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    }
                }).check();

        //???????????? ?????? ??????
        goMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walkPopUpView.setVisibility(View.GONE);
            }
        });

        //????????? ?????? ??????
        goHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //??????????????? ?????? ?????? ?????? ?????? ??????
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,  @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) {
                walkMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
    }

    //??????????????? ?????????
    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        //?????? ??????
        walkMap = naverMap;
        walkMap.setLocationSource(locationSource);
        ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE);
        naverMap.setLocationSource(locationSource);

        //?????? ????????? ??????
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setScaleBarEnabled(false);
        uiSettings.setZoomControlEnabled(false);
        uiSettings.setLocationButtonEnabled(false);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, getResources().getDisplayMetrics());
        naverMap.setContentPadding(0, 0, 0, height);

        //????????? ?????? ?????? ??????
        CameraPosition cameraPosition = new CameraPosition(location, 16);
        naverMap.setCameraPosition(cameraPosition);
    }

    //???????????? ?????? ??? ?????????
    @Override
    public void onBackPressed() {
        if (walkType) {
            AlertDialog.Builder builder = new AlertDialog.Builder(WalkActivity.this);
            builder.setTitle("????????????");
            builder.setMessage("????????? ?????????????????????????\n????????? ???????????? ??????????????? ???????????? ????????????.");
            builder.setNegativeButton("??????",(dialog, which) ->
                finish()
            );
            builder.setNeutralButton("??????", (dialog, which) -> dialog.cancel());
            builder.create().show();
        } else {
            finish();
        }
    }

    //???????????? ??????
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            long diff = new Date().getTime() - startDate.getTime();
            long secs = diff / 1000;

            walkSecond = (int)secs;

            long sec = secs % 60;
            long min = secs / 60;
            long hour = secs / 3600;

            @SuppressLint("DefaultLocale")
            String result = String.format("%02d:%02d:%02d", hour, min, sec);
            walkTime.setText(result);
        }
    };

    public class timeThread implements Runnable {
        @Override
        public void run() {
            int i = 0;

            while (true) {
                while (walkType) {
                    Message msg = new Message();
                    msg.arg1 = i++;
                    handler.sendMessage(msg);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                //????????? ??????
                            }
                        });
                        return; // ???????????? ?????? ?????? return
                    }
                }
            }
        }
    }

    //?????? ?????? ??????
    private void updateLocation() {
        //???????????? ?????? ??????
        pathOverlay = new PathOverlay();

        //?????? ?????? ??????
        walkDistance.setText((Math.floor((WalkInformationModel.getInstance().getTotalDistance()/1000) * 10) / 10) + "km");

        //????????? ??????
        currentLocation = new LatLng(latitude, longitude);
        pathOverlay.setCoords(WalkInformationModel.getInstance().getCoords());

        //????????? ?????? ??????
        pathOverlay.setWidth(30);
        pathOverlay.setColor(Color.parseColor("#dd695d"));
        pathOverlay.setOutlineColor(Color.parseColor("white"));
        pathOverlay.setOutlineWidth(2);
        pathOverlay.setPatternImage(OverlayImage.fromResource(R.drawable.img_foot));
        pathOverlay.setPatternInterval(50);
        pathOverlay.setMap(walkMap);
    }

    //backgroundService ????????? ??????
    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if(mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) { }

    //foreground?????? ?????? ?????? ????????? ??????
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListenLocation(SendLocationToActivity event) {
        if(event != null) {
            if (WalkInformationModel.getInstance().getCoords().size() > 1) {
                updateLocation();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (walkType) {
            WalkInformationModel.getInstance().destroyWalkInformationModel();
            //backgroundService ??????
            mService.removeLocationUpdates();
            //???????????? ?????????
            timeThread.interrupt();
        }
        super.onDestroy();
    }

    //???????????? ??????
    private void sendWalk() {
        Integer sendWalkMinute = (walkSecond / 60);
        String sendWalkDistance = Math.floor((WalkInformationModel.getInstance().getTotalDistance()/1000) * 10) / 10 + "";

        JSONObject walkObject = new JSONObject();
        try {
            walkObject.put("dog", HomeVO.getHomeVO().getDog().getId());
            walkObject.put("date", walkDate);
            walkObject.put("time", startTime);
            walkObject.put("minutes", sendWalkMinute);
            walkObject.put("distance", sendWalkDistance);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "?????? ?????? ?????? : ????????? ?????? ??????????????????", Toast.LENGTH_SHORT).show();
            walkLoadingView.setVisibility(View.GONE);
            walkStartButton.setEnabled(true);
        }
        JsonObjectRequest walkRequest = new JsonObjectRequest(Request.Method.POST, Common.URL + "/diary/walk", walkObject,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        WalkInformationModel.getInstance().destroyWalkInformationModel();

                        Gson gson = new Gson();
                        WalkVO[] list = gson.fromJson(response.getString("walkList"), WalkVO[].class);
                        List<WalkVO> alist = Arrays.asList(list);
                        ArrayList<WalkVO> arrayList = new ArrayList<WalkVO>();
                        arrayList.addAll(alist);
                        HomeVO.getInstance().setWalkList(arrayList);

                        CalendarSet.setWalk(getApplicationContext());

                        walkType = false;

                        insertWalkStartTime.setText("?????? ?????? : " + startTime);
                        insertWalkTime.setText("?????? ?????? : " + sendWalkMinute + "???");
                        insertWalkDistance.setText("?????? ?????? : " + sendWalkDistance+"km");

                        walkingView.setVisibility(View.GONE);
                        //backgroundService ??????
                        mService.removeLocationUpdates();
                        //???????????? ?????????
                        timeThread.interrupt();

                        walkLoadingView.setVisibility(View.GONE);
                        walkPopUpView.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "?????? ?????? ?????? : ????????? ?????? ??????????????????", Toast.LENGTH_SHORT).show();
                        walkLoadingView.setVisibility(View.GONE);
                        walkStartButton.setEnabled(true);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "?????? ?????? ?????? : ????????? ?????? ??????????????????", Toast.LENGTH_SHORT).show();
                    walkLoadingView.setVisibility(View.GONE);
                    walkStartButton.setEnabled(true);
                }
            });
        walkRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        walkRequest.setShouldCache(false);
        AppHelper.requestQueue.add(walkRequest);
    }
}
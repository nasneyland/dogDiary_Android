package com.najin.dogdiary.information;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.najin.dogdiary.R;
import com.najin.dogdiary.calendar.CalendarActivity;
import com.najin.dogdiary.model.CalendarVO;
import com.najin.dogdiary.model.HomeVO;
import com.najin.dogdiary.model.MemberVO;
import com.najin.dogdiary.model.WalkVO;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;
import com.najin.dogdiary.support.CustomTimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WalkedActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;

    ConstraintLayout walkedLoadingView;

    TextView walkDateText;
    TextView walkStartTimeText;
    TextView walkTimeText;
    EditText walkDistanceText;
    TextView checkWalkStartTime;
    TextView checkWalkTime;
    TextView checkWalkDistance;
    Button insertButton;

    String walkDate;
    Integer walkTime;
    int selectedStartHour = 12;
    int selectedStartMin = 0;
    int selectedHour = 0;
    int selectedMin = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_walked);

        walkedLoadingView = (ConstraintLayout) findViewById(R.id.walkedLoadingView);
        walkedLoadingView.setVisibility(View.GONE);

        //admob 셋팅
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //test
//        mInterstitialAd.setAdUnitId("ca-app-pub-1025720268361094/3562644757");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        //뷰 기본 셋팅
        walkDateText = (TextView) findViewById(R.id.walkDate);
        walkStartTimeText = (TextView) findViewById(R.id.walkStartTimeText);
        walkTimeText = (TextView) findViewById(R.id.walkTimeText);
        walkDistanceText = (EditText) findViewById(R.id.walkDistanceText);
        checkWalkStartTime = (TextView) findViewById(R.id.checkWalkStartTime);
        checkWalkTime = (TextView) findViewById(R.id.checkWalkTime);
        checkWalkDistance = (TextView) findViewById(R.id.checkWalkDistance);
        checkWalkStartTime.setVisibility(View.GONE);
        checkWalkTime.setVisibility(View.GONE);
        checkWalkDistance.setVisibility(View.GONE);
        insertButton = (Button) findViewById(R.id.walkInsertButton);

        //status bar 색상지정
        getWindow().setStatusBarColor(Color.parseColor("#dd695d"));

        //오늘인지 다른날짜인지 확인
        Intent intent = getIntent();
        walkDate = intent.getExtras().getString("informationDate");
        String walkDateString = intent.getExtras().getString("informationDateString");
        walkDateText.setText(walkDateString);

        //산책 시작시간 입력
        walkStartTimeText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkWalkStartTime.setVisibility(View.GONE);
                CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(WalkedActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedStartHour = hourOfDay;
                        selectedStartMin = minute;
                        String ampm;
                        int hour;
                        if(hourOfDay == 12) {
                            ampm = "오후";
                            hour = 12;
                        } else if (hourOfDay > 12) {
                            ampm = "오후";
                            hour = hourOfDay - 12;
                        } else {
                            ampm = "오전";
                            hour = hourOfDay;
                        }
                        if (minute == 0) {
                            walkStartTimeText.setText(ampm + " " + hour + "시");
                        } else {
                            walkStartTimeText.setText(ampm + " " + hour + "시 " + minute + "분");
                        }
                    }
                }, selectedStartHour,selectedStartMin,false);
                timePickerDialog.setTitle("시작시간");
                timePickerDialog.show();
            }
        });

        //산책시간 입력
        walkTimeText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkWalkTime.setVisibility(View.GONE);
                CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(WalkedActivity.this,  new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedHour = hourOfDay;
                        selectedMin = minute;
                        if (hourOfDay == 0) {
                            walkTimeText.setText(minute + "분");
                        } else {
                            walkTimeText.setText(hourOfDay + "시간 " + minute + "분");
                        }
                        walkTime = (hourOfDay * 60) + minute;
                    }
                }, selectedHour,selectedMin,true);
                timePickerDialog.setTitle("산책시간");
                timePickerDialog.show();
            }
        });

        //산책거리 입력
        walkDistanceText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkWalkDistance.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        //등록 버튼 클릭 이벤트
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (walkStartTimeText.getText().toString().length() == 0) { //시작시간 입력을 안했을 때
                    checkWalkStartTime.setVisibility(View.VISIBLE);
                } else if (walkTimeText.getText().toString().length() == 0) { //산책시간 입력을 안했을 때
                    checkWalkTime.setText("산책 시간을 입력해주세요.");
                    checkWalkTime.setVisibility(View.VISIBLE);
                } else if (walkTime < 5) { //산책시간이 5분 이하일 때
                    checkWalkTime.setText("산책 시간을 5분 이상 입력해주세요.");
                    checkWalkTime.setVisibility(View.VISIBLE);
                } else if (walkDistanceText.getText().toString().length() == 0 || walkDistanceText.getText().toString().equals(".")) { //산책거리 입력을 안했을 때
                    checkWalkDistance.setVisibility(View.VISIBLE);
                } else if ((Math.floor(Float.parseFloat(walkDistanceText.getText().toString()) * 10) / 10) == 0) { //산책거리 0km일 때
                    checkWalkDistance.setVisibility(View.VISIBLE);
                } else {
                    walkedLoadingView.setVisibility(View.VISIBLE);
                    insertButton.setEnabled(false);
                    if (MemberVO.getInstance().getGrade() == 1) {
                        if (mInterstitialAd.isLoaded()) {
                            //광고 보여주기
                            mInterstitialAd.show();
                            mInterstitialAd.setAdListener(new AdListener() {
                                @Override
                                public void onAdClosed() {
                                    walkedInsert();
                                }
                            });
                        } else {
                            walkedInsert();
                        }
                    } else {
                        walkedInsert();
                    }
                }
            }
        });
    }

    //editText가 아닌 곳 클릭하면 키보드 내려가게 하기
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void walkedInsert() {
        JSONObject walkObject = new JSONObject();
        try {
            walkObject.put("dog", HomeVO.getHomeVO().getDog().getId());
            walkObject.put("date", walkDate);
            walkObject.put("time", walkStartTimeText.getText().toString());
            walkObject.put("minutes", walkTime);
            walkObject.put("distance", Math.floor(Float.parseFloat(walkDistanceText.getText().toString()) * 10) / 10);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
            walkedLoadingView.setVisibility(View.GONE);
            insertButton.setEnabled(true);
        }
        JsonObjectRequest walkRequest = new JsonObjectRequest(Request.Method.POST, Common.URL + "/diary/walk", walkObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Gson gson = new Gson();
                            WalkVO[] list = gson.fromJson(response.getString("walkList"), WalkVO[].class);
                            List<WalkVO> alist = Arrays.asList(list);
                            ArrayList<WalkVO> arrayList = new ArrayList<WalkVO>();
                            arrayList.addAll(alist);
                            HomeVO.getInstance().setWalkList(arrayList);

                            String walkUrl = Common.URL+"/diary/walk/"+HomeVO.getInstance().getDog().getId();
                            StringRequest dogRequest = new StringRequest(
                                    Request.Method.GET,
                                    walkUrl,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Gson gson = new Gson();
                                            WalkVO[] list = gson.fromJson(response,WalkVO[].class);
                                            List<WalkVO> alist = Arrays.asList(list);
                                            ArrayList<WalkVO> arrayList = new ArrayList<WalkVO>();
                                            arrayList.addAll(alist);
                                            CalendarVO.getInstance().setWalkList(arrayList);

                                            Intent mainIntent = new Intent(getApplicationContext(), CalendarActivity.class);
                                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(mainIntent);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                                            walkedLoadingView.setVisibility(View.GONE);
                                            insertButton.setEnabled(true);
                                        }
                                    }
                            );
                            dogRequest.setShouldCache(false);
                            AppHelper.requestQueue.add(dogRequest);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
                            walkedLoadingView.setVisibility(View.GONE);
                            insertButton.setEnabled(true);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
                        walkedLoadingView.setVisibility(View.GONE);
                        insertButton.setEnabled(true);
                    }
                });
        walkRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        walkRequest.setShouldCache(false);
        AppHelper.requestQueue.add(walkRequest);
    }
}
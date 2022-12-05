package com.najin.dogdiary.update;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.najin.dogdiary.R;
import com.najin.dogdiary.calendar.CalendarActivity;
import com.najin.dogdiary.model.CalendarVO;
import com.najin.dogdiary.model.HomeVO;
import com.najin.dogdiary.model.WalkVO;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;
import com.najin.dogdiary.support.CustomTimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class UpdateWalkedActivity extends AppCompatActivity {

    ConstraintLayout walkedUpdateLoadingView;
    TextView walkUpdateDateText;
    TextView walkUpdateStartTimeText;
    TextView walkUpdateTimeText;
    TextView walkUpdateDistanceText;
    TextView checkWalkUpdateDistance;
    Button walkUpdateButton;

    String informationDateString;
    int walkId;
    String walkDate;
    String walkTime;
    int walkMinutes;
    double walkDistance;

    int selectedStartHour;
    int selectedStartMin;
    int selectedHour;
    int selectedMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_update_walked);

        walkedUpdateLoadingView = (ConstraintLayout) findViewById(R.id.walkedUpdateLoadingView);
        walkedUpdateLoadingView.setVisibility(View.GONE);

        walkUpdateDateText = (TextView) findViewById(R.id.walkUpdateDateText);
        walkUpdateStartTimeText = (TextView) findViewById(R.id.walkUpdateStartTimeText);
        walkUpdateTimeText = (TextView) findViewById(R.id.walkUpdateTimeText);
        walkUpdateDistanceText = (TextView) findViewById(R.id.walkUpdateDistanceText);
        checkWalkUpdateDistance = (TextView) findViewById(R.id.checkWalkUpdateDistance);
        checkWalkUpdateDistance.setVisibility(View.GONE);
        walkUpdateButton = (Button) findViewById(R.id.walkUpdateButton);

        //status bar 색상지정
        getWindow().setStatusBarColor(Color.parseColor("#dd695d"));

        //산책정보 셋팅
        Intent intent = getIntent();
        informationDateString = intent.getStringExtra("informationDateString");
        walkId = intent.getIntExtra("walkId",0);
        walkDate = intent.getStringExtra("walkDate");
        walkTime = intent.getStringExtra("walkTime");
        walkMinutes = intent.getIntExtra("walkMinutes",0);
        walkDistance = intent.getDoubleExtra("walkDistance",0);

        //산책 날짜 셋팅
        walkUpdateDateText.setText(informationDateString);

        //산책 시작시간 셋팅
        int hourIndex = walkTime.indexOf("시");
        selectedStartHour = Integer.parseInt(walkTime.substring(3,hourIndex));
        if (walkTime.substring(0,2).equals("오후") && selectedStartHour != 12) {
            selectedStartHour += 12;
        }
        if (walkTime.contains("분")) {
            int minIndex = walkTime.indexOf("분");
            selectedStartMin = Integer.parseInt(walkTime.substring(hourIndex+2,minIndex));
        } else {
            selectedStartMin = 0;
        }

        //산책 시간 셋팅
        walkUpdateStartTimeText.setText(walkTime);
        selectedHour = walkMinutes / 60;
        selectedMin = walkMinutes % 60;
        if (selectedHour == 0) {
            walkUpdateTimeText.setText(selectedMin+"분");
        } else {
            walkUpdateTimeText.setText(selectedHour+"시간 "+selectedMin+"분");
        }
        walkUpdateDistanceText.setText(walkDistance+"");

        //산책 날짜 입력
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear += 1;
                walkUpdateDateText.setText(year+"년 "+monthOfYear+"월 "+dayOfMonth+"일");
                String month;
                String day;
                if (monthOfYear < 10) {
                    month = "0" + monthOfYear;
                } else {
                    month = "" + monthOfYear;
                }
                if (dayOfMonth < 10) {
                    day = "0" + dayOfMonth;
                } else {
                    day = "" + dayOfMonth;
                }
                walkDate = year+"-"+month+"-"+day;
            }
        };
        walkUpdateDateText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar c = new GregorianCalendar();
                int mYear=c.get(Calendar.YEAR);
                int mMonth=c.get(Calendar.MONTH);
                int mDay=c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(UpdateWalkedActivity.this, android.R.style.Theme_Holo_Light_Dialog, listener,
                        Integer.parseInt(walkDate.substring(0,4)),
                        Integer.parseInt(walkDate.substring(5,7))-1,
                        Integer.parseInt(walkDate.substring(8,10)));

                Calendar maxDate = Calendar.getInstance();
                maxDate.set(mYear,mMonth,mDay);
                Calendar minDate = Calendar.getInstance();
                minDate.set(2020,0,1);
                dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
                dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
                dialog.setTitle("산책 날짜");

                dialog.show();
            }
        });

        //산책 시작시간 입력
        walkUpdateStartTimeText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(UpdateWalkedActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                            walkUpdateStartTimeText.setText(ampm + " " + hour + "시");
                        } else {
                            walkUpdateStartTimeText.setText(ampm + " " + hour + "시 " + minute + "분");
                        }
                    }
                }, selectedStartHour,selectedStartMin,false);
                timePickerDialog.setTitle("시작시간");
                timePickerDialog.show();
            }
        });

        //산책시간 입력
        walkUpdateTimeText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(UpdateWalkedActivity.this,  new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedHour = hourOfDay;
                        selectedMin = minute;
                        if (hourOfDay == 0) {
                            walkUpdateTimeText.setText(minute + "분");
                        } else {
                            walkUpdateTimeText.setText(hourOfDay + "시간 " + minute + "분");
                        }
                        walkMinutes = (hourOfDay * 60) + minute;
                    }
                }, selectedHour,selectedMin,true);
                timePickerDialog.setTitle("산책시간");
                timePickerDialog.show();
            }
        });

        //산책거리 입력
        walkUpdateDistanceText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkWalkUpdateDistance.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        //등록 버튼 클릭 이벤트
        walkUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (walkUpdateDistanceText.getText().toString().length() == 0 || walkUpdateDistanceText.getText().toString().equals(".")) { //산책거리 입력을 안했을 때
                    checkWalkUpdateDistance.setVisibility(View.VISIBLE);
                } else if ((Math.floor(Float.parseFloat(walkUpdateDistanceText.getText().toString()) * 10) / 10) == 0) { //산책거리 0km일 때
                    checkWalkUpdateDistance.setVisibility(View.VISIBLE);
                } else {
                    walkedUpdateLoadingView.setVisibility(View.VISIBLE);
                    walkUpdateButton.setEnabled(false);
                    walkedUpdate();
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

    private void walkedUpdate() {
        JSONObject walkObject = new JSONObject();
        try {
            walkObject.put("date", walkDate);
            walkObject.put("time", walkUpdateStartTimeText.getText().toString());
            walkObject.put("minutes", walkMinutes);
            walkObject.put("distance", Math.floor(Float.parseFloat(walkUpdateDistanceText.getText().toString()) * 10) / 10);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
            walkedUpdateLoadingView.setVisibility(View.GONE);
            walkUpdateButton.setEnabled(true);
        }
        JsonObjectRequest walkRequest = new JsonObjectRequest(Request.Method.PUT, Common.URL + "/diary/walk/" + walkId, walkObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setWalk();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
                        walkedUpdateLoadingView.setVisibility(View.GONE);
                        walkUpdateButton.setEnabled(true);
                    }
                });
        walkRequest.setShouldCache(false);
        AppHelper.requestQueue.add(walkRequest);
    }

    private void setWalk() {
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

                        loadHomeData();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        dogRequest.setShouldCache(false);
        AppHelper.requestQueue.add(dogRequest);
    }

    //home data 셋팅
    private void loadHomeData() {
        String dogId = HomeVO.getInstance().getDog().getId();
        String dogUrl = Common.URL + "/diary/home/" + dogId;
        StringRequest homeRequest = new StringRequest(
                Request.Method.GET,
                dogUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //homeVO 셋팅팅
                        Gson gson = new Gson();
                        HomeVO.setInstance(gson.fromJson(response, HomeVO.class));
                        Intent mainIntent = new Intent(getApplicationContext(), CalendarActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                        overridePendingTransition(0, 0);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        homeRequest.setShouldCache(false);
        AppHelper.requestQueue.add(homeRequest);
    }
}
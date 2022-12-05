package com.najin.dogdiary.update;

import android.app.DatePickerDialog;
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
import android.widget.EditText;
import android.widget.TextView;
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
import com.najin.dogdiary.model.WeightVO;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class UpdateWeightActivity extends AppCompatActivity {

    ConstraintLayout weightUpdateLoadingView;
    TextView weightUpdateDateText;

    Button updateButton;

    EditText dogWeightUpdateText;
    TextView weightUpdateText;
    TextView checkWeightUpdateText;

    String informationDateString;
    int weightId;
    String weightDate;
    double weight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_update_weight);

        weightUpdateLoadingView = (ConstraintLayout) findViewById(R.id.weightUpdateLoadingView);
        weightUpdateLoadingView.setVisibility(View.GONE);
        checkWeightUpdateText = (TextView) findViewById(R.id.checkWeightUpdateText);
        checkWeightUpdateText.setVisibility(View.GONE);
        dogWeightUpdateText = (EditText) findViewById(R.id.dogWeightUpdateText);
        weightUpdateText = (TextView) findViewById(R.id.weightUpdateText);
        weightUpdateDateText = (TextView) findViewById(R.id.weightUpdateDateText);
        updateButton = (Button) findViewById(R.id.weightupdateButton);

        //status bar 색상지정
        getWindow().setStatusBarColor(Color.parseColor("#65ab84"));

        //몸무게 정보 셋팅
        Intent intent = getIntent();
        informationDateString = intent.getStringExtra("informationDateString");
        weightId = intent.getIntExtra("weightId",0);
        weightDate = intent.getStringExtra("weightDate");
        weight = intent.getFloatExtra("weight",0);

        //몸무게 날짜 셋팅
        weightUpdateDateText.setText(informationDateString);
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear += 1;
                weightUpdateDateText.setText(year+"년 "+monthOfYear+"월 "+dayOfMonth+"일");
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
                weightDate = year+"-"+month+"-"+day;
            }
        };
        weightUpdateDateText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar c = new GregorianCalendar();
                int mYear=c.get(Calendar.YEAR);
                int mMonth=c.get(Calendar.MONTH);
                int mDay=c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(UpdateWeightActivity.this, android.R.style.Theme_Holo_Light_Dialog, listener,
                        Integer.parseInt(weightDate.substring(0,4)),
                        Integer.parseInt(weightDate.substring(5,7))-1,
                        Integer.parseInt(weightDate.substring(8,10)));

                Calendar maxDate = Calendar.getInstance();
                maxDate.set(mYear,mMonth,mDay);
                Calendar minDate = Calendar.getInstance();
                minDate.set(2020,0,1);
                dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
                dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
                dialog.setTitle("몸무게 날짜");

                dialog.show();
            }
        });

        //몸무게 셋팅
        dogWeightUpdateText.setText(weight+"");
        weightUpdateText.setText(weight+"kg");
        dogWeightUpdateText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkWeightUpdateText.setVisibility(View.GONE);
                setWeight();
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        //등록 버튼 클릭 이벤트
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weight > 0) {
                    weightUpdateLoadingView.setVisibility(View.VISIBLE);
                    updateButton.setEnabled(false);
                    weightUpdate();
                } else {
                    checkWeightUpdateText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //몸무게 계산
    private void setWeight() {
        double totalWeight = 0;
        double removeWeight = 0;
        double dogWeight = 0;

        if (!dogWeightUpdateText.getText().toString().equals("") && !dogWeightUpdateText.getText().toString().equals(".")) {
            dogWeight = Math.floor(Float.parseFloat(dogWeightUpdateText.getText().toString()) * 10) / 10; //뺄 몸무게
        };
        weight = Math.floor(dogWeight * 10) / 10;
        weightUpdateText.setText(weight + "kg");
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

    private void weightUpdate() {
        JSONObject weightObject = new JSONObject();
        try {
            weightObject.put("date", weightDate);
            weightObject.put("kg", weight);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
            weightUpdateLoadingView.setVisibility(View.GONE);
            updateButton.setEnabled(true);
        }
        JsonObjectRequest weightRequest = new JsonObjectRequest(Request.Method.PUT, Common.URL + "/diary/weight/" + weightId, weightObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setCalendarWeight();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
                        weightUpdateLoadingView.setVisibility(View.GONE);
                        updateButton.setEnabled(true);
                    }
                });
        weightRequest.setShouldCache(false);
        AppHelper.requestQueue.add(weightRequest);
    }

    private void setCalendarWeight() {
        String walkUrl = Common.URL+"/diary/weight/"+HomeVO.getInstance().getDog().getId();
        StringRequest dogRequest = new StringRequest(
                Request.Method.GET,
                walkUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        WeightVO[] list = gson.fromJson(response,WeightVO[].class);
                        List<WeightVO> alist = Arrays.asList(list);
                        ArrayList<WeightVO> arrayList = new ArrayList<WeightVO>();
                        arrayList.addAll(alist);
                        CalendarVO.getInstance().setWeightList(arrayList);

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




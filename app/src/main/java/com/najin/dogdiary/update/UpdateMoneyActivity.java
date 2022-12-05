package com.najin.dogdiary.update;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
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
import com.najin.dogdiary.model.MoneyVO;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class UpdateMoneyActivity extends AppCompatActivity {

    ConstraintLayout moneyUpdateLoadingView;

    TextView checkUpdateItemText;
    TextView checkUpdatePriceText;

    Button foodButton;
    Button toyButton;
    Button hospitalButton;
    Button beautyButton;
    Button etcButton;

    TextView moneyUpdateDateText;
    EditText moneyUpdateItemText;
    EditText moneyPriceText;
    Button moneyUpdateButton;

    String informationDateString;
    int moneyId;
    String moneyDate;
    int moneyType;
    String moneyItem;
    int moneyPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_update_money);

        moneyUpdateLoadingView = (ConstraintLayout) findViewById(R.id.moneyUpdateLoadingView);
        moneyUpdateLoadingView.setVisibility(View.GONE);

        //status bar 색상지정
        getWindow().setStatusBarColor(Color.parseColor("#5e4fa2"));

        //몸무게 정보 셋팅
        Intent intent = getIntent();
        informationDateString = intent.getStringExtra("informationDateString");
        moneyId = intent.getIntExtra("moneyId",0);
        moneyDate = intent.getStringExtra("moneyDate");
        moneyType = intent.getIntExtra("moneyType",0);
        moneyItem = intent.getStringExtra("moneyItem");
        moneyPrice = intent.getIntExtra("moneyPrice",0);

        foodButton = (Button) findViewById(R.id.foodUpdateButton);
        toyButton = (Button) findViewById(R.id.toyUpdateButton);
        hospitalButton = (Button) findViewById(R.id.hospitalUpdateButton);
        beautyButton = (Button) findViewById(R.id.beautyUpdateButton);
        etcButton = (Button) findViewById(R.id.etcUpdateButton);
        buttonUnSelect(foodButton);
        buttonUnSelect(toyButton);
        buttonUnSelect(hospitalButton);
        buttonUnSelect(beautyButton);
        buttonUnSelect(etcButton);

        checkUpdateItemText = (TextView) findViewById(R.id.checkUpdateItemText);
        checkUpdatePriceText = (TextView) findViewById(R.id.checkUpdatePriceText);
        checkUpdateItemText.setVisibility(View.GONE);
        checkUpdatePriceText.setVisibility(View.GONE);

        //지출 날짜 셋팅
        moneyUpdateDateText = (TextView) findViewById(R.id.moneyUpdateDateText);
        moneyUpdateDateText.setText(informationDateString);
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear += 1;
                moneyUpdateDateText.setText(year+"년 "+monthOfYear+"월 "+dayOfMonth+"일");
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
                moneyDate = year+"-"+month+"-"+day;
            }
        };
        moneyUpdateDateText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar c = new GregorianCalendar();
                int mYear=c.get(Calendar.YEAR);
                int mMonth=c.get(Calendar.MONTH);
                int mDay=c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(UpdateMoneyActivity.this, android.R.style.Theme_Holo_Light_Dialog, listener,
                        Integer.parseInt(moneyDate.substring(0,4)),
                        Integer.parseInt(moneyDate.substring(5,7))-1,
                        Integer.parseInt(moneyDate.substring(8,10)));

                Calendar maxDate = Calendar.getInstance();
                maxDate.set(mYear,mMonth,mDay);
                Calendar minDate = Calendar.getInstance();
                minDate.set(2020,0,1);
                dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
                dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
                dialog.setTitle("지출 날짜");

                dialog.show();
            }
        });

        //지출 분류 셋팅
        switch (moneyType) {
            case 1:
                buttonSelect(foodButton);
                break;
            case 2:
                buttonSelect(toyButton);
                break;
            case 3:
                buttonSelect(hospitalButton);
                break;
            case 4:
                buttonSelect(beautyButton);
                break;
            case 5:
                buttonSelect(etcButton);
                break;
        }

        foodButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonSelect(foodButton);
                buttonUnSelect(toyButton);
                buttonUnSelect(hospitalButton);
                buttonUnSelect(beautyButton);
                buttonUnSelect(etcButton);
                moneyType = 1;
            }
        });
        toyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonUnSelect(foodButton);
                buttonSelect(toyButton);
                buttonUnSelect(hospitalButton);
                buttonUnSelect(beautyButton);
                buttonUnSelect(etcButton);
                moneyType = 2;
            }
        });
        hospitalButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonUnSelect(foodButton);
                buttonUnSelect(toyButton);
                buttonSelect(hospitalButton);
                buttonUnSelect(beautyButton);
                buttonUnSelect(etcButton);
                moneyType = 3;
            }
        });
        beautyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonUnSelect(foodButton);
                buttonUnSelect(toyButton);
                buttonUnSelect(hospitalButton);
                buttonSelect(beautyButton);
                buttonUnSelect(etcButton);
                moneyType = 4;
            }
        });
        etcButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonUnSelect(foodButton);
                buttonUnSelect(toyButton);
                buttonUnSelect(hospitalButton);
                buttonUnSelect(beautyButton);
                buttonSelect(etcButton);
                moneyType = 5;
            }
        });

        //내역,금액 유효성검사
        moneyUpdateItemText = (EditText) findViewById(R.id.moneyUpdateItem);
        moneyUpdateItemText.setText(moneyItem);
        moneyUpdateItemText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkUpdateItemText.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
        moneyPriceText = (EditText) findViewById(R.id.moneyUpdatePrice);
        moneyPriceText.setText(moneyPrice+"");
        moneyPriceText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkUpdatePriceText.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        //등록 버튼 클릭 이벤트
        moneyUpdateButton = (Button) findViewById(R.id.moneyUpdateButton);
        moneyUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moneyUpdateItemText.getText().toString().trim().length() == 0) {
                    checkUpdateItemText.setVisibility(View.VISIBLE);
                } else if (moneyPriceText.getText().toString().length() == 0) {
                    checkUpdatePriceText.setVisibility(View.VISIBLE);
                } else {
                    moneyUpdateLoadingView.setVisibility(View.VISIBLE);
                    moneyUpdateButton.setEnabled(false);
                    moneyUpdate();
                }
            }
        });
    }

    private void buttonSelect(Button button) {
        button.setBackgroundResource(R.drawable.button_border_select);
        button.setTextColor(Color.parseColor("#5e4fa2"));
        button.setTypeface(null, Typeface.BOLD);
    }

    private void buttonUnSelect(Button button) {
        button.setBackgroundResource(R.drawable.button_border_unselect);
        button.setTextColor(Color.parseColor("gray"));
        button.setTypeface(null, Typeface.NORMAL);
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

    private void moneyUpdate() {
        JSONObject moneyObject = new JSONObject();
        try {
            moneyObject.put("date", moneyDate);
            moneyObject.put("type", moneyType);
            moneyObject.put("item", moneyUpdateItemText.getText().toString());
            moneyObject.put("price", moneyPriceText.getText().toString());
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
            moneyUpdateLoadingView.setVisibility(View.GONE);
            moneyUpdateButton.setEnabled(true);
        }
        JsonObjectRequest moneyRequest = new JsonObjectRequest(Request.Method.PUT, Common.URL + "/diary/money/" + moneyId, moneyObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setMoney();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
                        moneyUpdateLoadingView.setVisibility(View.GONE);
                        moneyUpdateButton.setEnabled(true);
                    }
                });
        moneyRequest.setShouldCache(false);
        AppHelper.requestQueue.add(moneyRequest);
    }

    private void setMoney() {
        String moneyUrl = Common.URL+"/diary/money/"+HomeVO.getInstance().getDog().getId();
        StringRequest dogRequest = new StringRequest(
                Request.Method.GET,
                moneyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        MoneyVO[] list = gson.fromJson(response,MoneyVO[].class);
                        List<MoneyVO> alist = Arrays.asList(list);
                        ArrayList<MoneyVO> arrayList = new ArrayList<MoneyVO>();
                        arrayList.addAll(alist);
                        CalendarVO.getInstance().setMoneyList(arrayList);

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
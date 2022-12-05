package com.najin.dogdiary.information;

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
import android.widget.EditText;
import android.widget.TextView;
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
import com.najin.dogdiary.home.HomeActivity;
import com.najin.dogdiary.model.CalendarVO;
import com.najin.dogdiary.model.HomeVO;
import com.najin.dogdiary.model.MemberVO;
import com.najin.dogdiary.model.MoneyVO;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MoneyActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;

    ConstraintLayout moneyLoadingView;

    TextView moneyDateText;
    EditText moneyItem;
    EditText moneyPrice;
    Button insertButton;
    String moneyDate;

    TextView checkTypeText;
    TextView checkItemText;
    TextView checkPriceText;

    Integer type = 0;
    Button foodButton;
    Button toyButton;
    Button hospitalButton;
    Button beautyButton;
    Button etcButton;

    String informationDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_money);

        moneyLoadingView = (ConstraintLayout) findViewById(R.id.moneyLoadingView);
        moneyLoadingView.setVisibility(View.GONE);

        //status bar 색상지정
        getWindow().setStatusBarColor(Color.parseColor("#5e4fa2"));

        //admob 셋팅
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //test
//        mInterstitialAd.setAdUnitId("ca-app-pub-1025720268361094/3562644757");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        //오늘인지 다른날짜인지 확인
        Intent intent = getIntent();
        informationDate = intent.getExtras().getString("informationDate");
        moneyDateText = (TextView) findViewById(R.id.moneyDate);
        if (informationDate.equals("today")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = simpleDateFormat.format(new Date());
            moneyDateText.setText("오늘");
            moneyDate = today;
        } else {
            String moneyDateString = intent.getExtras().getString("informationDateString");
            moneyDateText.setText(moneyDateString);
            moneyDate = informationDate;
        }

        checkTypeText = (TextView) findViewById(R.id.checkTypeText);
        checkItemText = (TextView) findViewById(R.id.checkItemText);
        checkPriceText = (TextView) findViewById(R.id.checkPriceText);
        checkTypeText.setVisibility(View.GONE);
        checkItemText.setVisibility(View.GONE);
        checkPriceText.setVisibility(View.GONE);

        //지출내역, 금액 유효성검사
        moneyItem = (EditText) findViewById(R.id.moneyItem);
        moneyItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkItemText.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        moneyPrice = (EditText) findViewById(R.id.moneyPrice);
        moneyPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPriceText.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        //분류 입력
        foodButton = (Button) findViewById(R.id.foodButton);
        toyButton = (Button) findViewById(R.id.toyButton);
        hospitalButton = (Button) findViewById(R.id.hospitalButton);
        beautyButton = (Button) findViewById(R.id.beautyButton);
        etcButton = (Button) findViewById(R.id.etcButton);
        buttonUnSelect(foodButton);
        buttonUnSelect(toyButton);
        buttonUnSelect(hospitalButton);
        buttonUnSelect(beautyButton);
        buttonUnSelect(etcButton);

        foodButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonSelect(foodButton);
                buttonUnSelect(toyButton);
                buttonUnSelect(hospitalButton);
                buttonUnSelect(beautyButton);
                buttonUnSelect(etcButton);
                checkTypeText.setVisibility(View.GONE);
                type = 1;
            }
        });
        toyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonUnSelect(foodButton);
                buttonSelect(toyButton);
                buttonUnSelect(hospitalButton);
                buttonUnSelect(beautyButton);
                buttonUnSelect(etcButton);
                checkTypeText.setVisibility(View.GONE);
                type = 2;
            }
        });
        hospitalButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonUnSelect(foodButton);
                buttonUnSelect(toyButton);
                buttonSelect(hospitalButton);
                buttonUnSelect(beautyButton);
                buttonUnSelect(etcButton);
                checkTypeText.setVisibility(View.GONE);
                type = 3;
            }
        });
        beautyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonUnSelect(foodButton);
                buttonUnSelect(toyButton);
                buttonUnSelect(hospitalButton);
                buttonSelect(beautyButton);
                buttonUnSelect(etcButton);
                checkTypeText.setVisibility(View.GONE);
                type = 4;
            }
        });
        etcButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonUnSelect(foodButton);
                buttonUnSelect(toyButton);
                buttonUnSelect(hospitalButton);
                buttonUnSelect(beautyButton);
                buttonSelect(etcButton);
                checkTypeText.setVisibility(View.GONE);
                type = 5;
            }
        });

        //등록 버튼 클릭 이벤트
        insertButton = (Button) findViewById(R.id.moneyInsertButton);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0) {
                    checkTypeText.setVisibility(View.VISIBLE);
                } else if (moneyItem.getText().toString().trim().length() == 0) {
                    checkItemText.setVisibility(View.VISIBLE);
                } else if (moneyPrice.getText().toString().length() == 0) {
                    checkPriceText.setVisibility(View.VISIBLE);
                } else {
                    moneyLoadingView.setVisibility(View.VISIBLE);
                    insertButton.setEnabled(false);
                    if (MemberVO.getInstance().getGrade() == 1) {
                        if (mInterstitialAd.isLoaded()) {
                            //광고 보여주기
                            mInterstitialAd.show();
                            mInterstitialAd.setAdListener(new AdListener() {
                                @Override
                                public void onAdClosed() {
                                    moneyInsert();
                                }
                            });
                        } else {
                            moneyInsert();
                        }
                    } else {
                        moneyInsert();
                    }
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

    private void moneyInsert() {
        JSONObject money = new JSONObject();
        try {
            money.put("dog", HomeVO.getHomeVO().getDog().getId());
            money.put("type", type);
            money.put("item", moneyItem.getText().toString());
            money.put("price", moneyPrice.getText().toString());
            money.put("date", moneyDate);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
            moneyLoadingView.setVisibility(View.GONE);
            insertButton.setEnabled(true);
        }
        JsonObjectRequest smsRequest = new JsonObjectRequest(Request.Method.POST, Common.URL + "/diary/money", money,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Gson gson = new Gson();
                        HomeVO.getInstance().setTotalMoney(response.getInt("totalMoney"));

                        MoneyVO[] list = gson.fromJson(response.getString("moneyList"), MoneyVO[].class);
                        List<MoneyVO> alist = Arrays.asList(list);
                        ArrayList<MoneyVO> arrayList = new ArrayList<MoneyVO>();
                        arrayList.addAll(alist);
                        HomeVO.getInstance().setMoneyList(arrayList);

                        setMoney();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
                        moneyLoadingView.setVisibility(View.GONE);
                        insertButton.setEnabled(true);
                    }

                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
                    moneyLoadingView.setVisibility(View.GONE);
                    insertButton.setEnabled(true);
                }
            });
        smsRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        smsRequest.setShouldCache(false);
        AppHelper.requestQueue.add(smsRequest);
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

                        startMainActivity();
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

    //메인 페이지로 이동
    private void startMainActivity() {
        if (informationDate.equals("today")) {
            //홈으로 이동
            Intent mainIntent = new Intent(getApplicationContext(), HomeActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
        } else {
            //캘린더로 이동
            Intent mainIntent = new Intent(getApplicationContext(), CalendarActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
        }
    }
}
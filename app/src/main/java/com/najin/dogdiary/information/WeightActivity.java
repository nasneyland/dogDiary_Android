package com.najin.dogdiary.information;

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
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.najin.dogdiary.model.WeightVO;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class WeightActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;

    ConstraintLayout weightLoadingView;

    TextView weightDateText;
    Button insertButton;
    String weightDate;
    String informationDate;

    RadioButton calculatorButton;
    RadioButton directButton;
    LinearLayout calculatorView;
    LinearLayout directView;
    EditText totalWeightText;
    EditText removeWeightText;
    EditText dogWeightText;
    TextView weightText;
    TextView weightInfoText;

    Integer weightType = 1;
    double weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_weight);

        weightLoadingView = (ConstraintLayout) findViewById(R.id.weightLoadingView);
        weightLoadingView.setVisibility(View.GONE);

        //status bar 색상지정
        getWindow().setStatusBarColor(Color.parseColor("#65ab84"));

        //admob 셋팅
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //test
//        mInterstitialAd.setAdUnitId("ca-app-pub-1025720268361094/3562644757");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        //오늘인지 다른날짜인지 확인
        Intent intent = getIntent();
        informationDate = intent.getExtras().getString("informationDate");
        weightDateText = (TextView) findViewById(R.id.weightDate);
        if (informationDate.equals("today")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = simpleDateFormat.format(new Date());
            weightDateText.setText("오늘");
            weightDate = today;
        } else {
            String weightDateString = intent.getExtras().getString("informationDateString");
            weightDateText.setText(weightDateString);
            weightDate = informationDate;
        }

        //몸무게 관련 뷰 선언
        calculatorButton = (RadioButton) findViewById(R.id.calculatorButton);
        directButton = (RadioButton) findViewById(R.id.directButton);

        calculatorView = (LinearLayout) findViewById(R.id.calculatorView);
        directView = (LinearLayout) findViewById(R.id.directView);
        directView.setVisibility(View.GONE);

        totalWeightText = (EditText) findViewById(R.id.totalWeightText);
        removeWeightText = (EditText) findViewById(R.id.removeWeightText);
        dogWeightText = (EditText) findViewById(R.id.dogWeightText);
        weightText = (TextView) findViewById(R.id.weightText);
        weightInfoText = (TextView) findViewById(R.id.weightInfoText);

        //계산기 모드
        calculatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculatorButton.setBackgroundResource(R.drawable.radio_select);
                directButton.setBackgroundResource(R.drawable.radio_unselect);
                calculatorView.setVisibility(View.VISIBLE);
                directView.setVisibility(View.GONE);
                totalWeightText.setText("");
                removeWeightText.setText("");
                weightText.setText("0.0kg");
                weightInfoText.setText("");
                weightType = 1;
            }
        });

        //직접입력 모드
        directButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                directButton.setBackgroundResource(R.drawable.radio_select);
                calculatorButton.setBackgroundResource(R.drawable.radio_unselect);
                directView.setVisibility(View.VISIBLE);
                calculatorView.setVisibility(View.GONE);
                dogWeightText.setText("");
                weightText.setText("0.0kg");
                weightInfoText.setText("");
                weightType = 2;
            }
        });

        //몸무게 입력시 몸무게 계산 후 셋팅
        totalWeightText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setWeight();
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        removeWeightText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setWeight();
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        dogWeightText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
        insertButton = (Button) findViewById(R.id.weightInsertButton);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weight > 0) {
                    weightLoadingView.setVisibility(View.VISIBLE);
                    insertButton.setEnabled(false);
                    if (MemberVO.getInstance().getGrade() == 1) {
                        if (mInterstitialAd.isLoaded()) {
                            //광고 보여주기
                            mInterstitialAd.show();
                            mInterstitialAd.setAdListener(new AdListener() {
                                @Override
                                public void onAdClosed() {
                                weightInsert();
                                }
                            });
                        } else {
                            weightInsert();
                        }
                    } else {
                        weightInsert();
                    }
                } else {
                    weightInfoText.setText("몸무게를 정확히 입력해주세요.");
                }
            }
        });
    }

    //몸무게 계산
    private void setWeight() {
        double totalWeight = 0;
        double removeWeight = 0;
        double dogWeight = 0;

        //몸무게 라벨
        if (weightType == 1) {
            if (!totalWeightText.getText().toString().equals("") && !totalWeightText.getText().toString().equals(".")) {
                totalWeight = Math.floor(Float.parseFloat(totalWeightText.getText().toString()) * 10) / 10; //전체 몸무게
            };
            if (!removeWeightText.getText().toString().equals("") && !removeWeightText.getText().toString().equals(".")) {
                removeWeight = Math.floor(Float.parseFloat(removeWeightText.getText().toString()) * 10) / 10; //뺄 몸무게
            };
            BigDecimal totalWeight2 = new BigDecimal(String.valueOf(totalWeight));
            BigDecimal removeWeight2 = new BigDecimal(String.valueOf(removeWeight));

            weight = totalWeight2.subtract(removeWeight2).doubleValue();
            weightText.setText(weight + "kg");
        } else {
            if (!dogWeightText.getText().toString().equals("") && !dogWeightText.getText().toString().equals(".")) {
                dogWeight = Math.floor(Float.parseFloat(dogWeightText.getText().toString()) * 10) / 10; //뺄 몸무게
            };
            weight = Math.floor(dogWeight * 10) / 10;
            weightText.setText(weight + "kg");
        }

        //몸무게 변화 라벨
        if (HomeVO.getInstance().getLastWeight() != null && informationDate.equals("today")) {
            double diffWeight = weight - HomeVO.getInstance().getLastWeight();
            if (weight <= 0) {
                weightInfoText.setText("몸무게를 정확히 입력해주세요.");
            } else if (diffWeight > 0) {
                weightInfoText.setText(String.format("%d일 전보다 %.1f키로 증가했습니다.",HomeVO.getInstance().getLastWeightDay(),Math.abs(diffWeight)));
            } else if (diffWeight < 0) {
                weightInfoText.setText(String.format("%d일 전보다 %.1f키로 감소했습니다.",HomeVO.getInstance().getLastWeightDay(),Math.abs(diffWeight)));
            } else {
                weightInfoText.setText(HomeVO.getInstance().getLastWeightDay()+"일 전과 몸무게가 동일합니다.");
            }
        } else {
            weightInfoText.setText("");
        }
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

    private void weightInsert() {
        JSONObject weightObject = new JSONObject();
        try {
            weightObject.put("dog", HomeVO.getHomeVO().getDog().getId());
            weightObject.put("date", weightDate);
            weightObject.put("kg", weight);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
            weightLoadingView.setVisibility(View.GONE);
            insertButton.setEnabled(true);
        }
        JsonObjectRequest weightRequest = new JsonObjectRequest(Request.Method.POST, Common.URL + "/diary/weight", weightObject,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Gson gson = new Gson();
                        HomeVO.getInstance().setLastWeight((float)response.getDouble("lastWeight"));
                        HomeVO.getInstance().setLastWeightDay(response.getInt("lastWeightDay"));

                        WeightVO[] list = gson.fromJson(response.getString("weightChart"), WeightVO[].class);
                        List<WeightVO> alist = Arrays.asList(list);
                        ArrayList<WeightVO> arrayList = new ArrayList<WeightVO>();
                        arrayList.addAll(alist);
                        HomeVO.getInstance().setWeightChart(arrayList);
                        setCalendarWeight();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
                        weightLoadingView.setVisibility(View.GONE);
                        insertButton.setEnabled(true);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
                    weightLoadingView.setVisibility(View.GONE);
                    insertButton.setEnabled(true);
                }
            });
        weightRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        weightRequest.setShouldCache(false);
        AppHelper.requestQueue.add(weightRequest);
    }

    private void setCalendarWeight() {
        String weightUrl = Common.URL+"/diary/weight/"+HomeVO.getInstance().getDog().getId();
        StringRequest dogRequest = new StringRequest(
                Request.Method.GET,
                weightUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        WeightVO[] list = gson.fromJson(response,WeightVO[].class);
                        List<WeightVO> alist = Arrays.asList(list);
                        ArrayList<WeightVO> arrayList = new ArrayList<WeightVO>();
                        arrayList.addAll(alist);
                        CalendarVO.getInstance().setWeightList(arrayList);
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




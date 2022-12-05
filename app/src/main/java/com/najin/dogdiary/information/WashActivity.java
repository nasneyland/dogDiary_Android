package com.najin.dogdiary.information;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.najin.dogdiary.model.WashVO;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class WashActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;

    ConstraintLayout washLoadingView;
    TextView washDateText;
    Button insertButton;
    String washDate;

    String informationDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_wash);

        washLoadingView = (ConstraintLayout) findViewById(R.id.washLoadingView);
        washLoadingView.setVisibility(View.GONE);

        //status bar 색상지정
        getWindow().setStatusBarColor(Color.parseColor("#fdae61"));

        //admob 셋팅
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //test
//        mInterstitialAd.setAdUnitId("ca-app-pub-1025720268361094/3562644757");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        //오늘인지 다른날짜인지 확인
        Intent intent = getIntent();
        informationDate = intent.getExtras().getString("informationDate");
        washDateText = (TextView) findViewById(R.id.washDate);
        if (informationDate.equals("today")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = simpleDateFormat.format(new Date());
            washDateText.setText("오늘");
            washDate = today;
        } else {
            String washDateString = intent.getExtras().getString("informationDateString");
            washDateText.setText(washDateString);
            washDate = informationDate;
        }

        //등록 버튼 클릭 이벤트
        insertButton = (Button) findViewById(R.id.washInsertButton);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                washLoadingView.setVisibility(View.VISIBLE);
                insertButton.setEnabled(false);
                if (MemberVO.getInstance().getGrade() == 1) {
                    if (mInterstitialAd.isLoaded()) {
                        //광고 보여주기
                        mInterstitialAd.show();
                        mInterstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                            washInsert();
                            }
                        });
                    } else {
                        washInsert();
                    }
                } else {
                    washInsert();
                }
            }
        });
    }

    //목욕 입력
    private void washInsert() {
        JSONObject wash = new JSONObject();
        try {
            wash.put("dog", HomeVO.getHomeVO().getDog().getId());
            wash.put("date", washDate);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
            washLoadingView.setVisibility(View.GONE);
            insertButton.setEnabled(true);
        }
        JsonObjectRequest washRequest = new JsonObjectRequest(Request.Method.POST, Common.URL + "/diary/wash",wash,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            HomeVO.getInstance().setLastWashDay(response.getInt("lastWashDay"));
                            String washUrl = Common.URL+"/diary/wash/"+HomeVO.getInstance().getDog().getId();
                            StringRequest dogRequest = new StringRequest(
                                    Request.Method.GET,
                                    washUrl,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Gson gson = new Gson();
                                            WashVO[] list = gson.fromJson(response,WashVO[].class);
                                            List<WashVO> alist = Arrays.asList(list);
                                            ArrayList<WashVO> arrayList = new ArrayList<WashVO>();
                                            arrayList.addAll(alist);
                                            CalendarVO.getInstance().setWashList(arrayList);
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                            washLoadingView.setVisibility(View.GONE);
                            insertButton.setEnabled(true);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                        washLoadingView.setVisibility(View.GONE);
                        insertButton.setEnabled(true);
                    }
                });
        washRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        washRequest.setShouldCache(false);
        AppHelper.requestQueue.add(washRequest);
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
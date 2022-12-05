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
import com.najin.dogdiary.model.HeartVO;
import com.najin.dogdiary.model.HomeVO;
import com.najin.dogdiary.model.MemberVO;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class HeartActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;

    ConstraintLayout heartLoadingView;
    TextView heartDateText;
    Button insertButton;
    String heartDate;

    String informationDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_heart);

        heartLoadingView = (ConstraintLayout) findViewById(R.id.heartLoadingView);
        heartLoadingView.setVisibility(View.GONE);

        //status bar 색상지정
        getWindow().setStatusBarColor(Color.parseColor("#4575b4"));

        //admob 셋팅
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //test
//        mInterstitialAd.setAdUnitId("ca-app-pub-1025720268361094/3562644757");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        //오늘인지 다른날짜인지 확인
        Intent intent = getIntent();
        informationDate = intent.getExtras().getString("informationDate");
        heartDateText = (TextView) findViewById(R.id.heartDate);
        if (informationDate.equals("today")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = simpleDateFormat.format(new Date());
            heartDateText.setText("오늘");
            heartDate = today;
        } else {
            String heartDateString = intent.getExtras().getString("informationDateString");
            heartDateText.setText(heartDateString);
            heartDate = informationDate;
        }

        //등록 버튼 클릭 이벤트
        insertButton = (Button) findViewById(R.id.heartInsertButton);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heartLoadingView.setVisibility(View.VISIBLE);
                insertButton.setEnabled(false);
                if (MemberVO.getInstance().getGrade() == 1) {
                    if (mInterstitialAd.isLoaded()) {
                        //광고 보여주기
                        mInterstitialAd.show();
                        mInterstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                heartInsert();
                            }
                        });
                    } else {
                        heartInsert();
                    }
                } else {
                    heartInsert();
                }
            }
        });
    }

    //심장사상충 입력
    private void heartInsert() {
        JSONObject heart = new JSONObject();
        try {
            heart.put("dog", HomeVO.getHomeVO().getDog().getId());
            heart.put("date", heartDate);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
            heartLoadingView.setVisibility(View.GONE);
            insertButton.setEnabled(true);
        }
        JsonObjectRequest smsRequest = new JsonObjectRequest(Request.Method.POST, Common.URL + "/diary/heart",heart,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            HomeVO.getInstance().setLastHeartDay(response.getInt("lastHeartDay"));
                            String heartUrl = Common.URL+"/diary/heart/"+HomeVO.getInstance().getDog().getId();
                            StringRequest dogRequest = new StringRequest(
                                    Request.Method.GET,
                                    heartUrl,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Gson gson = new Gson();
                                            HeartVO[] list = gson.fromJson(response,HeartVO[].class);
                                            List<HeartVO> alist = Arrays.asList(list);
                                            ArrayList<HeartVO> arrayList = new ArrayList<HeartVO>();
                                            arrayList.addAll(alist);
                                            CalendarVO.getInstance().setHeartList(arrayList);
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
                            heartLoadingView.setVisibility(View.GONE);
                            insertButton.setEnabled(true);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                        heartLoadingView.setVisibility(View.GONE);
                        insertButton.setEnabled(true);
                    }
                });
        smsRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        smsRequest.setShouldCache(false);
        AppHelper.requestQueue.add(smsRequest);
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




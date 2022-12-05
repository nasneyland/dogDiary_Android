package com.najin.dogdiary.information;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ScrollView;
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
import com.najin.dogdiary.model.EtcVO;
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

public class EtcActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;

    ConstraintLayout etcLoadingView;
    ScrollView etc_scroll;

    String informationDate;
    TextView etcDateText;
    EditText etcTitle;
    EditText etcContent;
    TextView etc_content_count;
    Button insertButton;
    String etcDate;
    GridView colorSet;

    TextView checkTitleText;
    TextView checkContentText;
    TextView checkColorText;

    String[] colors = new String[]{"#e9a799", "#db8438", "#c76026", "#963f2e", "#edc233",
            "#97d5e0", "#89bbb8", "#479a83", "#5c7148", "#4a5336",
            "#489ad8", "#bfa7f6", "#a7a3f8", "#8357ac", "#3c5066",
            "#bec3c7", "#999999", "#818b8d", "#798da5", "#495057"};
    String selectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_etc);

        etc_scroll = (ScrollView) findViewById(R.id.etc_scroll);
        etcLoadingView = (ConstraintLayout) findViewById(R.id.etcLoadingView);
        etcLoadingView.setVisibility(View.GONE);

        //status bar 색상지정
        getWindow().setStatusBarColor(Color.parseColor("#707578"));

        //admob 셋팅
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //test
//        mInterstitialAd.setAdUnitId("ca-app-pub-1025720268361094/3562644757");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        //오늘인지 다른날짜인지 확인
        Intent intent = getIntent();
        informationDate = intent.getExtras().getString("informationDate");
        etcDateText = (TextView) findViewById(R.id.etcDate);
        if (informationDate.equals("today")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = simpleDateFormat.format(new Date());
            etcDateText.setText("오늘");
            etcDate = today;
        } else {
            String moneyDateString = intent.getExtras().getString("informationDateString");
            etcDateText.setText(moneyDateString);
            etcDate = informationDate;
        }

        checkTitleText = (TextView) findViewById(R.id.checkTitleText);
        checkContentText = (TextView) findViewById(R.id.checkContentText);
        checkColorText = (TextView) findViewById(R.id.checkColorText);
        checkTitleText.setVisibility(View.GONE);
        checkContentText.setVisibility(View.GONE);
        checkColorText.setVisibility(View.GONE);

        //색상 팔레트 셋팅
        EtcColorView.selected = null;
        colorSet = (GridView) findViewById(R.id.colorSet);
        EtcColorAdapter adapter = new EtcColorAdapter(this);
        colorSet.setAdapter(adapter);
        colorSet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedColor = colors[position];
                checkColorText.setVisibility(View.GONE);
                EtcColorView.selected = position;
                colorSet.invalidateViews();
            }
        });

        //제목 입력 유효성검사
        etcTitle = (EditText) findViewById(R.id.etcTitle);
        etcTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkTitleText.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        etcContent = (EditText) findViewById(R.id.etcContent);
        etc_content_count = (TextView) findViewById(R.id.etc_content_count);
        etcContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkContentText.setVisibility(View.GONE);
                etc_content_count.setText(s.length()+"/100");
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        //등록 버튼 클릭 이벤트
        insertButton = (Button) findViewById(R.id.etcInsertButton);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedColor == null) {
                    checkColorText.setVisibility(View.VISIBLE);
                } else if (etcTitle.getText().toString().trim().length() == 0) {
                    checkTitleText.setVisibility(View.VISIBLE);
                } else if (etcContent.getText().toString().trim().length() == 0) {
                    checkContentText.setVisibility(View.VISIBLE);
                } else {
                    etcLoadingView.setVisibility(View.VISIBLE);
                    insertButton.setEnabled(false);
                    if (MemberVO.getInstance().getGrade() == 1) {
                        if (mInterstitialAd.isLoaded()) {
                            //광고 보여주기
                            mInterstitialAd.show();
                            mInterstitialAd.setAdListener(new AdListener() {
                                @Override
                                public void onAdClosed() {
                                    etcInsert();
                                }
                            });
                        } else {
                            etcInsert();
                        }
                    } else {
                        etcInsert();
                    }
                }
            }
        });
    }

    private void etcInsert() {
        JSONObject etc = new JSONObject();
        try {
            etc.put("dog", HomeVO.getHomeVO().getDog().getId());
            etc.put("color", selectedColor);
            etc.put("title", etcTitle.getText().toString());
            etc.put("content", etcContent.getText().toString());
            etc.put("date",etcDate);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
            etcLoadingView.setVisibility(View.GONE);
            insertButton.setEnabled(true);
        }
        JsonObjectRequest etcRequest = new JsonObjectRequest(Request.Method.POST, Common.URL + "/diary/etc", etc,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    setEtc();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
                    etcLoadingView.setVisibility(View.GONE);
                    insertButton.setEnabled(true);
                }
            });
        etcRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        etcRequest.setShouldCache(false);
        AppHelper.requestQueue.add(etcRequest);
    }

    private void setEtc() {
        String etcUrl = Common.URL+"/diary/etc/"+HomeVO.getInstance().getDog().getId();
        StringRequest dogRequest = new StringRequest(
                Request.Method.GET,
                etcUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        EtcVO[] list = gson.fromJson(response,EtcVO[].class);
                        List<EtcVO> alist = Arrays.asList(list);
                        ArrayList<EtcVO> arrayList = new ArrayList<EtcVO>();
                        arrayList.addAll(alist);
                        CalendarVO.getInstance().setEtcList(arrayList);

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
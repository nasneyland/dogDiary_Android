package com.najin.dogdiary.member;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.najin.dogdiary.R;
import com.najin.dogdiary.calendar.CalendarSet;
import com.najin.dogdiary.home.HomeActivity;
import com.najin.dogdiary.model.HomeVO;
import com.najin.dogdiary.model.MemberVO;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences user;

    @Override
    protected void onCreate(Bundle savedInstanceStare) {
        super.onCreate(savedInstanceStare);
        setContentView(R.layout.activity_splash);

        user = getSharedPreferences("user", Activity.MODE_PRIVATE);

        //requestQueue 선언
        if (AppHelper.requestQueue == null) {
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        //자동로그인 구현
        String memberId = user.getString("id",null);
        String dogId = user.getString("dog_id",null);

        if (memberId == null) {
            //로그인 페이지로 이동
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            },1500);
        } else {
            //회원 정보 셋팅 후 홈화면으로 이동
            loadMemberData(memberId,dogId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    //member data 셋팅
    private void loadMemberData(String memberId, String dogId) {
        String memberUrl = Common.URL + "/diary/member/" + memberId;
        StringRequest memberRequest = new StringRequest(
            Request.Method.GET,
            memberUrl,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //memberVO 셋팅팅
                    Gson gson = new Gson();
                    if (gson.fromJson(response, MemberVO.class).getId().equals("0")) {
                        //해당 아이디가 없으면 로그인 정보 지우고 로그인화면으로 이동
                        SharedPreferences.Editor editor = user.edit();
                        editor.remove("id");
                        editor.remove("dog_id");
                        editor.apply();

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        MemberVO.setInstance(gson.fromJson(response, MemberVO.class));

                        //dog_id를 background에 저장
                        SharedPreferences.Editor editor = user.edit();
                        editor.putString("id",memberId);
                        editor.apply();

                        //등록된 dog가 있을 때는 home 셋팅, 없을 때는 메인 홈화면으로
                        if (MemberVO.getInstance().getDogList().size() != 0) {
                            loadHomeData(dogId);
                        } else {
                            startMainActivity();
                        }
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                    builder.setTitle("서버 연결 실패");
                    builder.setMessage("인터넷 연결 상태를 확인해주세요.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            finish();
                        }
                    });
                    AlertDialog dialog = builder.show();
                }
            }
        );
        memberRequest.setShouldCache(false);
        AppHelper.requestQueue.add(memberRequest);
    }

    //home data 셋팅
    private void loadHomeData(String dogId) {
        String dogUrl;
        String dog_id;

        if (dogId != null) {
            //등록된 강아지번호가 있으면
            dog_id = dogId;
        } else {
            //등록된 강아지번호가 없으면 첫번째 강아지를 선택하기
            dog_id = MemberVO.getInstance().getDogList().get(0).getId();
        }

        dogUrl = Common.URL + "/diary/home/" + dog_id;
        StringRequest dogRequest = new StringRequest(
            Request.Method.GET,
            dogUrl,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //homeVO 셋팅팅
                    Gson gson = new Gson();
                    HomeVO.setInstance(gson.fromJson(response, HomeVO.class));

                    //dog_id를 background에 저장
                    SharedPreferences.Editor editor = user.edit();
                    editor.putString("dog_id",dog_id);
                    editor.commit();

                    CalendarSet.setCalendar(getApplicationContext());

                    startMainActivity();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Common.serverChecking(SplashActivity.this);
                }
            }
        );
        dogRequest.setShouldCache(false);
        AppHelper.requestQueue.add(dogRequest);
    }

    //메인 페이지로 이동
    private void startMainActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        },1500);
    }
}
package com.najin.dogdiary.member;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.najin.dogdiary.R;
import com.najin.dogdiary.calendar.CalendarSet;
import com.najin.dogdiary.home.HomeActivity;
import com.najin.dogdiary.model.HomeVO;
import com.najin.dogdiary.model.MemberVO;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    ConstraintLayout loginLoadingView;
    EditText phoneText;
    EditText authText;
    Button authButton;
    Button authCheckButton;
    TextView authCheckText;
    TextView mailText;

    String phoneNumber;
    String authNumber;
    String memberId;

    Integer authCheckCount = 0;
    CountDownTimer timer;
    long totalSecond;

    SharedPreferences user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_login);

        user = getSharedPreferences("user",MODE_PRIVATE);

//        //로그인 횟수 5회제한 풀기
//        SharedPreferences.Editor editor = user.edit();
//        editor.clear();
//        editor.apply();

        //레이아웃 구성 뷰 선언
        loginLoadingView = (ConstraintLayout) findViewById(R.id.loginLoadingView);
        phoneText = (EditText) findViewById(R.id.phone_text);
        authText = (EditText) findViewById(R.id.auth_text);
        authButton = (Button) findViewById(R.id.auth_button);
        authCheckButton = (Button) findViewById(R.id.auth_check_button);
        authCheckText = (TextView) findViewById(R.id.auth_check_text);
        mailText = (TextView) findViewById(R.id.mail_text);

        //레이아웃 초기화
        loginLoadingView.setVisibility(View.GONE);
        authText.setVisibility(View.GONE);
        authCheckButton.setVisibility(View.GONE);
        authCheckText.setVisibility(View.GONE);
        authButton.setEnabled(false);

        //핸드폰번호 입력 유효성검사
        phoneText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (authCheckCount < 5) {
                    if (s.length() == 11) {
                        authButton.setEnabled(true);
                        authButton.setBackgroundResource(R.drawable.button_member_enable);
                    } else {
                        authButton.setEnabled(false);
                        authButton.setBackgroundResource(R.drawable.button_member_disable);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        //인증번호 전송 누를 때
        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //오늘날짜 구하기
                long now = System.currentTimeMillis();
                Date nowDate = new Date(now);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String today = simpleDateFormat.format(nowDate);

                //문자 인증횟수 5회로 제한하기기
                String authDate = user.getString("authDate","");
                Integer authCount = user.getInt("authCount",5);
                Snackbar snack;

                if (authDate.equals(today)) {
                    if (authCount <= 0) {
                        //문자 전송 버튼 비활성
                        authButton.setText("인증 횟수 초과");
                        authCheckButton.setVisibility(View.GONE);
                        authText.setVisibility(View.GONE);
                        if (timer != null) {
                            timer.cancel();
                        }
                        snack = Snackbar.make(v,"인증번호 발송에 실패했습니다.\n하루에 인증 가능한 횟수를 초과했습니다.",Snackbar.LENGTH_LONG);
                    } else {
                        SharedPreferences.Editor editor = user.edit();
                        editor.putInt("authCount",authCount - 1);
                        editor.apply();
                        snack = Snackbar.make(v,"인증번호가 발송되었습니다. 최대 20초 소요\n(남은 인증횟수 " + (authCount-1) + "회)",Snackbar.LENGTH_LONG);

                        //전화번호 회원인지 확인 및 인증번호 전송
                        phoneNumber = phoneText.getText().toString();
                        sendSMS();
                    }
                } else {
                    SharedPreferences.Editor editor = user.edit();
                    editor.putString("authDate",today);
                    editor.putInt("authCount",4);
                    editor.apply();
                    snack = Snackbar.make(v,"인증번호가 발송되었습니다. 최대 20초 소요\n(남은 인증횟수 4회)",Snackbar.LENGTH_LONG);

                    //전화번호 회원인지 확인 및 인증번호 전송
                    phoneNumber = phoneText.getText().toString();
                    sendSMS();
                }

                View view = snack.getView();
                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                params.gravity = Gravity.TOP;
                view.setLayoutParams(params);
                snack.show();
            }
        });

        //인증번호 입력 유효성검사
        authText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (authCheckCount < 5) {
                    if (s.length() != 0) {
                        authCheckButton.setEnabled(true);
                        authCheckButton.setBackgroundResource(R.drawable.button_member_enable);
                    } else {
                        authCheckButton.setEnabled(false);
                        authCheckButton.setBackgroundResource(R.drawable.button_member_disable);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        //인증번호 확인 전송 누를 때
        authCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authText.getText().toString().equals(authNumber)) {
                    //인증번호 맞았을 때
                    if (totalSecond == 0) {
                        authButton.setEnabled(true);
                        authCheckText.setVisibility(View.VISIBLE);
                        authCheckText.setText("인증번호 입력시간이 초과되었습니다.");
                    } else if (memberId.equals("0")) {
                        //회원이 아니므로 회원가입 페이지로 이동
                        Intent joinIntent = new Intent(getApplicationContext(), JoinActivity.class);
                        joinIntent.putExtra("phoneNumber",phoneNumber);
                        startActivity(joinIntent);
                    } else {
                        //회원이므로 id정보를 등록하고 메인페이지로 이동
                        loginLoadingView.setVisibility(View.VISIBLE);
                        authCheckButton.setEnabled(false);
                        authButton.setEnabled(false);
                        loadMemberData(memberId);
                    }
                } else {
                    //인증번호 틀렸을 때
                    authCheckText.setVisibility(View.VISIBLE);
                    if (authCheckCount >= 5) {
                        authCheckText.setText("인증번호 입력횟수가 초과되었습니다.");
                        timer.cancel();
                        authButton.setEnabled(false);
                        authButton.setBackgroundResource(R.drawable.button_member_disable);
                        authButton.setText("입력횟수 초과");
                        authCheckButton.setEnabled(false);
                        authCheckButton.setBackgroundResource(R.drawable.button_member_disable);
                    } else {
                        authCheckCount ++;
                        authCheckText.setText("인증번호가 틀렸습니다("+authCheckCount+"/5)");
                    }
                }
            }
        });

        //메일로 로그인 눌렀을 때
        mailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mailIntent = new Intent(getApplicationContext(), MailActivity.class);
                startActivity(mailIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginLoadingView.setVisibility(View.GONE);
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

    //sms 인증
    private void sendSMS() {
        authButton.setEnabled(false);
        JSONObject phone = new JSONObject();
        try {
            phone.put("phone", phoneNumber);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
            authButton.setEnabled(true);
        }
        JsonObjectRequest smsRequest = new JsonObjectRequest(Request.Method.POST, Common.URL + "/diary/member/sms-auth",phone,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            authText.setText("");
                            authCheckText.setVisibility(View.GONE);
                            authText.setVisibility(View.VISIBLE);
                            authCheckButton.setVisibility(View.VISIBLE);
                            memberId=response.getString("id");
                            authNumber=response.getString("authNumber");
                            //5분 timer 셋팅
                            if (timer != null) {
                                timer.cancel();
                            }
                            timer = new CountDownTimer(300000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    totalSecond = millisUntilFinished / 1000;
                                    long minutes = (totalSecond % 3600) / 60;
                                    long seconds = (totalSecond % 3600) % 60;

                                    if (seconds >= 10) {
                                        authButton.setText("재전송 " + minutes +":"+ seconds);
                                    } else {
                                        authButton.setText("재전송 " + minutes +":0"+ seconds);
                                    }
                                    if (totalSecond == 285) {
                                        authButton.setEnabled(true);
                                    }
                                }

                                public void onFinish() {
                                    timer.cancel();
                                }
                            }.start();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                            authButton.setEnabled(true);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Common.serverChecking(LoginActivity.this);
                        authButton.setEnabled(true);
                    }
                });
        smsRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        smsRequest.setShouldCache(false);
        AppHelper.requestQueue.add(smsRequest);
    }

    //member data 셋팅
    private void loadMemberData(String memberId) {
        String memberUrl = Common.URL + "/diary/member/" + memberId;
        StringRequest memberRequest = new StringRequest(
                Request.Method.GET,
                memberUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //memberVO 셋팅팅
                        Gson gson = new Gson();
                        MemberVO.setInstance(gson.fromJson(response, MemberVO.class));

                        //id를 background에 저장
                        SharedPreferences.Editor editor = user.edit();
                        editor.putString("id",memberId);
                        editor.commit();

                        //등록된 dog가 있을 때는 home 셋팅, 없을 때는 메인 홈화면으로
                        if (MemberVO.getInstance().getDogList().size() != 0) {
                            loadHomeData();
                        } else {
                            startMainActivity();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                        loginLoadingView.setVisibility(View.GONE);
                        authCheckButton.setEnabled(true);
                    }
                }
        );
        memberRequest.setShouldCache(false);
        AppHelper.requestQueue.add(memberRequest);
    }

    //home data 셋팅
    private void loadHomeData() {
        String dogId = MemberVO.getInstance().getDogList().get(0).getId();
        String dogUrl = Common.URL + "/diary/home/" + dogId;
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
                        editor.putString("dog_id",dogId);
                        editor.commit();

                        CalendarSet.setCalendar(getApplicationContext());

                        startMainActivity();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                        loginLoadingView.setVisibility(View.GONE);
                        authCheckButton.setEnabled(true);
                    }
                }
        );
        dogRequest.setShouldCache(false);
        AppHelper.requestQueue.add(dogRequest);
    }

    //메인 페이지로 이동
    private void startMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), HomeActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }
}
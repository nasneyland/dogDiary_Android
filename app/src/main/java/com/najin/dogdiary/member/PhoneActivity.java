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

public class PhoneActivity extends AppCompatActivity {

    ConstraintLayout phoneLoadingView;
    EditText phoneText;
    EditText authText;
    Button authButton;
    Button authCheckButton;
    TextView authCheckText;
    TextView phoneCheckText;

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
        setContentView(R.layout.activity_member_phone);

        user = getSharedPreferences("user",MODE_PRIVATE);

        Intent intent = getIntent();
        memberId = intent.getExtras().getString("memberId");

        //???????????? ?????? ??? ??????
        phoneLoadingView = (ConstraintLayout) findViewById(R.id.member_phoneLoadingView);
        phoneText = (EditText) findViewById(R.id.member_phone_text);
        authText = (EditText) findViewById(R.id.member_auth_text);
        authButton = (Button) findViewById(R.id.member_auth_button);
        phoneCheckText = (TextView) findViewById(R.id.member_phone_check_text);
        authCheckButton = (Button) findViewById(R.id.member_auth_check_button);
        authCheckText = (TextView) findViewById(R.id.member_auth_check_text);

        //???????????? ?????????
        phoneLoadingView.setVisibility(View.GONE);
        authText.setVisibility(View.GONE);
        authCheckButton.setVisibility(View.GONE);
        authCheckText.setVisibility(View.GONE);
        phoneCheckText.setVisibility(View.GONE);
        authButton.setEnabled(false);

        //??????????????? ?????? ???????????????
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

        //???????????? ?????? ?????? ???
        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //???????????? ?????????
                long now = System.currentTimeMillis();
                Date nowDate = new Date(now);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String today = simpleDateFormat.format(nowDate);

                //?????? ???????????? 5?????? ???????????????
                String authDate = user.getString("authDate","");
                Integer authCount = user.getInt("authCount",5);
                Snackbar snack;

                if (authDate.equals(today)) {
                    if (authCount <= 0) {
                        //?????? ?????? ?????? ?????????
                        authButton.setText("?????? ?????? ??????");
                        authCheckButton.setVisibility(View.GONE);
                        authText.setVisibility(View.GONE);
                        timer.cancel();
                        snack = Snackbar.make(v,"???????????? ????????? ??????????????????.\n????????? ?????? ????????? ????????? ??????????????????.",Snackbar.LENGTH_LONG);
                    } else {
                        SharedPreferences.Editor editor = user.edit();
                        editor.putInt("authCount",authCount - 1);
                        editor.apply();
                        snack = Snackbar.make(v,"??????????????? ?????????????????????. ?????? 20??? ??????\n(?????? ???????????? " + (authCount-1) + "???)",Snackbar.LENGTH_LONG);

                        //???????????? ???????????? ?????? ??? ???????????? ??????
                        phoneNumber = phoneText.getText().toString();
                        sendSMS();
                    }
                } else {
                    SharedPreferences.Editor editor = user.edit();
                    editor.putString("authDate",today);
                    editor.putInt("authCount",4);
                    editor.apply();
                    snack = Snackbar.make(v,"??????????????? ?????????????????????. ?????? 20??? ??????\n(?????? ???????????? 4???)",Snackbar.LENGTH_LONG);

                    //???????????? ???????????? ?????? ??? ???????????? ??????
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

        //???????????? ?????? ???????????????
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

        //???????????? ?????? ?????? ?????? ???
        authCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authText.getText().toString().equals(authNumber)) {
                    //???????????? ????????? ???
                    if (totalSecond == 0) {
                        authButton.setEnabled(true);
                        authCheckText.setVisibility(View.VISIBLE);
                        authCheckText.setText("???????????? ??????????????? ?????????????????????.");
                    } else {
                        //??????????????? ??????
                        phoneLoadingView.setVisibility(View.VISIBLE);
                        authCheckButton.setEnabled(false);
                        authButton.setEnabled(false);
                        updatePhone();
                    }
                } else {
                    //???????????? ????????? ???
                    authCheckText.setVisibility(View.VISIBLE);
                    if (authCheckCount >= 5) {
                        authCheckText.setText("???????????? ??????????????? ?????????????????????.");
                        timer.cancel();
                        authButton.setEnabled(false);
                        authButton.setBackgroundResource(R.drawable.button_member_disable);
                        authButton.setText("???????????? ??????");
                        authCheckButton.setEnabled(false);
                        authCheckButton.setBackgroundResource(R.drawable.button_member_disable);
                    } else {
                        authCheckCount ++;
                        authCheckText.setText("??????????????? ???????????????("+authCheckCount+"/5)");
                    }
                }
            }
        });
    }

    //editText??? ?????? ??? ???????????? ????????? ???????????? ??????
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

    //sms ??????
    private void sendSMS() {
        authButton.setEnabled(false);
        JSONObject phone = new JSONObject();
        try {
            phone.put("phone", phoneNumber);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"?????? ?????? ?????? : ????????? ?????? ??????????????????",Toast.LENGTH_SHORT).show();
            authButton.setEnabled(true);
        }
        JsonObjectRequest smsRequest = new JsonObjectRequest(Request.Method.POST, Common.URL + "/diary/member/sms-re-auth",phone,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getInt("id") == 1) {
                            phoneCheckText.setVisibility(View.VISIBLE);
                        } else {
                            phoneCheckText.setVisibility(View.GONE);
                            authText.setText("");
                            authCheckText.setVisibility(View.GONE);
                            authText.setVisibility(View.VISIBLE);
                            authCheckButton.setVisibility(View.VISIBLE);
                            authNumber = response.getString("authNumber");
                            //5??? timer ??????
                            if (timer != null) {
                                timer.cancel();
                            }
                            timer = new CountDownTimer(300000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    totalSecond = millisUntilFinished / 1000;
                                    long minutes = (totalSecond % 3600) / 60;
                                    long seconds = (totalSecond % 3600) % 60;

                                    if (seconds >= 10) {
                                        authButton.setText("????????? " + minutes + ":" + seconds);
                                    } else {
                                        authButton.setText("????????? " + minutes + ":0" + seconds);
                                    }
                                    if (totalSecond == 285) {
                                        authButton.setEnabled(true);
                                    }
                                }

                                public void onFinish() {
                                    timer.cancel();
                                }
                            }.start();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"?????? ?????? ?????? : ????????? ?????? ??????????????????",Toast.LENGTH_SHORT).show();
                        authButton.setEnabled(true);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),"?????? ?????? ?????? : ????????? ?????? ??????????????????",Toast.LENGTH_SHORT).show();
                    authButton.setEnabled(true);
                }
            });
        smsRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        smsRequest.setShouldCache(false);
        AppHelper.requestQueue.add(smsRequest);
    }

    //????????? ??????
    private void updatePhone() {
        JSONObject phone = new JSONObject();
        try {
            phone.put("phone", phoneNumber);
            phone.put("type", "phone");
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"?????? ?????? ?????? : ????????? ?????? ??????????????????",Toast.LENGTH_SHORT).show();
            phoneLoadingView.setVisibility(View.GONE);
            authCheckButton.setEnabled(true);
        }
        JsonObjectRequest smsRequest = new JsonObjectRequest(Request.Method.PUT, Common.URL + "/diary/member/"+memberId, phone,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadMemberData();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"?????? ?????? ?????? : ????????? ?????? ??????????????????",Toast.LENGTH_SHORT).show();
                        phoneLoadingView.setVisibility(View.GONE);
                        authCheckButton.setEnabled(true);
                    }
                });
        smsRequest.setShouldCache(false);
        AppHelper.requestQueue.add(smsRequest);
    }

    //member data ??????
    private void loadMemberData() {
        String memberUrl = Common.URL + "/diary/member/" + memberId;
        StringRequest memberRequest = new StringRequest(
                Request.Method.GET,
                memberUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //memberVO ?????????
                        Gson gson = new Gson();
                        MemberVO.setInstance(gson.fromJson(response, MemberVO.class));

                        //id??? background??? ??????
                        SharedPreferences.Editor editor = user.edit();
                        editor.putString("id",memberId);
                        editor.commit();

                        //????????? dog??? ?????? ?????? home ??????, ?????? ?????? ?????? ???????????????
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
                        Toast.makeText(getApplicationContext(),"?????? ?????? ?????? : ????????? ?????? ??????????????????",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
        );
        memberRequest.setShouldCache(false);
        AppHelper.requestQueue.add(memberRequest);
    }

    //home data ??????
    private void loadHomeData() {
        String dogId = MemberVO.getInstance().getDogList().get(0).getId();
        String dogUrl = Common.URL + "/diary/home/" + dogId;
        StringRequest dogRequest = new StringRequest(
                Request.Method.GET,
                dogUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //homeVO ?????????
                        Gson gson = new Gson();
                        HomeVO.setInstance(gson.fromJson(response, HomeVO.class));

                        //dog_id??? background??? ??????
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
                        Toast.makeText(getApplicationContext(),"?????? ?????? ?????? : ????????? ?????? ??????????????????",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
        );
        dogRequest.setShouldCache(false);
        AppHelper.requestQueue.add(dogRequest);
    }

    //?????? ???????????? ??????
    private void startMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), HomeActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }
}
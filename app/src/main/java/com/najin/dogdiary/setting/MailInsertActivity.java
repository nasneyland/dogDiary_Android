package com.najin.dogdiary.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.najin.dogdiary.R;
import com.najin.dogdiary.model.MemberVO;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;

import org.json.JSONException;
import org.json.JSONObject;

public class MailInsertActivity extends AppCompatActivity {

    TextView mailStatusText;
    EditText mailText;
    EditText authText;
    Button authButton;
    Button authCheckButton;
    TextView authCheckText;
    TextView mailCheckText;
    ConstraintLayout mailLoadingView;

    String mail;
    String authNumber;

    Integer authCheckCount = 0;
    CountDownTimer timer;
    long totalSecond;

    SharedPreferences user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_mail);

        user = getSharedPreferences("user",MODE_PRIVATE);

        //레이아웃 구성 뷰 선언
        mailText = (EditText) findViewById(R.id.setting_mail_text);
        authText = (EditText) findViewById(R.id.setting_auth_text);
        authButton = (Button) findViewById(R.id.setting_auth_button);
        authCheckButton = (Button) findViewById(R.id.setting_auth_check_button);
        authCheckText = (TextView) findViewById(R.id.setting_auth_check_text);
        mailCheckText = (TextView) findViewById(R.id.setting_mail_check_text);
        mailLoadingView = (ConstraintLayout) findViewById(R.id.setting_mailLoadingView);

        //레이아웃 초기화
        authText.setVisibility(View.GONE);
        authCheckButton.setVisibility(View.GONE);
        authCheckText.setVisibility(View.GONE);
        mailCheckText.setVisibility(View.GONE);
        authButton.setEnabled(false);
        mailLoadingView.setVisibility(View.GONE);


        mailStatusText = (TextView) findViewById(R.id.mail_status_text);
        if (MemberVO.getInstance().getMail().equals("")) {
            mailStatusText.setText("이메일 등록");
        } else {
            mailStatusText.setText("이메일 변경");
        }

        //메일 입력 유효성검사
        mailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mailCheckText.setVisibility(View.GONE);
                if (authCheckCount < 5) {
                    if (s.toString().contains("@") && s.toString().contains(".")) {
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
                mail = mailText.getText().toString();
                authButton.setText("전송중");
                authButton.setEnabled(false);
                sendMail();
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

        //인증번호 전송 누를 때
        authCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authText.getText().toString().equals(authNumber)) {
                    //인증번호 맞았을 때
                    if (totalSecond == 0) {
                        authButton.setEnabled(true);
                        authCheckText.setVisibility(View.VISIBLE);
                        authCheckText.setText("인증번호 입력시간이 초과되었습니다.");
                    } else {
                        authCheckButton.setEnabled(false);
                        mailLoadingView.setVisibility(View.VISIBLE);
                        insertMail();
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

    //mail 인증
    private void sendMail() {
        JSONObject mailObject = new JSONObject();
        try {
            mailObject.put("mail", mail);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
            authButton.setText("인증번호 전송");
            authButton.setEnabled(true);
        }
        JsonObjectRequest mailRequest = new JsonObjectRequest(Request.Method.POST, Common.URL + "/diary/member/mail-auth-insert",mailObject,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getInt("id") == 0) {
                            //이미 등록된 이메일인 경우
                            mailCheckText.setVisibility(View.VISIBLE);
                            authButton.setText("인증번호 전송");
                            authButton.setEnabled(true);
                        } else {
                            authButton.setEnabled(false);
                            authText.setText("");
                            authCheckText.setVisibility(View.GONE);
                            authText.setVisibility(View.VISIBLE);
                            authCheckButton.setVisibility(View.VISIBLE);
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
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                        authButton.setText("인증번호 전송");
                        authButton.setEnabled(true);
                    }

                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                    authButton.setText("인증번호 전송");
                    authButton.setEnabled(true);
                }
            });
        mailRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mailRequest.setShouldCache(false);
        AppHelper.requestQueue.add(mailRequest);
    }

    //mail 입력하기
    private void insertMail() {
        JSONObject mailObject = new JSONObject();
        try {
            mailObject.put("mail", mail);
            mailObject.put("type", "mail");
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
            authCheckButton.setEnabled(true);
            mailLoadingView.setVisibility(View.GONE);
        }
        JsonObjectRequest mailRequest = new JsonObjectRequest(Request.Method.PUT, Common.URL + "/diary/member/"+MemberVO.getInstance().getId(), mailObject,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    MemberVO.getInstance().setMail(mail);
                    backActivity();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                    authCheckButton.setEnabled(true);
                    mailLoadingView.setVisibility(View.GONE);
                }
            });
        mailRequest.setShouldCache(false);
        AppHelper.requestQueue.add(mailRequest);
    }

    //셋팅 페이지로 이동
    private void backActivity() {
        Intent settingIntent = new Intent(getApplicationContext(), SettingActivity.class);
        settingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(settingIntent);
    }
}
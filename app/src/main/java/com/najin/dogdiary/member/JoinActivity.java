package com.najin.dogdiary.member;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.najin.dogdiary.R;
import com.najin.dogdiary.home.HomeActivity;
import com.najin.dogdiary.model.DogVO;
import com.najin.dogdiary.model.MemberVO;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JoinActivity extends AppCompatActivity {

    ConstraintLayout joinLoadingView;
    CheckBox check1;
    CheckBox check2;
    Button button;

    String phoneNumber;
    String mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_join);

        //레이아웃 구성 뷰 선언
        joinLoadingView = (ConstraintLayout) findViewById(R.id.joinLoadingView);
        joinLoadingView.setVisibility(View.GONE);
        check1 = (CheckBox) findViewById(R.id.checkBox1);
        check2 = (CheckBox) findViewById(R.id.checkBox2);
        button = (Button) findViewById(R.id.join_button);

        Intent intent = getIntent();
        phoneNumber = intent.getExtras().getString("phoneNumber",null);
        mail = intent.getExtras().getString("mail",null);

        check1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check1.isChecked() && check2.isChecked()) {
                    button.setEnabled(true);
                    button.setBackgroundResource(R.drawable.button_member_enable);
                } else {
                    button.setEnabled(false);
                    button.setBackgroundResource(R.drawable.button_member_disable);
                }
            }
        });

        check2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check1.isChecked() && check2.isChecked()) {
                    button.setEnabled(true);
                    button.setBackgroundResource(R.drawable.button_member_enable);
                } else {
                    button.setEnabled(false);
                    button.setBackgroundResource(R.drawable.button_member_disable);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setEnabled(false);
                joinLoadingView.setVisibility(View.VISIBLE);
                //회원가입하기
                loadMemberData();
            }
        });
    }

    //member data 셋팅
    private void loadMemberData() {
        JSONObject param = new JSONObject();
        try {
            if (phoneNumber != null) {
                param.put("phone", phoneNumber);
            } else {
                param.put("mail", mail);
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
            button.setEnabled(true);
            joinLoadingView.setVisibility(View.GONE);
        }
        JsonObjectRequest joinRequest = new JsonObjectRequest(Request.Method.POST, Common.URL + "/diary/member",param,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String id = response.getString("id");

                        //id 저장하기
                        SharedPreferences user = getSharedPreferences("user",MODE_PRIVATE);
                        SharedPreferences.Editor editor = user.edit();
                        editor.putString("id",id);
                        editor.commit();

                        //member instance에 저장
                        MemberVO.getInstance().setId(id);
                        MemberVO.getInstance().setPhone(response.getString("phone"));
                        MemberVO.getInstance().setMail(response.getString("mail"));
                        MemberVO.getInstance().setGrade(response.getInt("grade"));
                        MemberVO.getInstance().setDogList(new ArrayList<DogVO>());

                        //메인 페이지로 이동
                        Intent mainIntent = new Intent(getApplicationContext(), HomeActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                        button.setEnabled(true);
                        joinLoadingView.setVisibility(View.GONE);
                    }

                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                    button.setEnabled(true);
                    joinLoadingView.setVisibility(View.GONE);
                }
            });
        joinRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        joinRequest.setShouldCache(false);
        AppHelper.requestQueue.add(joinRequest);
    }
}




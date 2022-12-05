package com.najin.dogdiary.member;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;

public class AdminActivity extends AppCompatActivity {

    EditText memberText;
    Button memberButton;

    SharedPreferences user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_admin);

        memberText = (EditText) findViewById(R.id.member_number);
        memberButton = (Button) findViewById(R.id.member_button);

        user = getSharedPreferences("user", Activity.MODE_PRIVATE);

        //requestQueue 선언
        if (AppHelper.requestQueue == null) {
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        //저장된 정보 지우기
        SharedPreferences.Editor editor = user.edit();
        editor.remove("id");
        editor.remove("dog_id");
        editor.apply();

        //인증번호 전송 누를 때
        memberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dog = "401";
                if (memberText.getText().toString().length() != 0) {
                    dog = memberText.getText().toString();
                }
                loadHomeData(dog);
            }
        });
    }

    //home data 셋팅
    private void loadHomeData(String dog) {
        String dogUrl = Common.URL + "/diary/home/" + dog;
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
                        editor.putString("dog_id",dog);
                        editor.commit();

                        CalendarSet.setCalendar(getApplicationContext());

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
        Intent mainIntent = new Intent(getApplicationContext(), HomeActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }
}
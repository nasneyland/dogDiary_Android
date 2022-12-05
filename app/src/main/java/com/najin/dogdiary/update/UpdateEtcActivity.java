package com.najin.dogdiary.update;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.najin.dogdiary.R;
import com.najin.dogdiary.calendar.CalendarActivity;
import com.najin.dogdiary.information.EtcColorAdapter;
import com.najin.dogdiary.information.EtcColorView;
import com.najin.dogdiary.model.CalendarVO;
import com.najin.dogdiary.model.EtcVO;
import com.najin.dogdiary.model.HomeVO;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class UpdateEtcActivity extends AppCompatActivity {

    ConstraintLayout etcUpdateLoadingView;

    EditText etcTitleText;
    EditText etcContentText;
    TextView etc_content_count;
    Button insertButton;
    GridView colorSet;

    TextView etcUpdateDateText;
    TextView checkTitleText;
    TextView checkContentText;
    TextView checkColorText;

    String informationDateString;
    int etcId;
    String etcDate;
    String etcTitle;
    String etcContent;

    String[] colors = new String[]{"#e9a799", "#db8438", "#c76026", "#963f2e", "#edc233",
            "#97d5e0", "#89bbb8", "#479a83", "#5c7148", "#4a5336",
            "#489ad8", "#bfa7f6", "#a7a3f8", "#8357ac", "#3c5066",
            "#bec3c7", "#999999", "#818b8d", "#798da5", "#495057"};
    String selectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_update_etc);

        //기타 정보 셋팅
        Intent intent = getIntent();
        informationDateString = intent.getStringExtra("informationDateString");
        etcId = intent.getIntExtra("etcId", 0);
        etcDate = intent.getStringExtra("etcDate");
        selectedColor = intent.getStringExtra("etcColor");
        etcTitle = intent.getStringExtra("etcTitle");
        etcContent = intent.getStringExtra("etcContent");

        etcUpdateLoadingView = (ConstraintLayout) findViewById(R.id.etcUpdateLoadingView);
        etcUpdateLoadingView.setVisibility(View.GONE);
        checkTitleText = (TextView) findViewById(R.id.checkTitleUpdateText);
        checkContentText = (TextView) findViewById(R.id.checkContentUpdateText);
        checkTitleText.setVisibility(View.GONE);
        checkContentText.setVisibility(View.GONE);

        //status bar 색상지정
        getWindow().setStatusBarColor(Color.parseColor("#707578"));

        //지출 날짜 셋팅
        etcUpdateDateText = (TextView) findViewById(R.id.etcUpdateDateText);
        etcUpdateDateText.setText(informationDateString);
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear += 1;
                etcUpdateDateText.setText(year + "년 " + monthOfYear + "월 " + dayOfMonth + "일");
                String month;
                String day;
                if (monthOfYear < 10) {
                    month = "0" + monthOfYear;
                } else {
                    month = "" + monthOfYear;
                }
                if (dayOfMonth < 10) {
                    day = "0" + dayOfMonth;
                } else {
                    day = "" + dayOfMonth;
                }
                etcDate = year + "-" + month + "-" + day;
            }
        };
        etcUpdateDateText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar c = new GregorianCalendar();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(UpdateEtcActivity.this, android.R.style.Theme_Holo_Light_Dialog, listener,
                        Integer.parseInt(etcDate.substring(0, 4)),
                        Integer.parseInt(etcDate.substring(5, 7)) - 1,
                        Integer.parseInt(etcDate.substring(8, 10)));

                Calendar minDate = Calendar.getInstance();
                minDate.set(2020, 0, 1);
                dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
                dialog.setTitle("일정 날짜");

                dialog.show();
            }
        });

        //색상 팔레트 셋팅
        for (int i = 0; i < 20; i++) {
            String color = colors[i];
            if (selectedColor.equals(color)) {
                EtcColorView.selected = i;
                break;
            }
        }
        colorSet = (GridView) findViewById(R.id.colorSetUpdate);
        EtcColorAdapter adapter = new EtcColorAdapter(this);
        colorSet.setAdapter(adapter);
        colorSet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedColor = colors[position];
                EtcColorView.selected = position;
                colorSet.invalidateViews();
            }
        });

        //제목 입력 유효성검사
        etcTitleText = (EditText) findViewById(R.id.etcUpdateTitle);
        etcTitleText.setText(etcTitle);
        etcTitleText.addTextChangedListener(new TextWatcher() {
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

        //상세내용 유효성검사
        etcContentText = (EditText) findViewById(R.id.etcUpdateContent);
        etcContentText.setText(etcContent);
        etc_content_count = (TextView) findViewById(R.id.etcUpdateContentLabel);
        etc_content_count.setText(etcContent.length() + "/100");
        etcContentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkContentText.setVisibility(View.GONE);
                etc_content_count.setText(s.length() + "/100");
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        //등록 버튼 클릭 이벤트
        insertButton = (Button) findViewById(R.id.etcUpdateButton);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etcTitleText.getText().toString().trim().length() == 0) {
                    checkTitleText.setVisibility(View.VISIBLE);
                } else if (etcContentText.getText().toString().trim().length() == 0) {
                    checkContentText.setVisibility(View.VISIBLE);
                } else {
                    etcUpdateLoadingView.setVisibility(View.VISIBLE);
                    insertButton.setEnabled(false);
                    etcUpdate();
                }
            }
        });
    }

    private void etcUpdate() {
        JSONObject etcObject = new JSONObject();
        try {
            etcObject.put("date", etcDate);
            etcObject.put("color", selectedColor);
            etcObject.put("title", etcTitleText.getText().toString());
            etcObject.put("content", etcContentText.getText().toString());
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
            etcUpdateLoadingView.setVisibility(View.GONE);
            insertButton.setEnabled(true);
        }
        JsonObjectRequest etcRequest = new JsonObjectRequest(Request.Method.PUT, Common.URL + "/diary/etc/" + etcId, etcObject,
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
                        etcUpdateLoadingView.setVisibility(View.GONE);
                        insertButton.setEnabled(true);
                    }
                });
        etcRequest.setShouldCache(false);
        AppHelper.requestQueue.add(etcRequest);
    }

    private void setEtc() {
        String etcUrl = Common.URL + "/diary/etc/" + HomeVO.getInstance().getDog().getId();
        StringRequest dogRequest = new StringRequest(
                Request.Method.GET,
                etcUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        EtcVO[] list = gson.fromJson(response, EtcVO[].class);
                        List<EtcVO> alist = Arrays.asList(list);
                        ArrayList<EtcVO> arrayList = new ArrayList<EtcVO>();
                        arrayList.addAll(alist);
                        CalendarVO.getInstance().setEtcList(arrayList);

                        Intent mainIntent = new Intent(getApplicationContext(), CalendarActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                        overridePendingTransition(0, 0);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "서버 연결 실패 : 잠시후 다시 이용해주세요", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        dogRequest.setShouldCache(false);
        AppHelper.requestQueue.add(dogRequest);
    }
}
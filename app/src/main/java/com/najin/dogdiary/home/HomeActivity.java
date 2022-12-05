package com.najin.dogdiary.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.najin.dogdiary.R;
import com.najin.dogdiary.calendar.CalendarActivity;
import com.najin.dogdiary.calendar.CalendarMonthSet;
import com.najin.dogdiary.information.EtcActivity;
import com.najin.dogdiary.information.HeartActivity;
import com.najin.dogdiary.information.MoneyActivity;
import com.najin.dogdiary.information.WalkActivity;
import com.najin.dogdiary.information.WashActivity;
import com.najin.dogdiary.information.WeightActivity;
import com.najin.dogdiary.member.DogInsertActivity;
import com.najin.dogdiary.model.HomeVO;
import com.najin.dogdiary.model.MoneyVO;
import com.najin.dogdiary.model.WalkVO;
import com.najin.dogdiary.model.WeightVO;
import com.najin.dogdiary.setting.SettingActivity;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    //오늘날짜
    TextView todayText;
    //강아지정보
    ImageView birthImage;
    ImageView profileImage;
    TextView dogName;
    TextView dogAge;
    TextView dogHumanAge;
    //뷰 이동 버튼
    ImageButton calendarButton;
    ImageButton settingButton;
    //강아지 등록, 정보입력 버튼
    Button insertDogButton;
    LinearLayout insertInfoButton;
    ImageButton walkButton;
    ImageButton washButton;
    ImageButton weightButton;
    ImageButton heartButton;
    ImageButton moneyButton;
    ImageButton etcButton;
    //관리 날짜 정보
    TextView lastHeartDay;
    TextView lastWashDay;
    TextView lastWeight;
    //산책 달력
    GridView monthView;
    TextView walkDistance;
    TextView walkMinutes;
    //몸무게 그래프
    LineChart weightChart;
    TextView weightMinText;
    TextView weightMaxText;
    //지출 프로그레스 뷰
    TextView totalMoneyText;
    ProgressBar moneyProgress1;
    ProgressBar moneyProgress2;
    ProgressBar moneyProgress3;
    ProgressBar moneyProgress4;
    ProgressBar moneyProgress5;
    TextView moneyPercent1;
    TextView moneyPercent2;
    TextView moneyPercent3;
    TextView moneyPercent4;
    TextView moneyPercent5;

    ConstraintLayout calendarLoadingView;

    SharedPreferences user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);

        //admob 초기화
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //레이아웃 구성 뷰 선언
        todayText = (TextView) findViewById(R.id.todayText);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        birthImage = (ImageView) findViewById(R.id.birthImage);
        calendarButton = (ImageButton) findViewById(R.id.calendarButton);
        settingButton = (ImageButton) findViewById(R.id.settingButton);
        dogName = (TextView) findViewById(R.id.dogName);
        dogAge = (TextView) findViewById(R.id.dogAge);
        dogHumanAge = (TextView) findViewById(R.id.dogHumanAge);
        insertDogButton = (Button) findViewById(R.id.insertDogButton);
        insertInfoButton = (LinearLayout) findViewById(R.id.insertInfoButton);
        walkButton = (ImageButton) findViewById(R.id.walkButton);
        washButton = (ImageButton) findViewById(R.id.washButton);
        weightButton = (ImageButton) findViewById(R.id.weightButton);
        heartButton = (ImageButton) findViewById(R.id.heartButton);
        moneyButton = (ImageButton) findViewById(R.id.moneyButton);
        etcButton = (ImageButton) findViewById(R.id.etcButton);
        lastHeartDay = (TextView) findViewById(R.id.lastHeartDay);
        lastWashDay = (TextView) findViewById(R.id.lastWashDay);
        lastWeight = (TextView) findViewById(R.id.lastWeight);
        //산책 통계
        monthView = (GridView) findViewById(R.id.monthView);
        walkDistance = (TextView) findViewById(R.id.walkDistance);
        walkMinutes = (TextView) findViewById(R.id.walkMinutes);
        //몸무게 그래프
        weightChart = (LineChart) findViewById(R.id.weightChart);
        weightMinText = (TextView) findViewById(R.id.weightMinText);
        weightMaxText = (TextView) findViewById(R.id.weightMaxText);
        //지출 프로그레스 뷰
        totalMoneyText = (TextView) findViewById(R.id.totalMoneyText);
        moneyProgress1 = (ProgressBar) findViewById(R.id.moneyProgress1);
        moneyProgress2 = (ProgressBar) findViewById(R.id.moneyProgress2);
        moneyProgress3 = (ProgressBar) findViewById(R.id.moneyProgress3);
        moneyProgress4 = (ProgressBar) findViewById(R.id.moneyProgress4);
        moneyProgress5 = (ProgressBar) findViewById(R.id.moneyProgress5);
        moneyPercent1 = (TextView) findViewById(R.id.moneyPercent1);
        moneyPercent2 = (TextView) findViewById(R.id.moneyPercent2);
        moneyPercent3 = (TextView) findViewById(R.id.moneyPercent3);
        moneyPercent4 = (TextView) findViewById(R.id.moneyPercent4);
        moneyPercent5 = (TextView) findViewById(R.id.moneyPercent5);

        calendarLoadingView = (ConstraintLayout) findViewById(R.id.calendarLoadingView);

        //달력 클릭 이벤트
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarMonthSet.getInstance().setYear(0);
                CalendarMonthSet.getInstance().setMonth(0);
                calendarLoadingView.setVisibility(View.VISIBLE);
                Intent calendarActivity = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(calendarActivity);
                overridePendingTransition(0, 0);
            }
        });

        //셋팅 클릭 이벤트
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingActivity = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(settingActivity);
                overridePendingTransition(0, 0);
            }
        });

        //강아지 등록하기 클릭 이벤트
        insertDogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dogInsertIntent = new Intent(getApplicationContext(), DogInsertActivity.class);
                startActivity(dogInsertIntent);
            }
        });

        //산책하기 클릭 이벤트
        walkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent walkActivity = new Intent(getApplicationContext(), WalkActivity.class);
                startActivity(walkActivity);
            }
        });

        //목욕 클릭 이벤트
        washButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent washActivity = new Intent(getApplicationContext(), WashActivity.class);
                washActivity.putExtra("informationDate","today");
                startActivity(washActivity);
            }
        });

        //몸무게 클릭 이벤트
        weightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent weightActivity = new Intent(getApplicationContext(), WeightActivity.class);
                weightActivity.putExtra("informationDate","today");
                startActivity(weightActivity);
            }
        });

        //심장사상충 클릭 이벤트
        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent heartActivity = new Intent(getApplicationContext(), HeartActivity.class);
                heartActivity.putExtra("informationDate","today");
                startActivity(heartActivity);
            }
        });

        //지출 클릭 이벤트
        moneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moneyActivity = new Intent(getApplicationContext(), MoneyActivity.class);
                moneyActivity.putExtra("informationDate","today");
                startActivity(moneyActivity);
            }
        });

        //기타내역 클릭 이벤트
        etcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent etcActivity = new Intent(getApplicationContext(), EtcActivity.class);
                etcActivity.putExtra("informationDate","today");
                startActivity(etcActivity);
            }
        });

        //몸무게 모드 변경
        TextView weightTypeText = (TextView) findViewById(R.id.weightTypeText);
        if (HomeVO.getInstance().getDog().getId() != null) {
            weightTypeText.setEnabled(true);
        } else {
            weightTypeText.setEnabled(false);
        }

        weightTypeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] oItems = {"최근 10회 변화량", "이번 달 변화량", "이번 연도 변화량", "전체 변화량"};
                AlertDialog.Builder oDialog = new AlertDialog.Builder(HomeActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                oDialog.setItems(oItems, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (HomeVO.getInstance().getLastWeightDay() != null) {
                            if (which == 0) {
                                setWeightChart(HomeVO.getHomeVO().getWeightChart());
                            } else {
                                setHomeWeight(which);
                            }
                        }
                    }
                }).setCancelable(true).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        calendarLoadingView.setVisibility(View.GONE);

        //home view 기본 셋팅
        setHome();
    }

    private void setHome() {
        //레이아웃 구성 뷰 셋팅
        todayText.bringToFront();
        profileImage.bringToFront();
        profileImage.setBackground(new ShapeDrawable(new OvalShape()));
        profileImage.setClipToOutline(true);

        //강아지 등록버튼
        insertDogButton.setVisibility(View.VISIBLE);
        insertInfoButton.setVisibility(View.GONE);

        //오늘날짜 셋팅
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 M월 d일");
        String today = dateFormat.format(date);
        todayText.setText(today);
        birthImage.setVisibility(View.GONE);

        //달력 셋팅
        HomeCalendarAdapter adapter = new HomeCalendarAdapter(this);
        monthView.setAdapter(adapter);

        //몸무게그래프 셋팅
        weightChart.setNoDataText("몸무게 데이터가 없습니다.");
        weightChart.setNoDataTextColor(Color.parseColor("gray"));

        //등록된 강아지가 있으면 홈 셋팅
        user = getSharedPreferences("user", MODE_PRIVATE);
        String dog_id = user.getString("dog_id", null);
        if (dog_id != null) {
            setHomeDate(HomeVO.getInstance());
        }
    }

    private void setHomeDate(HomeVO home) {
        //버튼 셋팅
        insertDogButton.setVisibility(View.GONE);
        insertInfoButton.setVisibility(View.VISIBLE);

        //강아지 프로필 이미지 셋팅
        if (!home.getDog().getImage().equals("")) {
            String url = Common.URL + "/media/" + home.getDog().getImage();
            Glide.with(this)
                    .load(url)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.img_profile)
                    .error(R.drawable.img_profile)
                    .fallback(R.drawable.img_profile)
                    .into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.img_profile);
        }

        //강아지 이름 & 성별 셋팅
        Integer strLength = home.getDog().getName().length();
        if (home.getDog().getGender() == 1) {
            //남자
            String str = home.getDog().getName() + " ♂";
            SpannableStringBuilder ssb = new SpannableStringBuilder(str);
            ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#387bba")), strLength, strLength + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            dogName.setText(ssb);
        } else {
            //여자
            String str = home.getDog().getName() + " ♀";
            SpannableStringBuilder ssb = new SpannableStringBuilder(str);
            ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#e8879b")), strLength, strLength + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            dogName.setText(ssb);
        }

        //강아지 나이 셋팅
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN);
        Date currentTime = Calendar.getInstance().getTime();
        String today = dateFormat.format(currentTime);
        try {
            Date birthDate = dateFormat.parse(home.getDog().getBirth());
            Date todayDate = dateFormat.parse(today);
            long diffTime = todayDate.getTime() - birthDate.getTime();
            long diffDaysLong = diffTime / (24 * 60 * 60 * 1000);
            Integer diffDays = (int) diffDaysLong;
            Integer year = diffDays / 365;
            Integer month = (diffDays % 365) / 30;
            Integer day = (diffDays % 365) % 12;
            //태어난지
            if (year < 0 || month < 0 || day < 0) {
                dogAge.setText("-");
            } else if (year == 0) {
                dogAge.setText(month + "개월");
            } else if (month == 0) {
                dogAge.setText(year + "년");
            } else {
                dogAge.setText(year + "년 " + month + "개월");
            }
            //사람나이
            Integer age = 0;
            if (year == 0) {
                //1살 전까지
                switch (month) {
                    case 1:
                        age = 1;
                        break;
                    case 2:
                        age = 2;
                        break;
                    case 3:
                        age = 5;
                        break;
                    case 4:
                        age = 6;
                        break;
                    case 5:
                        age = 8;
                        break;
                    case 6:
                        age = 10;
                        break;
                    case 7:
                        age = 11;
                        break;
                    case 8:
                        age = 12;
                        break;
                    case 9:
                        age = 13;
                        break;
                    case 10:
                        age = 14;
                        break;
                    case 11:
                        age = 15;
                        break;
                    default:
                        age = 0;
                        break;
                }
            } else if (year == 1) {
                age = 16;
            } else if (year == 2) {
                age = 24;
            } else {
                if (home.getLastWeight() == null || home.getLastWeight() < 10) {
                    age = 24 + (year - 2) * 5;
                } else if (home.getLastWeight() < 20) {
                    age = 24 + (year - 2) * 6;
                } else {
                    age = 24 + (year - 2) * 7;
                }
            }

            String group = "";
            if (year == 0) {
                if (month <= 4) {
                    group = "영유아기";
                } else {
                    group = "청소년기";
                }
            } else {
                if (year == 1) {
                    group = "청소년기";
                } else if (year <= 4) {
                    group = "청년기";
                } else if (year <= 10) {
                    group = "중장년기";
                } else {
                    group = "노년기";
                }
            }

            if (year < 0 || month < 0 || day < 0) {
                dogHumanAge.setText("-");

            } else {
                dogHumanAge.setText(age + "세 " + group);

            }

            //생일 배경 셋팅
            if (today.substring(5,7).equals(home.getDog().getBirth().substring(5,7)) && today.substring(8,10).equals(home.getDog().getBirth().substring(8,10))) {
                birthImage.setVisibility(View.VISIBLE);
            } else {
                birthImage.setVisibility(View.GONE);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //심장사상충 날짜 셋팅
        if (home.getLastHeartDay() == null) {
            lastHeartDay.setText("-");
            lastHeartDay.setTextColor(Color.parseColor("darkGray"));
            heartButton.setVisibility(View.VISIBLE);
        } else if (home.getLastHeartDay() == 0) {
            lastHeartDay.setText("오늘");
            lastHeartDay.setTextColor(Color.parseColor("#4575b4"));
            heartButton.setVisibility(View.GONE);
        } else {
            lastHeartDay.setText(home.getLastHeartDay() + "일전");
            if (home.getLastHeartDay() >= 30) {
                lastHeartDay.setTextColor(Color.parseColor("#dd695d"));
            } else {
                lastHeartDay.setTextColor(Color.parseColor("darkGray"));
            }
            heartButton.setVisibility(View.VISIBLE);
        }

        //목욕 날짜 셋팅
        if (home.getLastWashDay() == null) {
            lastWashDay.setText("-");
            lastWashDay.setTextColor(Color.parseColor("darkGray"));
            washButton.setVisibility(View.VISIBLE);
        } else if (home.getLastWashDay() == 0) {
            lastWashDay.setText("오늘");
            lastWashDay.setTextColor(Color.parseColor("#4575b4"));
            washButton.setVisibility(View.GONE);
        } else {
            lastWashDay.setText(home.getLastWashDay() + "일전");
            if (home.getLastWashDay() >= 100) {
                lastWashDay.setTextColor(Color.parseColor("#dd695d"));
            } else {
                lastWashDay.setTextColor(Color.parseColor("darkGray"));
            }
            washButton.setVisibility(View.VISIBLE);
        }

        //몸무게 셋팅
        lastWeight.setTextColor(Color.parseColor("darkGray"));
        if (home.getLastWeight() == null) {
            weightButton.setVisibility(View.VISIBLE);
            lastWeight.setText("-");
        } else if (home.getLastWeightDay() == 0) {
            weightButton.setVisibility(View.GONE);
            lastWeight.setText(home.getLastWeight() + "kg");
        } else {
            weightButton.setVisibility(View.VISIBLE);
            lastWeight.setText(home.getLastWeight() + "kg");
        }

        //산책 통계 셋팅
        SimpleDateFormat dayFormat = new SimpleDateFormat("d");
        Integer days = Integer.parseInt(dayFormat.format(currentTime));
        Integer totalMinutes = 0;
        double totalDistance = 0.0;
        for (int i = 0; i < home.getWalkList().size(); i++) {
            WalkVO walk = home.getWalkList().get(i);
            totalMinutes += walk.getMinutes();
            totalDistance += walk.getDistance();
        }
        walkDistance.setText(String.format("%.1f", totalDistance / days)+"km");
        walkMinutes.setText(totalMinutes / days+"분");

        //몸무게 그래프 셋팅
        if (home.getLastWeightDay() != null) {
            setWeightChart(home.getWeightChart());
        } else {
            weightMinText.setText("");
            weightMaxText.setText("");
        }

        //지출 프로그레스바 셋팅
        totalMoneyText.setText(NumberFormat.getInstance(Locale.getDefault()).format(home.getTotalMoney())+"원");

        Integer field1 = 0;
        Integer field2 = 0;
        Integer field3 = 0;
        Integer field4 = 0;
        Integer field5 = 0;

        for (int i = 0; i < home.getMoneyList().size(); i++) {
            MoneyVO money = home.getMoneyList().get(i);
            switch (money.getType()) {
                case 1:
                    field1 += money.getPrice();
                    break;
                case 2:
                    field2 += money.getPrice();
                    break;
                case 3:
                    field3 += money.getPrice();
                    break;
                case 4:
                    field4 += money.getPrice();
                    break;
                case 5:
                    field5 += money.getPrice();
                    break;
            }
        }

        Integer fieldTotal = field1 + field2 + field3 + field4 + field5;

        Integer percentField1 = (int)Math.round((double)field1 * 100 / (double)fieldTotal);
        Integer percentField2 = (int)Math.round((double)field2 * 100 / (double)fieldTotal);
        Integer percentField3 = (int)Math.round((double)field3 * 100 / (double)fieldTotal);
        Integer percentField4 = (int)Math.round((double)field4 * 100 / (double)fieldTotal);
        Integer percentField5 = (int)Math.round((double)field5 * 100 / (double)fieldTotal);

        if (fieldTotal != 0) {
            moneyPercent1.setText(percentField1 + "%");
            moneyPercent2.setText(percentField2 + "%");
            moneyPercent3.setText(percentField3 + "%");
            moneyPercent4.setText(percentField4 + "%");
            moneyPercent5.setText(percentField5 + "%");

            moneyProgress1.setProgress(percentField1);
            moneyProgress2.setProgress(percentField2);
            moneyProgress3.setProgress(percentField3);
            moneyProgress4.setProgress(percentField4);
            moneyProgress5.setProgress(percentField5);
        }
    }

    //몸무게 차트 셋팅
    private void setWeightChart(ArrayList<WeightVO> weightChartList) {
        //값 설정
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < weightChartList.size(); i++) {
            WeightVO weight = weightChartList.get(i);
            float val = (float) (Math.random() * 10);
            values.add(new Entry(i, weight.getKg()));
        }
        LineDataSet dataSet;
        dataSet = new LineDataSet(values, "weight");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        LineData data = new LineData(dataSets);

        //chart 속성 셋팅팅
        dataSet.setColor(Color.parseColor("#65ab84")); //차트 선 색
        dataSet.setDrawFilled(true); // 차트 아래 fill(채우기) 설정
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawValues(false);
        dataSet.setFillColor(Color.parseColor("#acdcad")); // 차트 아래 채우기 색 설정
        dataSet.setDrawCircles(false);
        weightChart.getLegend().setEnabled(false);
        weightChart.getDescription().setEnabled(false);
        weightChart.setTouchEnabled(false);
        weightChart.setDragEnabled(false);

        //x축 설정
        XAxis xAxis = weightChart.getXAxis(); // x 축 설정
        xAxis.setDrawLabels(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        weightMinText.setText(weightChartList.get(0).getDate());
        weightMaxText.setText(weightChartList.get(weightChartList.size()-1).getDate()); //

        //y축 설정
        YAxis yAxisLeft = weightChart.getAxisLeft(); //Y축의 왼쪽면 설정
        yAxisLeft.setDrawLabels(false);
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setDrawGridLines(false);
        YAxis yAxisRight = weightChart.getAxisRight(); //Y축의 오른쪽면 설정
        yAxisRight.setLabelCount(3, true);
        yAxisRight.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkGray)); //Y축 텍스트 컬러 설정
        yAxisRight.setGridColor(ContextCompat.getColor(getApplicationContext(), R.color.gray)); // Y축 줄의 컬러 설정

        weightChart.setData(data);
    }

    //몸무게 차트 기간 변경
    private void setHomeWeight(int type) {
        JSONObject weight = new JSONObject();
        try {
            weight.put("type", type);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
        }
        String weightUrl = Common.URL+"/diary/home/weight/"+HomeVO.getInstance().getDog().getId();
        System.out.println(weightUrl);
        System.out.println(type);
        JsonObjectRequest weightRequest = new JsonObjectRequest(Request.Method.POST, weightUrl, weight,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        try {
//                            Gson gson = new Gson();
//                            WeightVO[] list = gson.fromJson(response.getString("weightChart"), WeightVO[].class);
//                            List<WeightVO> alist = Arrays.asList(list);
//                            ArrayList<WeightVO> arrayList = new ArrayList<WeightVO>();
//                            arrayList.addAll(alist);
//                            setWeightChart(arrayList);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(getApplicationContext(),"서버 연결 실패222 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
//                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"서버 연결 실패333 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                    }
                });
        weightRequest.setShouldCache(false);
        AppHelper.requestQueue.add(weightRequest);
    }
}
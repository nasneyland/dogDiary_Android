package com.najin.dogdiary.calendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.najin.dogdiary.R;
import com.najin.dogdiary.information.EtcActivity;
import com.najin.dogdiary.information.HeartActivity;
import com.najin.dogdiary.information.MoneyActivity;
import com.najin.dogdiary.information.WalkedActivity;
import com.najin.dogdiary.information.WashActivity;
import com.najin.dogdiary.information.WeightActivity;
import com.najin.dogdiary.model.CalendarVO;
import com.najin.dogdiary.model.EtcVO;
import com.najin.dogdiary.model.HeartVO;
import com.najin.dogdiary.model.HomeVO;
import com.najin.dogdiary.model.MoneyVO;
import com.najin.dogdiary.model.WalkVO;
import com.najin.dogdiary.model.WashVO;
import com.najin.dogdiary.model.WeightVO;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;
import com.najin.dogdiary.update.UpdateEtcActivity;
import com.najin.dogdiary.update.UpdateMoneyActivity;
import com.najin.dogdiary.update.UpdateWalkedActivity;
import com.najin.dogdiary.update.UpdateWeightActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends FragmentActivity {

    ConstraintLayout calendarLoadingBackground;
    ConstraintLayout calendarLoadingView;
    private TextView thisMonthText;
    TextView todayText;
    TextView calendarSelectedDate;
    ConstraintLayout calendarPopUpBackground;

    //??????
    ImageButton walkButton;
    ImageButton washButton;
    ImageButton weightButton;
    ImageButton heartButton;
    ImageButton moneyButton;
    ImageButton etcButton;

    //day list ??????
    ListAdapter listAdapter;
    ListView listView;

    //?????? intent?????? ????????????
    String selectedDate;
    String selectedDateString;
    ArrayList<Object> selectedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarLoadingBackground = (ConstraintLayout) findViewById(R.id.calendarLoadingBackground);
        calendarLoadingView = (ConstraintLayout) findViewById(R.id.calendarLoadingView);
        thisMonthText = findViewById(R.id.thisMonth);
        todayText = findViewById(R.id.todayText);
        calendarSelectedDate = findViewById(R.id.calendarSelectedDate);
        calendarPopUpBackground = (ConstraintLayout) findViewById(R.id.calendarPopUpBackground);
        calendarPopUpBackground.setVisibility(View.GONE);
        walkButton = (ImageButton) findViewById(R.id.walkButton);
        washButton = (ImageButton) findViewById(R.id.washButton);
        weightButton = (ImageButton) findViewById(R.id.weightButton);
        heartButton = (ImageButton) findViewById(R.id.heartButton);
        moneyButton = (ImageButton) findViewById(R.id.moneyButton);
        etcButton = (ImageButton) findViewById(R.id.etcButton);

        listView = (ListView) findViewById(R.id.calendarList);

        //???????????? ????????????
        calendarPopUpBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarPopUpBackground.setVisibility(View.GONE);
            }
        });

        //?????? ?????? ??????
        todayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarLoadingBackground.setVisibility(View.VISIBLE);
                CalendarMonthSet.getInstance().setYear(0);
                CalendarMonthSet.getInstance().setMonth(0);
                Intent calendarIntent = new Intent(getApplicationContext(), CalendarActivity.class);
                calendarIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(calendarIntent);
                overridePendingTransition(0, 0);
            }
        });

        //???????????? ?????? ?????????
        walkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent walkedActivity = new Intent(getApplicationContext(), WalkedActivity.class);
                walkedActivity.putExtra("informationDate", selectedDate);
                walkedActivity.putExtra("informationDateString", selectedDateString);
                startActivity(walkedActivity);
            }
        });

        //?????? ?????? ?????????
        washButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent washActivity = new Intent(getApplicationContext(), WashActivity.class);
                washActivity.putExtra("informationDate", selectedDate);
                washActivity.putExtra("informationDateString", selectedDateString);
                startActivity(washActivity);
            }
        });

        //????????? ?????? ?????????
        weightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent weightActivity = new Intent(getApplicationContext(), WeightActivity.class);
                weightActivity.putExtra("informationDate", selectedDate);
                weightActivity.putExtra("informationDateString", selectedDateString);
                startActivity(weightActivity);
            }
        });

        //??????????????? ?????? ?????????
        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent heartActivity = new Intent(getApplicationContext(), HeartActivity.class);
                heartActivity.putExtra("informationDate", selectedDate);
                heartActivity.putExtra("informationDateString", selectedDateString);
                startActivity(heartActivity);
            }
        });

        //?????? ?????? ?????????
        moneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moneyActivity = new Intent(getApplicationContext(), MoneyActivity.class);
                moneyActivity.putExtra("informationDate", selectedDate);
                moneyActivity.putExtra("informationDateString", selectedDateString);
                startActivity(moneyActivity);
            }
        });

        //???????????? ?????? ?????????
        etcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent etcActivity = new Intent(getApplicationContext(), EtcActivity.class);
                etcActivity.putExtra("informationDate", selectedDate);
                etcActivity.putExtra("informationDateString", selectedDateString);
                startActivity(etcActivity);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        calendarLoadingBackground.setVisibility(View.GONE);
        calendarLoadingView.setVisibility(View.GONE);
        calendarPopUpBackground.setVisibility(View.GONE);

        //?????? flagMent ??????
        CalendarFragment calendarFragment = (CalendarFragment) getSupportFragmentManager().findFragmentById(R.id.calendarFragment);
        calendarFragment.setOnMonthChangeListener(new CalendarFragment.MonthlyFragmentListener() {
            @Override
            public void onChange(int year, int month) {
                //?????? ?????? ??? (?????? ????????? ??? ??????)
                CalendarMonthSet.getInstance().setYear(year);
                CalendarMonthSet.getInstance().setMonth(month);

                thisMonthText.setText(year + "." + (month + 1));
            }

            @Override
            public void onDayClick(CalendarDayView dayView) {
                //?????? ????????? ???
                Integer year = dayView.get(Calendar.YEAR);
                Integer month = dayView.get(Calendar.MONTH) + 1;
                Integer day = dayView.get(Calendar.DAY_OF_MONTH);

                Integer thisYear = Calendar.getInstance().get(Calendar.YEAR);
                Integer thisMonth = Calendar.getInstance().get(Calendar.MONTH)+1;
                Integer thisDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

                selectedDate = String.format("%s-%02d-%02d", year, month, day);
                selectedDateString = String.format("%s??? %d??? %d???", year, month, day);

                //????????? ????????? ???
                //????????? ???????????? ?????????
                if (HomeVO.getInstance().getDog().getId() == null) return;
                //?????? ?????? ?????? ????????????
                else if ((year > thisYear) || ((year.equals(thisYear)) && (month > thisMonth)) || ((year.equals(thisYear)) && (month.equals(thisMonth)) && (day > thisDay))) {
                    calendarPopUpBackground.setVisibility(View.VISIBLE);
                    walkButton.setVisibility(View.GONE);
                    washButton.setVisibility(View.GONE);
                    weightButton.setVisibility(View.GONE);
                    heartButton.setVisibility(View.GONE);
                    moneyButton.setVisibility(View.GONE);
                    //?????? ??????
                    calendarSelectedDate.setText(selectedDateString);

                    //list ??????
                    listAdapter = new ListAdapter();

                    for (EtcVO etc : CalendarVO.getInstance().getEtcList()) {
                        if (etc.getDate().equals(selectedDate)) {
                            listAdapter.addItem(new ListItem(0, etc.getId(), etc.getColor(),"["+etc.getTitle()+"] "+etc.getContent()));
                        }
                    }
                    listView.setAdapter(listAdapter);
                } else {
                    calendarPopUpBackground.setVisibility(View.VISIBLE);
                    //?????? ??????
                    calendarSelectedDate.setText(selectedDateString);

                    walkButton.setVisibility(View.VISIBLE);
                    washButton.setVisibility(View.VISIBLE);
                    weightButton.setVisibility(View.VISIBLE);
                    heartButton.setVisibility(View.VISIBLE);
                    moneyButton.setVisibility(View.VISIBLE);
                    etcButton.setVisibility(View.VISIBLE);

                    //list ??????
                    listAdapter = new ListAdapter();
                    selectedList = new ArrayList<>();

                    //?????? ??????????????? ????????? ????????? ???????????? ???????????? ???????????? ??????
                    for (WalkVO walk : CalendarVO.getInstance().getWalkList()) {
                        if (walk.getDate().equals(selectedDate)) {
                            listAdapter.addItem(new ListItem(1, walk.getId(), "#dd695d",walk.getTime() + " " + walk.getDistance() + "km " + walk.getMinutes() + "??? ??????"));
                            selectedList.add(walk);
                        }
                    }
                    for (WashVO wash : CalendarVO.getInstance().getWashList()) {
                        if (wash.getDate().equals(selectedDate)) {
                            listAdapter.addItem(new ListItem(2, wash.getId(), "#fdae61","???????????????"));
                            washButton.setVisibility(View.GONE);
                            selectedList.add(wash);
                        }
                    }
                    for (WeightVO weight : CalendarVO.getInstance().getWeightList()) {
                        if (weight.getDate().equals(selectedDate)) {
                            listAdapter.addItem(new ListItem(3, weight.getId(), "#65ab84", "????????? " + weight.getKg() + "kg"));
                            weightButton.setVisibility(View.GONE);
                            selectedList.add(weight);
                        }
                    }
                    for (HeartVO heart : CalendarVO.getInstance().getHeartList()) {
                        if (heart.getDate().equals(selectedDate)) {
                            listAdapter.addItem(new ListItem(4, heart.getId(), "#4575b4", "??????????????? ?????????"));
                            heartButton.setVisibility(View.GONE);
                            selectedList.add(heart);
                        }
                    }
                    for (MoneyVO money : CalendarVO.getInstance().getMoneyList()) {
                        if (money.getDate().equals(selectedDate)) {
                            String type="";
                            switch (money.getType()) {
                                case 1:
                                    type = "??????/??????";
                                    break;
                                case 2:
                                    type = "?????????";
                                    break;
                                case 3:
                                    type = "??????";
                                    break;
                                case 4:
                                    type = "??????/??????";
                                    break;
                                case 5:
                                    type = "??????";
                                    break;

                            }
                            listAdapter.addItem(new ListItem(5, money.getId(), "#5e4fa2", "("+type+") "+NumberFormat.getInstance(Locale.getDefault()).format(money.getPrice()) + "??? " + money.getItem()));
                            selectedList.add(money);
                        }
                    }
                    for (EtcVO etc : CalendarVO.getInstance().getEtcList()) {
                        if (etc.getDate().equals(selectedDate)) {
                            listAdapter.addItem(new ListItem(0, etc.getId(), etc.getColor(),"("+etc.getTitle()+") "+etc.getContent()));
                            selectedList.add(etc);
                        }
                    }

                    listView.setAdapter(listAdapter);
                }

                //???????????? ????????? ?????? ?????????
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ListItem item = (ListItem) listAdapter.getItem(position);
                        final CharSequence[] oItems;

                        if (item.getType() == 2 || item.getType() == 4) {
                            oItems = new CharSequence[]{"????????????"};
                        } else {
                            oItems = new CharSequence[]{"????????????", "????????????"};
                        }
                        AlertDialog.Builder oDialog = new AlertDialog.Builder(CalendarActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

                        //alert title ??????
                        TextView title = new TextView(CalendarActivity.this);
                        title.setPadding(50, 50, 50, 0);
                        title.setGravity(Gravity.CENTER);
                        title.setTextSize(15);
                        title.setTextColor(Color.GRAY);
                        title.setText(item.getDetail());
                        oDialog.setCustomTitle(title);

                        oDialog.setItems(oItems, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                ListItem item = (ListItem) listAdapter.getItem(position);
                                calendarLoadingView.setVisibility(View.VISIBLE);
                                if (which == 0) {
                                    switch (item.getType()) {
                                        case 1: //???????????? ??????
                                            Intent walkedActivity = new Intent(getApplicationContext(), UpdateWalkedActivity.class);
                                            WalkVO selectedWalk = (WalkVO) selectedList.get(position);
                                            walkedActivity.putExtra("informationDateString", selectedDateString);
                                            walkedActivity.putExtra("walkId", selectedWalk.getId());
                                            walkedActivity.putExtra("walkDate", selectedWalk.getDate());
                                            walkedActivity.putExtra("walkTime", selectedWalk.getTime());
                                            walkedActivity.putExtra("walkMinutes", selectedWalk.getMinutes());
                                            walkedActivity.putExtra("walkDistance", selectedWalk.getDistance());
                                            startActivity(walkedActivity);
                                            break;
                                        case 2: //???????????? ??????
                                            deleteWash(item.getId());
                                            break;
                                        case 3: //??????????????? ??????
                                            Intent weightActivity = new Intent(getApplicationContext(), UpdateWeightActivity.class);
                                            WeightVO selectedWeight = (WeightVO) selectedList.get(position);
                                            weightActivity.putExtra("informationDateString", selectedDateString);
                                            weightActivity.putExtra("weightId", selectedWeight.getId());
                                            weightActivity.putExtra("weightDate", selectedWeight.getDate());
                                            weightActivity.putExtra("weight", selectedWeight.getKg());
                                            startActivity(weightActivity);
                                            break;
                                        case 4: //??????????????? ??????
                                            deleteHeart(item.getId());
                                            break;
                                        case 5: //???????????? ??????
                                            Intent moneyActivity = new Intent(getApplicationContext(), UpdateMoneyActivity.class);
                                            MoneyVO selectedMoney = (MoneyVO) selectedList.get(position);
                                            moneyActivity.putExtra("informationDateString", selectedDateString);
                                            moneyActivity.putExtra("moneyId", selectedMoney.getId());
                                            moneyActivity.putExtra("moneyDate", selectedMoney.getDate());
                                            moneyActivity.putExtra("moneyType", selectedMoney.getType());
                                            moneyActivity.putExtra("moneyItem", selectedMoney.getItem());
                                            moneyActivity.putExtra("moneyPrice", selectedMoney.getPrice());
                                            startActivity(moneyActivity);
                                            break;
                                        case 0: //???????????? ??????
                                            Intent etcActivity = new Intent(getApplicationContext(), UpdateEtcActivity.class);
                                            EtcVO selectedEtc = (EtcVO) selectedList.get(position);
                                            etcActivity.putExtra("informationDateString", selectedDateString);
                                            etcActivity.putExtra("etcId", selectedEtc.getId());
                                            etcActivity.putExtra("etcDate", selectedEtc.getDate());
                                            etcActivity.putExtra("etcColor", selectedEtc.getColor());
                                            etcActivity.putExtra("etcTitle", selectedEtc.getTitle());
                                            etcActivity.putExtra("etcContent", selectedEtc.getContent());
                                            startActivity(etcActivity);
                                            break;
                                    }
                                } else if (which == 1) {
                                    switch (item.getType()) {
                                        case 1: //???????????? ??????
                                            deleteWalk(item.getId());
                                            break;
                                        case 3: //??????????????? ??????
                                            deleteWeight(item.getId());
                                            break;
                                        case 5: //???????????? ??????
                                            deleteMoney(item.getId());
                                            break;
                                        case 0: //???????????? ??????
                                            deleteEtc(item.getId());
                                            break;
                                    }
                                }
                            }
                        }).setCancelable(true).show();
                    }
                });
            }
        });
    }

    //???????????? ????????? ??????
    class ListAdapter extends BaseAdapter {
        ArrayList<ListItem> items = new ArrayList<ListItem>();

        @Override
        public int getCount() {
            int cnt = items.size();
            int height;
            switch (cnt) {
                case 0:
                    height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
                    break;
                default:
                    height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics());
                    break;
            }

            listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));

            return cnt;
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListItemView view = new ListItemView(getApplicationContext());

            ListItem item = items.get(position);
            view.setColor(item.getColor());
            view.setDetail(item.getDetail());

            return view;
        }

        public void addItem(ListItem item) {
            items.add(item);
        }
    }

    //???????????? ?????? ??? ?????? ??????
    private void deleteWalk(Integer id) {
        String url = Common.URL + "/diary/walk/" + id;
        StringRequest request = new StringRequest(
                Request.Method.DELETE,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String walkUrl = Common.URL+"/diary/walk/"+HomeVO.getInstance().getDog().getId();
                        StringRequest dogRequest = new StringRequest(
                                Request.Method.GET,
                                walkUrl,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Gson gson = new Gson();
                                        WalkVO[] list = gson.fromJson(response,WalkVO[].class);
                                        List<WalkVO> alist = Arrays.asList(list);
                                        ArrayList<WalkVO> arrayList = new ArrayList<WalkVO>();
                                        arrayList.addAll(alist);
                                        CalendarVO.getInstance().setWalkList(arrayList);
                                        loadHomeData();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(),"?????? ?????? ?????? : ????????? ?????? ??????????????????",Toast.LENGTH_SHORT).show();
                                        calendarLoadingView.setVisibility(View.GONE);
                                    }
                                }
                        );
                        dogRequest.setShouldCache(false);
                        AppHelper.requestQueue.add(dogRequest);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "?????? ?????? ?????? : ????????? ?????? ??????????????????", Toast.LENGTH_SHORT).show();
                        calendarLoadingView.setVisibility(View.GONE);
                    }
                }
        );
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    //???????????? ?????? ??? ?????? ??????
    private void deleteWash(Integer id) {
        String url = Common.URL + "/diary/wash/" + id;
        StringRequest request = new StringRequest(
                Request.Method.DELETE,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String washUrl = Common.URL+"/diary/wash/"+HomeVO.getInstance().getDog().getId();
                        StringRequest dogRequest = new StringRequest(
                                Request.Method.GET,
                                washUrl,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Gson gson = new Gson();
                                        WashVO[] list = gson.fromJson(response,WashVO[].class);
                                        List<WashVO> alist = Arrays.asList(list);
                                        ArrayList<WashVO> arrayList = new ArrayList<WashVO>();
                                        arrayList.addAll(alist);
                                        CalendarVO.getInstance().setWashList(arrayList);
                                        loadHomeData();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(),"?????? ?????? ?????? : ????????? ?????? ??????????????????",Toast.LENGTH_SHORT).show();
                                        calendarLoadingView.setVisibility(View.GONE);
                                    }
                                }
                        );
                        dogRequest.setShouldCache(false);
                        AppHelper.requestQueue.add(dogRequest);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "?????? ?????? ?????? : ????????? ?????? ??????????????????", Toast.LENGTH_SHORT).show();
                        calendarLoadingView.setVisibility(View.GONE);
                    }
                }
        );
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    //????????? ?????? ?????? ??? ?????? ??????
    private void deleteWeight(Integer id) {
        String url = Common.URL + "/diary/weight/" + id;
        StringRequest request = new StringRequest(
                Request.Method.DELETE,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String weightUrl = Common.URL+"/diary/weight/"+HomeVO.getInstance().getDog().getId();
                        StringRequest dogRequest = new StringRequest(
                                Request.Method.GET,
                                weightUrl,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Gson gson = new Gson();
                                        WeightVO[] list = gson.fromJson(response,WeightVO[].class);
                                        List<WeightVO> alist = Arrays.asList(list);
                                        ArrayList<WeightVO> arrayList = new ArrayList<WeightVO>();
                                        arrayList.addAll(alist);
                                        CalendarVO.getInstance().setWeightList(arrayList);
                                        loadHomeData();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(),"?????? ?????? ?????? : ????????? ?????? ??????????????????",Toast.LENGTH_SHORT).show();
                                        calendarLoadingView.setVisibility(View.GONE);
                                    }
                                }
                        );
                        dogRequest.setShouldCache(false);
                        AppHelper.requestQueue.add(dogRequest);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "?????? ?????? ?????? : ????????? ?????? ??????????????????", Toast.LENGTH_SHORT).show();
                        calendarLoadingView.setVisibility(View.GONE);
                    }
                }
        );
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    //??????????????? ?????? ?????? ??? ?????? ??????
    private void deleteHeart(Integer id) {
        String url = Common.URL + "/diary/heart/" + id;
        StringRequest request = new StringRequest(
                Request.Method.DELETE,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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

                                        loadHomeData();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(),"?????? ?????? ?????? : ????????? ?????? ??????????????????",Toast.LENGTH_SHORT).show();
                                        calendarLoadingView.setVisibility(View.GONE);
                                    }
                                }
                        );
                        dogRequest.setShouldCache(false);
                        AppHelper.requestQueue.add(dogRequest);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "?????? ?????? ?????? : ????????? ?????? ??????????????????", Toast.LENGTH_SHORT).show();
                        calendarLoadingView.setVisibility(View.GONE);
                    }
                }
        );
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    //?????? ?????? ?????? ??? ?????? ??????
    private void deleteMoney(Integer id) {
        String url = Common.URL + "/diary/money/" + id;
        StringRequest request = new StringRequest(
                Request.Method.DELETE,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String moneyUrl = Common.URL+"/diary/money/"+HomeVO.getInstance().getDog().getId();
                        StringRequest dogRequest = new StringRequest(
                                Request.Method.GET,
                                moneyUrl,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Gson gson = new Gson();
                                        MoneyVO[] list = gson.fromJson(response,MoneyVO[].class);
                                        List<MoneyVO> alist = Arrays.asList(list);
                                        ArrayList<MoneyVO> arrayList = new ArrayList<MoneyVO>();
                                        arrayList.addAll(alist);
                                        CalendarVO.getInstance().setMoneyList(arrayList);

                                        loadHomeData();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(),"?????? ?????? ?????? : ????????? ?????? ??????????????????",Toast.LENGTH_SHORT).show();
                                        calendarLoadingView.setVisibility(View.GONE);
                                    }
                                }
                        );
                        dogRequest.setShouldCache(false);
                        AppHelper.requestQueue.add(dogRequest);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "?????? ?????? ?????? : ????????? ?????? ??????????????????", Toast.LENGTH_SHORT).show();
                        calendarLoadingView.setVisibility(View.GONE);
                    }
                }
        );
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    //?????? ?????? ?????? ??? ?????? ??????
    private void deleteEtc(Integer id) {
        String url = Common.URL + "/diary/etc/" + id;
        StringRequest request = new StringRequest(
                Request.Method.DELETE,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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

                                        Intent mainIntent = new Intent(getApplicationContext(), CalendarActivity.class);
                                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(mainIntent);
                                        overridePendingTransition(0, 0);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(),"?????? ?????? ?????? : ????????? ?????? ??????????????????",Toast.LENGTH_SHORT).show();
                                        calendarLoadingView.setVisibility(View.GONE);
                                    }
                                }
                        );
                        dogRequest.setShouldCache(false);
                        AppHelper.requestQueue.add(dogRequest);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "?????? ?????? ?????? : ????????? ?????? ??????????????????", Toast.LENGTH_SHORT).show();
                        calendarLoadingView.setVisibility(View.GONE);
                    }
                }
        );
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    //home data ??????
    private void loadHomeData() {
        String dogId = HomeVO.getInstance().getDog().getId();
        String dogUrl = Common.URL + "/diary/home/" + dogId;
        StringRequest homeRequest = new StringRequest(
                Request.Method.GET,
                dogUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //homeVO ?????????
                        Gson gson = new Gson();
                        HomeVO.setInstance(gson.fromJson(response, HomeVO.class));
                        Intent mainIntent = new Intent(getApplicationContext(), CalendarActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                        overridePendingTransition(0, 0);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "?????? ?????? ?????? : ????????? ?????? ??????????????????", Toast.LENGTH_SHORT).show();
                        calendarLoadingView.setVisibility(View.GONE);
                    }
                }
        );
        homeRequest.setShouldCache(false);
        AppHelper.requestQueue.add(homeRequest);
    }
}



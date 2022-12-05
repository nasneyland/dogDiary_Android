package com.najin.dogdiary.calendar;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.najin.dogdiary.R;
import com.najin.dogdiary.model.CalendarVO;
import com.najin.dogdiary.model.DayVO;
import com.najin.dogdiary.model.EtcVO;
import com.najin.dogdiary.model.HeartVO;
import com.najin.dogdiary.model.HomeVO;
import com.najin.dogdiary.model.MoneyVO;
import com.najin.dogdiary.model.WalkVO;
import com.najin.dogdiary.model.WashVO;
import com.najin.dogdiary.model.WeightVO;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CalendarDayView extends RelativeLayout {

    private CalendarDayItem calendarDay;

    //오늘 날짜 표시
    Calendar now = Calendar.getInstance();
    Integer year = now.get(Calendar.YEAR);
    Integer month = now.get(Calendar.MONTH)+1;
    Integer day = now.get(Calendar.DATE);

    private TextView dayText;
    private View todayView;
    private TextView list1Text;
    private TextView list2Text;
    private TextView list3Text;
    private TextView list4Text;
    private TextView addListText;
    private ImageView birthCake;

    public CalendarDayView(@NonNull Context context) {
        this(context, null);
    }

    public CalendarDayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        calendarDay = new CalendarDayItem();

        //calendar cell 지정
        View v = View.inflate(context, R.layout.cell_calendar_day, this);
        dayText = v.findViewById(R.id.calendarDate);
        todayView = v.findViewById(R.id.todayView);
        list1Text = v.findViewById(R.id.list1Text);
        list2Text = v.findViewById(R.id.list2Text);
        list3Text = v.findViewById(R.id.list3Text);
        list4Text = v.findViewById(R.id.list4Text);
        addListText = v.findViewById(R.id.addListText);
        birthCake = v.findViewById(R.id.birthCake);

        todayView.setVisibility(INVISIBLE);
        list1Text.setVisibility(GONE);
        list2Text.setVisibility(GONE);
        list3Text.setVisibility(GONE);
        list4Text.setVisibility(GONE);
        addListText.setVisibility(GONE);
        birthCake.setVisibility(GONE);
    }

    //날짜 지정
    public void setDay(int year, int month, int day) {
        calendarDay.cal.set(year, month, day);
    }

    public void setDay(Calendar cal) {
        calendarDay.setDay((Calendar) cal.clone(), calendarDay.getIsMonth());
    }

    public void setDay(CalendarDayItem one) {
        calendarDay = one;
    }

    public CalendarDayItem getDay() {
        return calendarDay;
    }

    public int get(int field) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return calendarDay.get(field);
    }

    //이번달->전달->다음달 셋팅
    public void refresh() {
        dayText.setText(String.valueOf(calendarDay.get(Calendar.DAY_OF_MONTH)));

        //날짜 색상 지정
        if (!calendarDay.getIsMonth()) {
            //이번달이 아닌 날짜는 회색으로
            dayText.setTextColor(Color.parseColor("#adb5bd"));
        } else if(calendarDay.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            //일요일 빨간색으로 지정
            dayText.setTextColor(Color.parseColor("#dd695d"));
        } else {
            dayText.setTextColor(Color.parseColor("#000000"));
        }

        String dateString = String.format("%02d-%02d-%02d", calendarDay.get(Calendar.YEAR), calendarDay.get(Calendar.MONTH)+1, calendarDay.get(Calendar.DAY_OF_MONTH));
        String todayString = String.format("%02d-%02d-%02d", year, month, day);

        if (calendarDay.getIsMonth() && dateString.equals(todayString)) {
            dayText.setTextColor(Color.parseColor("#ffffff"));
            todayView.setVisibility(VISIBLE);
        } else {
            todayView.setVisibility(GONE);
        }

        if (HomeVO.getInstance().getDog().getId() != null) {
            //생일 케이크 셋팅
            if (calendarDay.getIsMonth() && dateString.substring(5).equals(HomeVO.getHomeVO().getDog().getBirth().substring(5))) {
                birthCake.setVisibility(VISIBLE);
                if (calendarDay.getIsMonth() && dateString.equals(todayString)) {
                    dayText.setVisibility(INVISIBLE);
                    todayView.setVisibility(INVISIBLE);
                } else {
                    todayView.setVisibility(VISIBLE);
                    dayText.setVisibility(VISIBLE);
                }
            } else {
                birthCake.setVisibility(GONE);
            }

            //데이터 셋팅
            if (calendarDay.getIsMonth()) {
                Integer walkMinutes = 0;
                double walkDistance = 0.0;

                ArrayList<DayVO> dayList = new ArrayList<DayVO>();

                //산책 시간 표시
                for (WalkVO walk : CalendarVO.getInstance().getWalkList()) {
                    if (walk.getDate().equals(dateString)) {
                        walkDistance += walk.getDistance();
                        walkMinutes += walk.getMinutes();
                    }
                }

                if (walkMinutes > 0) {
                    DayVO day = new DayVO();
                    day.setColor("#dd695d");
                    day.setTitle(String.format("%.1fkm", walkDistance));
                    dayList.add(day);
                }

                //심장사상충 복용일 나타내기
                boolean heartDate = false;
                for (HeartVO heart : CalendarVO.getInstance().getHeartList()) {
                    if (heart.getDate().equals(dateString)) {
                        heartDate = true;
                    }
                }
                if (heartDate) {
                    DayVO day = new DayVO();
                    day.setColor("#4575b4");
                    day.setTitle("심장사상충");
                    dayList.add(day);
                }

                //목욕일자 나타내기
                boolean washDate = false;
                for (WashVO wash : CalendarVO.getInstance().getWashList()) {
                    if (wash.getDate().equals(dateString)) {
                        washDate = true;
                    }
                }
                if (washDate) {
                    DayVO day = new DayVO();
                    day.setColor("#fdae61");
                    day.setTitle("목욕");
                    dayList.add(day);
                }

                //몸무게 잰 날짜와 몸무게 나타내기
                double weightKG = 0;
                for (WeightVO weight : CalendarVO.getInstance().getWeightList()) {
                    if (weight.getDate().equals(dateString)) {
                        weightKG = weight.getKg();
                    }
                }
                if (weightKG != 0) {
                    DayVO day = new DayVO();
                    day.setColor("#65ab84");
                    day.setTitle(String.format("%.1fkg", weightKG));
                    dayList.add(day);
                }

                //지출금액 나타내기
                Integer moneyPrices = 0;
                boolean moneyDate = false;
                for (MoneyVO money : CalendarVO.getInstance().getMoneyList()) {
                    if (money.getDate().equals(dateString)) {
                        moneyPrices += money.getPrice();
                        moneyDate = true;
                    }
                }
                if (moneyDate) {
                    DayVO day = new DayVO();
                    day.setColor("#5e4fa2");
                    day.setTitle(NumberFormat.getInstance(Locale.getDefault()).format(moneyPrices) + "원");
                    dayList.add(day);
                }

                //기타내역 나타내기
                for (EtcVO etc : CalendarVO.getInstance().getEtcList()) {
                    if (etc.getDate().equals(dateString)) {
                        DayVO day = new DayVO();
                        day.setColor(etc.getColor());
                        day.setTitle(etc.getTitle());
                        dayList.add(day);
                    }
                }

                if (dayList.size() == 0) {
                    list1Text.setVisibility(GONE);
                    list2Text.setVisibility(GONE);
                    list3Text.setVisibility(GONE);
                    list4Text.setVisibility(GONE);
                    addListText.setVisibility(GONE);
                } else {
                    if (dayList.size() >= 1) {
                        listSet(list1Text, dayList.get(0).getColor(), dayList.get(0).getTitle());
                    }
                    if (dayList.size() >= 2) {
                        listSet(list2Text, dayList.get(1).getColor(), dayList.get(1).getTitle());
                    }
                    if (dayList.size() >= 3) {
                        listSet(list3Text, dayList.get(2).getColor(), dayList.get(2).getTitle());
                    }
                    if (dayList.size() >= 4) {
                        listSet(list4Text, dayList.get(3).getColor(), dayList.get(3).getTitle());
                    }
                    if (dayList.size() >= 5) {
                        addListText.setVisibility(VISIBLE);
                        addListText.setText("+" + (dayList.size() - 4));
                    }
                }
            }
        }
    }

    private void listSet(TextView listText, String color, String title) {
        listText.setVisibility(VISIBLE);
        listText.setBackgroundColor(Color.parseColor(color));
        listText.setText(title);
    }
}
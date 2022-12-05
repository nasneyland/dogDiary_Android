package com.najin.dogdiary.calendar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.Calendar;

class CalendarPagerAdapter extends PagerAdapter
        implements ViewPager.OnPageChangeListener, CalendarMonthView.OnClickDayListener {

    private CalendarInterface calendarInterface;
    private CalendarMonthView[] monthViews;
    //기본 달력 셋팅
    private final static int BASE_YEAR = 2021;
    private final static int BASE_MONTH = Calendar.JANUARY;
    private final Calendar BASE_CAL;
    //리사이클 페이지 셋팅
    private final static int PAGES = 5;
    private final static int TOTAL_PAGES = Integer.MAX_VALUE;

    private final static int BASE_POSITION = TOTAL_PAGES / 2;
    private int previousPosition;

    public CalendarPagerAdapter(@NonNull Context context, @Nullable CalendarInterface calendarInterface) {
        this.calendarInterface = calendarInterface;
        Calendar base = Calendar.getInstance();
        base.set(BASE_YEAR, BASE_MONTH, 1);
        BASE_CAL = base;

        monthViews = new CalendarMonthView[PAGES];
        for(int i = 0; i < PAGES; i++) {
            monthViews[i] = new CalendarMonthView(context);
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        int howFarFromBase = position - BASE_POSITION;
        Calendar cal = (Calendar) BASE_CAL.clone();
        cal.add(Calendar.MONTH, howFarFromBase);

        position = position % PAGES;

        container.addView(monthViews[position]);

        monthViews[position].make(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
        monthViews[position].setOnClickDayListener(this);

        return monthViews[position];
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((CalendarMonthView) object).setOnClickDayListener(null);
        container.removeView((View) object);
    }

    public CalendarMonthItem getYearMonth(int position) {
        Calendar cal = (Calendar)BASE_CAL.clone();
        cal.add(Calendar.MONTH, position - BASE_POSITION);
        return new CalendarMonthItem(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
    }

    public int getPosition(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        return BASE_POSITION + howFarFromBase(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
    }

    private int howFarFromBase(int year, int month) {

        int disY = (year - BASE_YEAR) * 12;
        int disM = month - BASE_MONTH;

        return disY + disM;
    }

    //재활용 페이지 override
    @Override
    public int getCount() {
        return TOTAL_PAGES;
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }

    //페이지 변경 override
    @Override
    public void onPageScrollStateChanged(int state) {
        switch(state) {
        case ViewPager.SCROLL_STATE_DRAGGING:
            previousPosition = calendarInterface != null ? calendarInterface.getCurrentPosition() : 0;
            break;
        }
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(previousPosition != position) {
            previousPosition = position;
            CalendarMonthItem ym = getYearMonth(position);
            if (calendarInterface != null) {
                calendarInterface.onMonthChanged(ym.year, ym.month);
            }
        }
    }
    @Override
    public void onPageSelected(int position) {
    }

    //캘린더 클릭 override
    @Override
    public void onClick(CalendarDayView odv) {
        if (calendarInterface != null) calendarInterface.onClickDay(odv);
    }
}

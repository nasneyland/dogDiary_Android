/*
* Copyright (C) 2018 HansooLabs.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.najin.dogdiary.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarMonthView extends LinearLayout implements View.OnClickListener {

    public interface OnClickDayListener {
        void onClick(CalendarDayView odv);
    }

    private int mYear;
    private int mMonth;
    @Nullable
    private ArrayList<LinearLayout> weeks = null;
    @Nullable
    private ArrayList<CalendarDayView> dayViews = null;
    @Nullable
    private OnClickDayListener onClickDayListener = null;

    public void setOnClickDayListener(@Nullable OnClickDayListener listener) {
        this.onClickDayListener = listener;
    }

    public CalendarMonthView(Context context) {
        this(context, null);
    }

    public CalendarMonthView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarMonthView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(LinearLayout.VERTICAL);
        //Prepare many day-views enough to prevent recreation.

        if(weeks == null) {
            weeks = new ArrayList<>(6); //Max 6 weeks in a month
            dayViews = new ArrayList<>(42); // 7 days * 6 weeks = 42 days

            LinearLayout ll = null;
            for(int i=0; i<42; i++) {

                if(i % 7 == 0) {
                    //Create new week layout
                    ll = new LinearLayout(context);
                    LayoutParams params
                            = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
                    params.weight = 1;
                    ll.setOrientation(LinearLayout.HORIZONTAL);
                    ll.setLayoutParams(params);
                    ll.setWeightSum(7);

                    weeks.add(ll);
                }

                LayoutParams params
                        = new LayoutParams(0, LayoutParams.MATCH_PARENT);
                params.weight = 1;

                CalendarDayView ov = new CalendarDayView(context);
                ov.setLayoutParams(params);
                ov.setOnClickListener(this);

                ll.addView(ov);
                dayViews.add(ov);
            }
        }
        
        //for Preview of Graphic editor
        if(isInEditMode()) {
            Calendar cal = Calendar.getInstance();
            make(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
        }
    }

    /**
     * Get current year
     * @return 4 digits number of year
     */
    public int getYear() {
        return mYear;
    }

    /**
     * Get current month
     * @return 0~11 (Calendar.JANUARY ~ Calendar.DECEMBER)
     */
    public int getMonth() {
        return mMonth;
    }


    /**
     * Any layout manager that doesn't scroll will want this.
     */
    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }


    //monthView 셋팅
    public void make(int year, int month)
    {
        if(mYear == year && mMonth == month) {
            return;
        }
        
        long makeTime = System.currentTimeMillis();
        
        this.mYear = year;
        this.mMonth = month;

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        cal.setFirstDayOfWeek(Calendar.SUNDAY);//Sunday is first day of week in this sample
        
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);//Get day of the week in first day of this month
        int maxOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);//Get max day number of this month
        ArrayList<CalendarDayItem> oneDayDataList = new ArrayList<>();
        
        cal.add(Calendar.DAY_OF_MONTH, Calendar.SUNDAY - dayOfWeek);//Move to first day of first week

        //전달 날짜 셋팅 (gray)
        int seekDay;
        for(;;) {
            seekDay = cal.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek == seekDay) break;
            CalendarDayItem one = new CalendarDayItem();
            one.setDay(cal,false);
            oneDayDataList.add(one);
            //하루 증가
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        //이번달 날짜 셋팅 (black)
        for(int i=0; i < maxOfMonth; i++) {
            CalendarDayItem one = new CalendarDayItem();
            one.setDay(cal,true);
            oneDayDataList.add(one);
            //add one day
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        //다음달 날짜 셋팅 (gray)
        for(;;) {
            if(cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                CalendarDayItem one = new CalendarDayItem();
                one.setDay(cal,false);
                oneDayDataList.add(one);
            } 
            else {
                break;
            }
            //add one day
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        if(oneDayDataList.size() == 0) return;

        //Remove all day-views
        this.removeAllViews();
        
        int count = 0;
        for(CalendarDayItem one : oneDayDataList) {
            if(count % 7 == 0) {
                addView(weeks.get(count / 7));
            }
            CalendarDayView ov = dayViews.get(count);
            //캘린더 셋팅!!!!!!!
            ov.setDay(one);
            ov.refresh();
            count++;
        }

        //Set the weight-sum of LinearLayout to week counts
        this.setWeightSum(getChildCount());
    }

    //날짜 클릭할 때 메서드
    @Override
    public void onClick(View v) {
        CalendarDayView odv = (CalendarDayView) v;
        if (onClickDayListener != null) {
            this.onClickDayListener.onClick(odv);
        }
    }
}
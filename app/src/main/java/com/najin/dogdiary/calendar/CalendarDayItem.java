package com.najin.dogdiary.calendar;

import java.util.Calendar;

public class CalendarDayItem {
    
    Calendar cal;
    Boolean isMonth;

    public CalendarDayItem() {
        this.cal = Calendar.getInstance();
    }

//    public void setDay(int year, int month, int day) {
//        cal = Calendar.getInstance();
//        cal.set(year, month, day);
//    }

    public void setDay(Calendar cal, Boolean isMonth) {
        this.cal = (Calendar) cal.clone();
        this.isMonth = isMonth;
    }

    public Calendar getDay() {
        return cal;
    }

    public int get(int field) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return cal.get(field);
    }

    public Boolean getIsMonth() {
        return this.isMonth;
    }

    @Override
    public String toString() {
        return cal.get(Calendar.YEAR) + "년 " + cal.get(Calendar.MONTH) + "월 " + cal.get(Calendar.DATE) + "일";
    }
}

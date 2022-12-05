package com.najin.dogdiary.calendar;

public class CalendarMonthSet {

    private static final CalendarMonthSet calendarMonthSet = new CalendarMonthSet();

    private int year = 0;
    private int month = 0;

    //싱글톤 객체 읽기
    public static CalendarMonthSet getInstance() {
        return calendarMonthSet;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}

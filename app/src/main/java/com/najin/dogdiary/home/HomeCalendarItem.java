package com.najin.dogdiary.home;

public class HomeCalendarItem {

    private Integer day; //날짜
    private Integer level; //산책등급
    private boolean inMonth; //이번달인지

    public HomeCalendarItem(Integer day, Integer level, boolean inMonth) {
        this.day = day;
        this.level = level;
        this.inMonth = inMonth;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public boolean isInMonth() {
        return inMonth;
    }

    public void setInMonth(boolean inMonth) {
        this.inMonth = inMonth;
    }
}

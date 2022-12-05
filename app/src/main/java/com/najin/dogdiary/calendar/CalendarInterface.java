package com.najin.dogdiary.calendar;

import androidx.annotation.NonNull;

public interface CalendarInterface {
    //cell 클릭 이벤트
    void onClickDay(@NonNull CalendarDayView odv);
    //달 변경 이벤트
    void onMonthChanged(int year, int month);
    //현재 달력 위치
    int getCurrentPosition();
}




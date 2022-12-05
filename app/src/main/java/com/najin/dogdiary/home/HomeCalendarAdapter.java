package com.najin.dogdiary.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.najin.dogdiary.model.HomeVO;
import com.najin.dogdiary.model.WalkVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class HomeCalendarAdapter extends BaseAdapter {

    Context mContext;
    HomeCalendarItem[] items;

    //생성자 - context 셋팅
    HomeCalendarAdapter(Context context){
        super();
        mContext = context;
        init();
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HomeCalendarView view = new HomeCalendarView(mContext);

        view.setItem(items[position]);

        return view; //뷰 뿌려주기
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    public void init(){
        items = new HomeCalendarItem[7*6]; //아이템 크기 결정
        Calendar cal = Calendar.getInstance(); //Calendar 객체 가져오기

        //이번달
        cal.set(cal.DAY_OF_MONTH, 1); //1일로 설정
        int startDay = cal.get(Calendar.DAY_OF_WEEK); //현재 달 1일의 요일 (1: 일요일, . . . 7: 토요일)
        int lastDay = cal.getActualMaximum(Calendar.DATE); //달의 마지막 날짜
        //이전달
        cal.add(cal.MONTH,-1);
        int lastMonthDay = cal.getActualMaximum(Calendar.DATE);

        //이전달 날짜 셋팅
        for (int i=(startDay-2); i >= 0; i--) {
            items[i] = new HomeCalendarItem(lastMonthDay,0,false);
            lastMonthDay--;
        }
        //다음달 날짜 셋팅
        int nextdays = 1;
        int thisTimes = (lastDay+(startDay-1));
        for(int i=thisTimes; i < 42; i++){ /* 1일의 요일에 따라 시작위치 다르고 마지막 날짜까지 값 지정*/
            items[i] = new HomeCalendarItem(nextdays, 0,false);
            nextdays++;
        }

        ArrayList<Integer> walkMinutes = new ArrayList<>();
        Integer maxMinute = 0;
        Integer minMinute = 0;
        Integer diffMinute = 0;

        //이번달 산책 정보 셋팅
        if (HomeVO.getInstance().getDog().getId() != null) { //강아지가 등록되어있을때
            Integer walkCnt = HomeVO.getInstance().getWalkList().size();
            if (walkCnt != 0) { //강아지의 산책기록이 있을 때
                for (int i = 0; i < walkCnt; i++) {
                    walkMinutes.add(HomeVO.getInstance().getWalkList().get(i).getMinutes());
                }
                maxMinute = Collections.max(walkMinutes);
                minMinute = Collections.min(walkMinutes);
                diffMinute = maxMinute - minMinute;
            }
        }

        //이번달 날짜 셋팅
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-");
        String thisMonth = dateFormat.format(new Date());
        int thisdays = 1;
        for(int i=startDay-1; i<startDay-1+lastDay; i++){ /* 1일의 요일에 따라 시작위치 다르고 마지막 날짜까지 값 지정*/

            String thisDate;
            Integer minutes = 0;
            Integer level = 0;

            if (thisdays < 10 ) {
                thisDate = "0" + thisdays;
            } else {
                thisDate = "" + thisdays;
            }

            for (WalkVO walk : HomeVO.getInstance().getWalkList()) {
                if (walk.getDate().equals(thisMonth+thisDate)) {
                    minutes += walk.getMinutes();
                }
            }

            if (minutes != 0) {
                if (minutes >= (maxMinute - (1 * (diffMinute / 10)))) {
                    level= 5;
                } else if (minutes >= (maxMinute - (4 * (diffMinute / 10)))) {
                    level= 4;
                } else if (minutes >= (maxMinute - (6 * (diffMinute / 10)))) {
                    level= 3;
                } else if (minutes >= (maxMinute - (9 * (diffMinute / 10)))) {
                    level= 2;
                } else{
                    level= 1;
                }
            }
            items[i] = new HomeCalendarItem(thisdays, level,true);
            thisdays++;
        }

    }
}
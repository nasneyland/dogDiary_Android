package com.najin.dogdiary.calendar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.najin.dogdiary.R;

import java.util.Calendar;

public class CalendarFragment extends Fragment implements CalendarInterface {

    private ViewPager vPager;
    private CalendarPagerAdapter adapter;
    private MonthlyFragmentListener listener = null;
    private int monthYear = 0;
    private int monthMonth = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapter = new CalendarPagerAdapter(context, this);
    }

    @Override
    public void onDetach() {
        setOnMonthChangeListener(null);
        super.onDetach();
    }

    //calendar 인터페이스 override
    public interface MonthlyFragmentListener {
        void onChange(int year, int month);
        void onDayClick(CalendarDayView dayView);
    }

    public void setOnMonthChangeListener(MonthlyFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClickDay(@NonNull CalendarDayView odv) {
        if (listener != null) listener.onDayClick(odv);
    }

    @Override
    public void onMonthChanged(int year, int month) {
        if (listener != null) listener.onChange(year, month);
    }

    @Override
    public int getCurrentPosition() {
        return vPager.getCurrentItem();
    }

    public CalendarFragment() {
    }

    public static CalendarFragment newInstance(int year, int month) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (CalendarMonthSet.getInstance().getYear() != 0) {
            monthYear = CalendarMonthSet.getInstance().getYear();
            monthMonth = CalendarMonthSet.getInstance().getMonth();
        } else {
            if (getArguments() != null) {
                monthYear = getArguments().getInt("year");
                monthMonth = getArguments().getInt("month");
            } else {
                Calendar now = Calendar.getInstance();
                monthYear = now.get(Calendar.YEAR);
                monthMonth = now.get(Calendar.MONTH);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_monthly, container, false);
        vPager = v.findViewById(R.id.viewPager);
        vPager.setAdapter(adapter);
        vPager.setOnPageChangeListener(adapter);
        vPager.setCurrentItem(adapter.getPosition(monthYear, monthMonth));
        vPager.setOffscreenPageLimit(1);

        return v;
    }
}

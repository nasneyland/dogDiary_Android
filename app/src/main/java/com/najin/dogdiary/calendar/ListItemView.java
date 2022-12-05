package com.najin.dogdiary.calendar;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.najin.dogdiary.R;

public class ListItemView extends LinearLayout {

    View typeView;
    TextView detailView;

    public ListItemView(Context context) {
        super(context);
        init(context);
    }

    public ListItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.cell_calendar_list,this,true);

        typeView = findViewById(R.id.calendarListType);
        detailView = (TextView) findViewById(R.id.calendarListDetail);
    }

    public void setColor(String color) {
        typeView.setBackgroundColor(Color.parseColor(color));
    }

    public void setDetail(String detail) {
        detailView.setText(detail);
    }
}

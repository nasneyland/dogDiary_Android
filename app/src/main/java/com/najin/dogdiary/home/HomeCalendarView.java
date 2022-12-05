package com.najin.dogdiary.home;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.najin.dogdiary.R;

public class HomeCalendarView extends ConstraintLayout {

    TextView dayText;
    ImageView circle;
    Context mcontext;

    public HomeCalendarView(Context context) {
        super(context);
        mcontext = context;
        init(context);
    }

    public HomeCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.cell_home_grid, this, true);

        dayText = (TextView) findViewById(R.id.home_calendar_day);
        circle = (ImageView) findViewById(R.id.home_calendar_circle);
    }

    public void setItem(HomeCalendarItem item) {
        if (item.isInMonth()) {
            //이번달이면
            dayText.setText(item.getDay().toString());
            switch (item.getLevel()) {
                case 1:
                    circle.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.circle_home_calendar1));
                    break;
                case 2:
                    circle.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.circle_home_calendar2));
                    break;
                case 3:
                    circle.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.circle_home_calendar3));
                    break;
                case 4:
                    circle.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.circle_home_calendar4));
                    break;
                case 5:
                    circle.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.circle_home_calendar5));
                    break;
            }
        } else {
            //이번달이 아니면
            dayText.setText(item.getDay().toString());
            dayText.setTextColor(Color.parseColor("lightGray"));
            circle.setVisibility(GONE);
        }
    }
}
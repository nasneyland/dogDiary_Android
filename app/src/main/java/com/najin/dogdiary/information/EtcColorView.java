package com.najin.dogdiary.information;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.najin.dogdiary.R;

public class EtcColorView extends ConstraintLayout {

    View colorView;
    ImageView selectedColor;
    Context mcontext;

    public static Integer selected;

    public EtcColorView(Context context) {
        super(context);
        mcontext = context;
        init(context);
    }

    public EtcColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.cell_color_grid, this, true);

        colorView = findViewById(R.id.etc_color);
        selectedColor = (ImageView) findViewById(R.id.etc_selected_color);
    }

    public void setItem(String color, Integer position) {

        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(mcontext, R.drawable.circle_etc_color);
        drawable.setColor(Color.parseColor(color));
        colorView.setBackground(drawable);
        if (selected == position) {
            selectedColor.setVisibility(VISIBLE);
        } else {
            selectedColor.setVisibility(GONE);
        }

    }
}
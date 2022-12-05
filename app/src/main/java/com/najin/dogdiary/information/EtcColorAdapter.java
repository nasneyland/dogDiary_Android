package com.najin.dogdiary.information;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class EtcColorAdapter extends BaseAdapter {

    Context mContext;
    String[] colors;

    //생성자 - context 셋팅
    public EtcColorAdapter(Context context){
        super();
        mContext = context;
        init();
    }

    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EtcColorView view = new EtcColorView(mContext);

        view.setItem(colors[position],position);

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
        colors = new String[]{"#e9a799", "#db8438", "#c76026", "#963f2e", "#edc233",
                "#97d5e0", "#89bbb8", "#479a83", "#5c7148", "#4a5336",
                "#489ad8", "#bfa7f6", "#a7a3f8", "#8357ac", "#3c5066",
                "#bec3c7", "#999999", "#818b8d", "#798da5", "#495057"};
    }
}
package com.najin.dogdiary.support;

import android.app.AlertDialog;
import android.content.Context;

public class Common {
    //대표 url
    public static String URL = "#####";

    //서버 점검중
    public static void serverChecking(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("서버 연결 실패");
        builder.setMessage("인터넷 연결 상태를 확인해주세요.");
        builder.setPositiveButton("확인", null);
        AlertDialog dialog = builder.show();
    }
}

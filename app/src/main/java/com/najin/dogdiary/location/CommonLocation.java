package com.najin.dogdiary.location;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

import com.najin.dogdiary.model.HomeVO;

public class CommonLocation {
    public static final String KEY_REQUSTING_LOCATION_UPDATES = "LocationUpdateEnable";

    //알림창 내용
    public static String getLocationText(Location mLocation) {
        return mLocation == null ? "위치정보 수집 불가" : HomeVO.getInstance().getDog().getName() + " 산책하고 있어요!";
    }

    //알림창 제목
    public static CharSequence getLocationTitle(com.najin.dogdiary.location.MyBackgroundService myBackgroundService) {
        return String.format("멍멍한하루");
    }

    public static void setRequestingLocationUpdates(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUSTING_LOCATION_UPDATES, value)
                .apply();
    }

    public static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUSTING_LOCATION_UPDATES,false);
    }
}

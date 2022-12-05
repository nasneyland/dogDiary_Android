package com.najin.dogdiary.calendar;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.najin.dogdiary.model.CalendarVO;
import com.najin.dogdiary.model.EtcVO;
import com.najin.dogdiary.model.HeartVO;
import com.najin.dogdiary.model.HomeVO;
import com.najin.dogdiary.model.MoneyVO;
import com.najin.dogdiary.model.WalkVO;
import com.najin.dogdiary.model.WashVO;
import com.najin.dogdiary.model.WeightVO;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalendarSet {

    public static void setCalendar(Context context) {
        setWalk(context);
        setWash(context);
        setWeight(context);
        setHeart(context);
        setMoney(context);
        setEtc(context);
    }

    public static void setWalk(Context context) {
        String walkUrl = Common.URL+"/diary/walk/"+HomeVO.getInstance().getDog().getId();
        StringRequest dogRequest = new StringRequest(
                Request.Method.GET,
                walkUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        WalkVO[] list = gson.fromJson(response,WalkVO[].class);
                        List<WalkVO> alist = Arrays.asList(list);
                        ArrayList<WalkVO> arrayList = new ArrayList<WalkVO>();
                        arrayList.addAll(alist);
                        CalendarVO.getInstance().setWalkList(arrayList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        dogRequest.setShouldCache(false);
        AppHelper.requestQueue.add(dogRequest);
    }

    public static void setWash(Context context) {
        String washUrl = Common.URL+"/diary/wash/"+HomeVO.getInstance().getDog().getId();
        StringRequest dogRequest = new StringRequest(
                Request.Method.GET,
                washUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        WashVO[] list = gson.fromJson(response,WashVO[].class);
                        List<WashVO> alist = Arrays.asList(list);
                        ArrayList<WashVO> arrayList = new ArrayList<WashVO>();
                        arrayList.addAll(alist);
                        CalendarVO.getInstance().setWashList(arrayList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        dogRequest.setShouldCache(false);
        AppHelper.requestQueue.add(dogRequest);
    }

    public static void setWeight(Context context) {
        String weightUrl = Common.URL+"/diary/weight/"+HomeVO.getInstance().getDog().getId();
        StringRequest dogRequest = new StringRequest(
                Request.Method.GET,
                weightUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        WeightVO[] list = gson.fromJson(response,WeightVO[].class);
                        List<WeightVO> alist = Arrays.asList(list);
                        ArrayList<WeightVO> arrayList = new ArrayList<WeightVO>();
                        arrayList.addAll(alist);
                        CalendarVO.getInstance().setWeightList(arrayList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        dogRequest.setShouldCache(false);
        AppHelper.requestQueue.add(dogRequest);
    }

    public static void setHeart(Context context) {
        String heartUrl = Common.URL+"/diary/heart/"+HomeVO.getInstance().getDog().getId();
        StringRequest dogRequest = new StringRequest(
                Request.Method.GET,
                heartUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        HeartVO[] list = gson.fromJson(response,HeartVO[].class);
                        List<HeartVO> alist = Arrays.asList(list);
                        ArrayList<HeartVO> arrayList = new ArrayList<HeartVO>();
                        arrayList.addAll(alist);
                        CalendarVO.getInstance().setHeartList(arrayList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        dogRequest.setShouldCache(false);
        AppHelper.requestQueue.add(dogRequest);
    }

    public static void setMoney(Context context) {
        String moneyUrl = Common.URL+"/diary/money/"+HomeVO.getInstance().getDog().getId();
        StringRequest dogRequest = new StringRequest(
                Request.Method.GET,
                moneyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        MoneyVO[] list = gson.fromJson(response,MoneyVO[].class);
                        List<MoneyVO> alist = Arrays.asList(list);
                        ArrayList<MoneyVO> arrayList = new ArrayList<MoneyVO>();
                        arrayList.addAll(alist);
                        CalendarVO.getInstance().setMoneyList(arrayList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        dogRequest.setShouldCache(false);
        AppHelper.requestQueue.add(dogRequest);
    }

    public static void setEtc(Context context) {
        String etcUrl = Common.URL+"/diary/etc/"+HomeVO.getInstance().getDog().getId();
        StringRequest dogRequest = new StringRequest(
                Request.Method.GET,
                etcUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        EtcVO[] list = gson.fromJson(response,EtcVO[].class);
                        List<EtcVO> alist = Arrays.asList(list);
                        ArrayList<EtcVO> arrayList = new ArrayList<EtcVO>();
                        arrayList.addAll(alist);
                        CalendarVO.getInstance().setEtcList(arrayList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        dogRequest.setShouldCache(false);
        AppHelper.requestQueue.add(dogRequest);
    }
}

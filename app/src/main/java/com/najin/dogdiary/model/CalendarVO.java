package com.najin.dogdiary.model;

import java.util.ArrayList;

public class CalendarVO {

    private static final CalendarVO calendarVO = new CalendarVO();

    private ArrayList<WalkVO> walkList = new ArrayList<WalkVO>();
    private ArrayList<WashVO> washList = new ArrayList<WashVO>();
    private ArrayList<WeightVO> weightList = new ArrayList<WeightVO>();
    private ArrayList<HeartVO> heartList = new ArrayList<HeartVO>();
    private ArrayList<MoneyVO> moneyList = new ArrayList<MoneyVO>();
    private ArrayList<EtcVO> etcList = new ArrayList<EtcVO>();

    //싱글톤 객체 읽기
    public static CalendarVO getInstance() {
        return calendarVO;
    }

    //싱글톤 객체 파괴하기
    public void destroyCalendarVO() {
        this.walkList = new ArrayList<WalkVO>();
        this.washList = new ArrayList<WashVO>();
        this.weightList = new ArrayList<WeightVO>();
        this.heartList = new ArrayList<HeartVO>();
        this.moneyList = new ArrayList<MoneyVO>();
        this.etcList = new ArrayList<EtcVO>();
    }

    //get,set 메서드
    public ArrayList<WalkVO> getWalkList() {
        return walkList;
    }

    public void setWalkList(ArrayList<WalkVO> walkList) {
        this.walkList = walkList;
    }

    public ArrayList<WashVO> getWashList() {
        return washList;
    }

    public void setWashList(ArrayList<WashVO> washList) {
        this.washList = washList;
    }

    public ArrayList<WeightVO> getWeightList() {
        return weightList;
    }

    public void setWeightList(ArrayList<WeightVO> weightList) {
        this.weightList = weightList;
    }

    public ArrayList<HeartVO> getHeartList() {
        return heartList;
    }

    public void setHeartList(ArrayList<HeartVO> heartList) {
        this.heartList = heartList;
    }

    public ArrayList<MoneyVO> getMoneyList() {
        return moneyList;
    }

    public void setMoneyList(ArrayList<MoneyVO> moneyList) {
        this.moneyList = moneyList;
    }

    public ArrayList<EtcVO> getEtcList() {
        return etcList;
    }

    public void setEtcList(ArrayList<EtcVO> etcList) {
        this.etcList = etcList;
    }

    //toString
    @Override
    public String toString() {
        return "CalendarVO{" +
                "walkList=" + walkList +
                ", washList=" + washList +
                ", weightList=" + weightList +
                ", heartList=" + heartList +
                ", moneyList=" + moneyList +
                ", etcList=" + etcList +
                '}';
    }
}


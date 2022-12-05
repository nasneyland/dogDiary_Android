package com.najin.dogdiary.model;

import java.util.ArrayList;

public class HomeVO {

    private static final HomeVO homeVO = new HomeVO();

    private DogVO dog;

    private Integer lastWashDay;
    private Integer lastWeightDay;
    private Float lastWeight;
    private Integer lastHeartDay;
    private Integer totalMoney;

    private ArrayList<WalkVO> walkList = new ArrayList<WalkVO>();
    private ArrayList<WeightVO> weightChart = new ArrayList<WeightVO>();
    private ArrayList<MoneyVO> moneyList = new ArrayList<MoneyVO>();

    //싱글톤 객체 읽기
    public static HomeVO getInstance() {
        return homeVO;
    }

    //싱글톤 객체 값 설정하기
    public static void setInstance(HomeVO home) {
        homeVO.setDog(home.getDog());
        homeVO.setLastWashDay(home.getLastWashDay());
        homeVO.setLastWeightDay(home.getLastWeightDay());
        homeVO.setLastWeight(home.getLastWeight());
        homeVO.setLastHeartDay(home.getLastHeartDay());
        homeVO.setTotalMoney(home.getTotalMoney());
        homeVO.setWalkList(home.getWalkList());
        homeVO.setWeightChart(home.getWeightChart());
        homeVO.setMoneyList(home.getMoneyList());
    }

    //싱글톤 객체 파괴하기
    public void destroyHomeVO() {
        this.dog = null;
        this.lastWashDay = null;
        this.lastWeightDay = null;
        this.lastWeight = null;
        this.lastHeartDay = null;
        this.totalMoney = null;
        this.walkList = new ArrayList<WalkVO>();
        this.weightChart = new ArrayList<WeightVO>();
        this.moneyList = new ArrayList<MoneyVO>();
    }

    //get,set 메서드
    public static HomeVO getHomeVO() {
        return homeVO;
    }

    public DogVO getDog() {
        return dog;
    }

    public void setDog(DogVO dog) {
        this.dog = dog;
    }

    public Integer getLastWashDay() {
        return lastWashDay;
    }

    public void setLastWashDay(Integer lastWashDay) {
        this.lastWashDay = lastWashDay;
    }

    public Integer getLastWeightDay() {
        return lastWeightDay;
    }

    public void setLastWeightDay(Integer lastWeightDay) {
        this.lastWeightDay = lastWeightDay;
    }

    public Float getLastWeight() {
        return lastWeight;
    }

    public void setLastWeight(Float lastWeight) {
        this.lastWeight = lastWeight;
    }

    public Integer getLastHeartDay() {
        return lastHeartDay;
    }

    public void setLastHeartDay(Integer lastHeartDay) {
        this.lastHeartDay = lastHeartDay;
    }

    public Integer getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Integer totalMoney) {
        this.totalMoney = totalMoney;
    }

    public ArrayList<WalkVO> getWalkList() {
        return walkList;
    }

    public void setWalkList(ArrayList<WalkVO> walkList) {
        this.walkList = walkList;
    }

    public ArrayList<WeightVO> getWeightChart() {
        return weightChart;
    }

    public void setWeightChart(ArrayList<WeightVO> weightChart) {
        this.weightChart = weightChart;
    }

    public ArrayList<MoneyVO> getMoneyList() {
        return moneyList;
    }

    public void setMoneyList(ArrayList<MoneyVO> moneyList) {
        this.moneyList = moneyList;
    }

    //toString
    @Override
    public String toString() {
        return "HomeVO{" +
                "dog=" + dog +
                ", lastWashDay='" + lastWashDay + '\'' +
                ", lastWeightDay='" + lastWeightDay + '\'' +
                ", lastWeight='" + lastWeight + '\'' +
                ", lastHeartDay='" + lastHeartDay + '\'' +
                ", totalMoney='" + totalMoney + '\'' +
                ", walkList=" + walkList +
                ", weightChart=" + weightChart +
                ", moneyList=" + moneyList +
                '}';
    }
}


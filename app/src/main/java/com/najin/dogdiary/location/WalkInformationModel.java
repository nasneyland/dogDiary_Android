package com.najin.dogdiary.location;

import com.naver.maps.geometry.LatLng;

import java.util.ArrayList;

public class WalkInformationModel {

    private static final WalkInformationModel walkInformationModel = new WalkInformationModel();

    private LatLng beforeLocation;
    ArrayList<LatLng> coords = new ArrayList<>();
    double totalDistance = 0.0;

    //싱글톤 객체 읽기
    public static WalkInformationModel getInstance() {
        return walkInformationModel;
    }

    //싱글톤 객체 파괴하기
    public void destroyWalkInformationModel() {
        this.beforeLocation = null;
        this.coords = new ArrayList<>();;
        this.totalDistance = 0.0;
    }

    public LatLng getBeforeLocation() {
        return beforeLocation;
    }

    public void setBeforeLocation(LatLng beforeLocation) {
        this.beforeLocation = beforeLocation;
    }

    public ArrayList<LatLng> getCoords() {
        return coords;
    }

    public void addCoords(LatLng currentLocation) {
        coords.add(currentLocation);
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void addTotalDistance(double distance) {
        this.totalDistance += distance;
    }

    @Override
    public String toString() {
        return "WalkInformationModel{" +
                "beforeLocation=" + beforeLocation +
                ", coords=" + coords +
                ", totalDistance=" + totalDistance +
                '}';
    }
}

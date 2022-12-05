package com.najin.dogdiary.model;

public class WalkVO {

    private Integer id;
    private Integer dog_id;
    private String date;
    private String time;
    private Integer minutes;
    private double distance;

    //get,set 메서드
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDog_id() {
        return dog_id;
    }

    public void setDog_id(Integer dog_id) {
        this.dog_id = dog_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    //toString
    @Override
    public String toString() {
        return "WalkVO{" +
                "id=" + id +
                ", dog_id=" + dog_id +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", minutes=" + minutes +
                ", distance='" + distance + '\'' +
                '}';
    }
}

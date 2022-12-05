package com.najin.dogdiary.model;

public class WeightVO {

    private Integer id;
    private Integer dog_id;
    private String date;
    private float kg;

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

    public float getKg() {
        return kg;
    }

    public void setKg(float kg) {
        this.kg = kg;
    }

    //toString
    @Override
    public String toString() {
        return "WeightVO{" +
                "id=" + id +
                ", dog_id=" + dog_id +
                ", date='" + date + '\'' +
                ", kg='" + kg + '\'' +
                '}';
    }
}

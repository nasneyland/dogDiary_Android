package com.najin.dogdiary.calendar;

public class ListItem {

    Integer type;
    Integer id;
    String detail;
    String color;

    public ListItem(Integer type, Integer id, String color, String detail) {
        this.type = type;
        this.id = id;
        this.detail = detail;
        this.color = color;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "ListItem{" +
                "type=" + type +
                ", id=" + id +
                ", detail='" + detail + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}

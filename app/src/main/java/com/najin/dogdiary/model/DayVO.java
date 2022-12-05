package com.najin.dogdiary.model;

public class DayVO {

    private String color;
    private String title;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "DayVO{" +
                "color='" + color + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}

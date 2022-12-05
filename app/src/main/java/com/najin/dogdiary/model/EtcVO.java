package com.najin.dogdiary.model;

public class EtcVO {

    private Integer id;
    private Integer dog_id;
    private String date;
    private String color;
    private String title;
    private String content;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "EtcVO{" +
                "id=" + id +
                ", dog_id=" + dog_id +
                ", date='" + date + '\'' +
                ", color='" + color + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

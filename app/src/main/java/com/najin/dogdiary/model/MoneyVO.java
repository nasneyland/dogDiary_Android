package com.najin.dogdiary.model;

public class MoneyVO {

    private Integer id;
    private Integer dog_id;
    private String date;
    private String item;
    private Integer type;
    private Integer price;

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

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    //toString
    @Override
    public String toString() {
        return "MoneyVO{" +
                "id=" + id +
                ", dog_id=" + dog_id +
                ", date='" + date + '\'' +
                ", item='" + item + '\'' +
                ", type=" + type +
                ", price=" + price +
                '}';
    }
}

package com.najin.dogdiary.model;

public class DogVO {

    private String id;
    private String member_id;
    private String name;
    private Integer gender;
    private String birth;
    private String image;

    //get,set 메서드
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    //toString
    @Override
    public String toString() {
        return "DogVO{" +
                "id='" + id + '\'' +
                ", member_id='" + member_id + '\'' +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", birth='" + birth + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}

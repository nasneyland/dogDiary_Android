package com.najin.dogdiary.setting;

public class DogListItem {
    String id;
    String image;
    String name;
    Boolean selected;

    public DogListItem(String id, String image, String name, Boolean selected) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "DogListItem{" +
                "id='" + id + '\'' +
                ", image='" + image + '\'' +
                ", name='" + name + '\'' +
                ", selected=" + selected +
                '}';
    }
}

package com.example.prarthana.myapplication_restaurant;

public class Results {
    private String address;
    private String name;
    private String rating;
    private String isopen;
    private String icon;

    public String getAddress() {
        return address;
    }

    public String getIsopen() {
        return isopen;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setIsopen(String isopen) {
        this.isopen = isopen;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
}

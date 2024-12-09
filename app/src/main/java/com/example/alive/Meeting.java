package com.example.alive;

public class Meeting {
    private String name;
    private String dateTime;
    private String place;

    public Meeting(String name, String dateTime, String place) {
        this.name = name;
        this.dateTime = dateTime;
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getPlace() {
        return place;
    }
}


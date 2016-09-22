package com.expixel.pokeevents;

/**
 * Created by cellbody on 2016/9/21.
 */

public class Event {
    public String userImgUrl = "";
    public String userTeam = "";
    public String time = "";
    public Long timeInMillis = System.currentTimeMillis();
    public String eventType ="";
    public String place = "";
    public int playerAmount = 0;

    public Event() {

    }

    public Event(String userImgUrl, String userTeam, String time, Long timeInMillis, String eventType, String place, int playerAmount) {
        this.userImgUrl = userImgUrl;
        this.userTeam = userTeam;
        this.time = time;
        this.timeInMillis = timeInMillis;
        this.eventType = eventType;
        this.place = place;
        this.playerAmount = playerAmount;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public String getUserTeam() {
        return userTeam;
    }

    public String getTime() {
        return time;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPlace() {
        return place;
    }

    public int getPlayerAmount() {
        return playerAmount;
    }

    public Long getTimeInMillis() {
        return timeInMillis;
    }
}

package com.example.siddarthshikhar.yogaapp;

/**
 * Created by Siddarth Shikhar on 6/29/2015.
 */
public class YogaClass {
    String date,startTime,endTime,scheduleID,currStatus,venue;
    public YogaClass(String date,String startTime,String endTime,String scheduleID,String currStatus,String venue){
        this.date=date;
        this.startTime=startTime;
        this.endTime=endTime;
        this.scheduleID=scheduleID;
        this.currStatus=currStatus;
        this.venue=venue;
    }
}

package com.example.pc.caseproject;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;


public class AEDRequest{
    private String sender_token;
    private String sender_tel;

    private String sender_address;
    private String aed_address;
    private Location sender_location, aed_location;
    private Date date;

    public AEDRequest(){
        //현재 시간 자동 저장
        long now = System.currentTimeMillis();
        date = new Date(now);
    }

    public String getSender_token() {
        return sender_token;
    }

    public void setSender_token(String sender_token) {
        this.sender_token = sender_token;
    }

    public String getSender_tel() {
        return sender_tel;
    }

    public void setSender_tel(String sender_tel) {
        this.sender_tel = sender_tel;
    }

    public String getSender_address() {
        return sender_address;
    }

    public void setSender_address(String sender_address) {
        this.sender_address = sender_address;
    }

    public String getAed_address() {
        return aed_address;
    }

    public void setAed_address(String aed_address) {
        this.aed_address = aed_address;
    }

    public Location getSender_location() {
        return sender_location;
    }

    public void setSender_location(Location sender_location) {
        this.sender_location = sender_location;
    }

    public Location getAed_location() {
        return aed_location;
    }

    public void setAed_location(Location aed_location) {
        this.aed_location = aed_location;
    }

    public Date getDate() {
        return date;
    }
}

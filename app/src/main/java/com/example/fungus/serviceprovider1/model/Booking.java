package com.example.fungus.serviceprovider1.model;

public class Booking {
    String b_id;
    String date;
    String time;
    String s_id;
    String sp_id;
    String u_id;
    String status;

    public Booking() {
    }

    public Booking(String b_id, String date, String time, String s_id, String sp_id, String u_id, String status) {
        this.b_id = b_id;
        this.date = date;
        this.time = time;
        this.s_id = s_id;
        this.sp_id = sp_id;
        this.u_id = u_id;
        this.status = status;
    }

    public Booking(String b_id, String date, String time, String s_id, String sp_id, String u_id) {
        this.b_id = b_id;
        this.date = date;
        this.time = time;
        this.s_id = s_id;
        this.sp_id = sp_id;
        this.u_id = u_id;
    }

    public String getSp_id() {
        return sp_id;
    }

    public void setSp_id(String sp_id) {
        this.sp_id = sp_id;
    }

    public String getB_id() {
        return b_id;
    }

    public void setB_id(String b_id) {
        this.b_id = b_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package com.example.fungus.serviceprovider1.model;

import java.util.Comparator;

public class Service {
    String s_id;
    double s_latitude;
    double s_longitude;
    String s_name;
    String s_state;
    String address;
    String s_type;
    double distance;

    public Service() {
    }

    public Service(String s_id, double s_latitude, double s_longitude, String s_name, String s_state, String s_type) {
        this.s_id = s_id;
        this.s_latitude = s_latitude;
        this.s_longitude = s_longitude;
        this.s_name = s_name;
        this.s_state = s_state;
        this.s_type = s_type;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getS_type() {
        return s_type;
    }

    public void setS_type(String s_type) {
        this.s_type = s_type;
    }

    public String getS_state() {
        return s_state;
    }

    public void setS_state(String s_state) {
        this.s_state = s_state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public double getS_longitude() {
        return s_longitude;
    }

    public void setS_longitude(double s_longitude) {
        this.s_longitude = s_longitude;
    }

    public double getS_latitude() {
        return s_latitude;
    }

    public void setS_latitude(double s_latitude) {
        this.s_latitude = s_latitude;
    }

    public String getS_name() {
        return s_name;
    }

    public void setS_name(String s_name) {
        this.s_name = s_name;
    }

    public static Comparator<Service> serviceComparator = new Comparator<Service>() {

        public int compare(Service s1, Service s2) {
            double distance1 = s1.getDistance();
            double distance2 = s2.getDistance();
            double d = distance1-distance2;
            int a = (int)d;
            //ascending order
            return a;

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }};
}

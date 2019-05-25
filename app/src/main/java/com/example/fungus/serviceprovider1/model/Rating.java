package com.example.fungus.serviceprovider1.model;

public class Rating {
    private String ratingID;
    private String service_id;
    private Float ratingValue;

    public Rating() {
    }

    public Rating(String ratingID, String service_id, Float ratingValue) {
        this.ratingID = ratingID;
        this.service_id = service_id;
        this.ratingValue = ratingValue;
    }

    public String getRatingID() {
        return ratingID;
    }

    public void setRatingID(String ratingID) {
        this.ratingID = ratingID;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public Float getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Float ratingValue) {
        this.ratingValue = ratingValue;
    }
}

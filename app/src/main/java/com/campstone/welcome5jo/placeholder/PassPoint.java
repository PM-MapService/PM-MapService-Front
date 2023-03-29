package com.campstone.welcome5jo.placeholder;


public class PassPoint {
    static int num=1;
    public int id;
    public double lat;
    public double lon;
    public String type;
    public String description=null;

    public int getTurntype() {
        return turntype;
    }

    public void setTurntype(int turntype) {
        this.turntype = turntype;
    }

    public int turntype;
    public int seq;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public PassPoint(double lat, double lon, String type, int seq) {
        this.id = num+1;
        num++;
        this.lat = lat;
        this.lon = lon;
        this.type = type;
    }

}

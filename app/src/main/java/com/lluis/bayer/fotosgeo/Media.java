package com.lluis.bayer.fotosgeo;

/**
 * Created by 23878410v on 17/02/17.
 */

public class Media {
    String name;
    String type;
    String absolutePath;
    String lat;
    String lon;

    public Media() {
    }

    public Media(String name, String type, String absolutePath, String lat, String lon) {
        this.name = name;
        this.type = type;
        this.absolutePath = absolutePath;
        this.lat = lat;
        this.lon = lon;
    }

    /*public Double getDoubleLatitude(){
        return Double.parseDouble(lat);
    }

    public Double getDoubleLongitude(){
        return Double.parseDouble(lon);
    }*/

}

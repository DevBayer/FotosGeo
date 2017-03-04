package com.lluis.bayer.fotosgeo;

/**
 * Created by 23878410v on 17/02/17.
 */

public class Media {
    public String name;
    public String type;
    public String absolutePath;
    public String lat;
    public String lon;

    public Media() {
    }

    public Media(String name, String type, String absolutePath, String lat, String lon) {
        this.name = name;
        this.type = type;
        this.absolutePath = absolutePath;
        this.lat = lat;
        this.lon = lon;
    }
}

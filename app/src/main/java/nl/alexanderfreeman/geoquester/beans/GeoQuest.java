package nl.alexanderfreeman.geoquester.beans;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Alexander Freeman on 18-6-2017.
 */

public class GeoQuest {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String name;
    private Integer distance;
    private LatLng coordinates;
    private Uri image;
    private String code;
}

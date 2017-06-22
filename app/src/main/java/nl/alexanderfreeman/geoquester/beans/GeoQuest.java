package nl.alexanderfreeman.geoquester.beans;

import android.location.Location;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Alexander Freeman on 18-6-2017.
 */

public class GeoQuest implements Serializable {

    public GeoQuest() {
        this.description = "";
        this.name = "";
        this.longitude = 0;
        this.latitude = 0;
        this.image = "";
        this.code = "";
    }

    public GeoQuest(String name, String description, double longitude, double latitude, String image, String code) {
        this.name = name;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.image = image;
        this.code = code;
        this.distance = 0;
    }

    public Location getLocation() {
        Location l = new Location("");
        l.setLongitude(this.longitude);
        l.setLatitude(this.latitude);
        return l;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    private String name;
    private String description;
    private float distance;
    private double longitude;
    private double latitude;
    private String image;
    private String code;
}

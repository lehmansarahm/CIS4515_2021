package edu.temple.convoy;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Traveler implements Serializable {

    // data = JSON array with format:
    // {"username":"sarah5","firstname":"Sarah","lastname":"Lehman","latitude":"40.036","longitude":"-75.2203"}

    private String username;
    private String firstName;
    private String lastName;

    private double latitude;
    private double longitude;

    public Traveler(String username, double lat, double lon) {
        this.username = username;
        this.latitude = lat;
        this.longitude = lon;
    }

    public Traveler(String username, String firstName, String lastName, double lat, double lon) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.latitude = lat;
        this.longitude = lon;
    }


    // ============================================================================
    // ============================================================================


    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }


    // ============================================================================
    // ============================================================================


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return this.firstName;
    }


    // ============================================================================
    // ============================================================================


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return this.lastName;
    }


    // ============================================================================
    // ============================================================================


    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return this.latitude;
    }


    // ============================================================================
    // ============================================================================


    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    // ============================================================================
    // ============================================================================


    public String getName() {
        return (this.lastName + ", " + this.firstName);
    }

    public LatLng getLocation() {
        return new LatLng(this.latitude, this.longitude);
    }

    public String toString() {
        return ("Username: " + username
                + "\t First Name: " + firstName
                + "\t Last Name: " + lastName
                + "\t Latitude: " + latitude
                + "\t Longitude: " + longitude);
    }

}

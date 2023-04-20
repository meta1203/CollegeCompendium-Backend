package com.collegecompendium.backend.controllers;

import com.collegecompendium.backend.models.Location;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.Thread.sleep;

/**
 * This class is responsible for finding the longitude and latitude of a location.
 * It uses the OpenStreetMap API to find the location.
 *
 */
@Service
public class LocationController {
    private static LocationController instance = null;

    public static LocationController getInstance(){
        if(instance == null){
            instance = new LocationController();
        }
        return instance;
    }

    private String getResponse(String url) throws Exception{
        URL server = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) server.openConnection();

        connection.setRequestMethod("GET");

        if(connection.getResponseCode() != 200){
            // Response != OK, so we have an error
            throw new RuntimeException("Failed to talk to " + url + "\nHTTP error code : " + connection.getResponseCode());
        }

        BufferedReader response = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder msg = new StringBuilder();
        System.out.println("Response from server:");
        while ((line = response.readLine()) != null) {
            System.out.println(line);
            msg.append(line);
        }
        response.close();
        connection.disconnect();

        return msg.toString();
    }

    private LocationController(){
    }

    /**
     * Takes a string representation of a location and returns a Location object.
     * @param query Location we are attempting to find
     * @return Location object representing the locations longitude and latitude, or null if the location could not be found
     */
    public Location findLocation(String query) throws Exception {
        query = query.replace(" ", "+");
        String queryURL = "https://nominatim.openstreetmap.org/search?q=" + query + "&format=json" ; //+ "&format=json&polygon=1&addressdetails=1";
        String result;

        System.out.println(queryURL);
        result = getResponse(queryURL);

        System.out.println(result);

        String[] split = result.split(",");
        String lat = "";
        String lon = "";
        for (String s : split) {
            if(s.contains("\"lat\"")){
                lat = s;
            }
            if(s.contains("\"lon\"")){
                lon = s;
            }
            if(!lat.equals("") && !lon.equals("")){
                break;
            }
        }

        if(lat.equals("") || lon.equals("")){
            throw new RuntimeException("Could not find location: " + query);
        }

        lat = lat.substring(7, lat.length() - 1);
        lon = lon.substring(7, lon.length() - 1);

        System.out.println("Lat: " + lat);
        System.out.println("Lon: " + lon);

        return new Location(query, lat, lon);
    }
}

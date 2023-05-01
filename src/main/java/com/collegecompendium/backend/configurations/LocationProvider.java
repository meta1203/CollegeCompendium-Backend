package com.collegecompendium.backend.configurations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Service;

import com.collegecompendium.backend.models.Location;

import lombok.extern.log4j.Log4j2;

/**
 * This class is responsible for finding the longitude and latitude of a location.
 * It uses the OpenStreetMap API to find the location.
 *
 */
@Service
@Log4j2
public class LocationProvider {
	private static LocationProvider instance = null;

	public static LocationProvider getInstance(){
		if(instance == null){
			instance = new LocationProvider();
		}
		return instance;
	}

	private String getResponse(String url) {
		try {
			URL server = new URL(url);

			HttpURLConnection connection = (HttpURLConnection) server.openConnection();

			connection.setRequestMethod("GET");

			if(connection.getResponseCode() != 200){
				// Response != OK, so we have an error
				throw new IllegalStateException("Failed to talk to " + url + "\nHTTP error code : " + connection.getResponseCode());
			}

			BufferedReader response = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder msg = new StringBuilder();
			log.trace("Response from server:");
			while ((line = response.readLine()) != null) {
				log.trace(line);
				msg.append(line);
			}
			response.close();
			connection.disconnect();

			return msg.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private LocationProvider() {}

	/**
	 * Takes a string representation of a location and returns a Location object.
	 * @param query Location we are attempting to find
	 * @return Location object representing the locations longitude and latitude, or null if the location could not be found
	 */
	public Location findLocation(String input) {
		String query = input.replace(" ", "+");
		String queryURL = "https://nominatim.openstreetmap.org/search?q=" + query + "&format=json" ; //+ "&format=json&polygon=1&addressdetails=1";
		String result;

		log.debug("Location query: " + queryURL);
		result = getResponse(queryURL);

		log.trace(result);

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

		if(lat.equals("") || lon.equals("")) {
			throw new IllegalArgumentException("Could not find location: " + query);
		}

		lat = lat.substring(7, lat.length() - 1);
		lon = lon.substring(7, lon.length() - 1);

		log.debug("Lat: " + lat);
		log.debug("Lon: " + lon);

		return new Location(input, lat, lon);
	}
}

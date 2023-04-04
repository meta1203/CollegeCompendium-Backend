package com.collegecompendium.backend.models;

import java.util.Objects;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.ToString;

@Embeddable
@ToString
public class Location {
	@Column(length = 200)
	@Size(min = 0, max = 200)
	private String address;
	
	private int latitude;
	private int longitude;
	
	// this constructor & its annotations define how
	// Jackson will (de)serialize JSON representations
	@JsonCreator
	public Location(
			@JsonProperty("address") String address, 
			@JsonProperty("latitude") String latitude, 
			@JsonProperty("longitude") String longitude) {
		this.latitude = strToFixedPrecision(latitude);
		this.longitude = strToFixedPrecision(longitude);
	}
	
	public Location(String latitude, String longitude) {
		this("", latitude, longitude);
	}
	
	// JPA needs a default constructor, even if
	// we really don't want one. Thankfully, using
	// a private default constructor works just fine.
	@SuppressWarnings("unused")
	private Location() {
		this.address = "";
		this.latitude = 0;
		this.longitude = 0;
	}
	
	@JsonGetter
	public String getLatitude() {
		return fixedPrecisionToStr(latitude);
	}
	
	@JsonGetter
	public String getLongitude() {
		return fixedPrecisionToStr(longitude);
	}
	
	@JsonGetter
	public String getAddress() {
		return address;
	}
	
	public double distanceFrom(Location two) {
		double a = Double.parseDouble(fixedPrecisionToStr(this.latitude - two.latitude));
		double b = Double.parseDouble(fixedPrecisionToStr(this.longitude - two.longitude));
		// this is INCORRECT, as sphere topology is non-euclidean
		// BUT this should be close enough :^)
		return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
	}
	
	public static String fixedPrecisionToStr(int input) {
		StringBuilder temp = new StringBuilder(Integer.toString(input));
		// left-pad integer to 8 places
		while (temp.length() < 8) {
			temp.insert(0, '0');
		}
		temp.insert(temp.length() - 6, '.');
		return temp.toString();
	}
	
	public static int strToFixedPrecision(String input) {
		String[] split = input.split(Pattern.quote("."));
		if (split.length != 2) {
			throw new IllegalArgumentException("coordinate " + input + " is malformed.");
		}
		
		if (split[1].length() > 6) {
			split[1] = split[1].substring(0, 6);
		} else {
			while (split[1].length() < 6) {
				split[1] += "0";
			}
		}
		
		return Integer.parseInt(split[0] + split[1]);
	}
	
	@Override
	public boolean equals(Object o) {
		if (! (o instanceof Location))
			return false;
		
		Location o2 = (Location)o;
		return Objects.equals(this.address, o2.address) &&
				this.latitude == o2.latitude &&
				this.longitude == o2.longitude;
	}
}

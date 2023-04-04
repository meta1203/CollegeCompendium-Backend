package com.collegecompendium.backend.models;

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
	
	private int latitudeInt;
	private int longitudeInt;
	
	// this constructor & its annotations define how
	// Jackson will (de)serialize JSON representations
	@JsonCreator
	public Location(
			@JsonProperty("address") String address, 
			@JsonProperty("latitude") String latitude, 
			@JsonProperty("longitude") String longitude) {
		this.latitudeInt = strToFixedPrecision(latitude);
		this.longitudeInt = strToFixedPrecision(longitude);
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
		this.latitudeInt = 0;
		this.longitudeInt = 0;
	}
	
	@JsonGetter
	public String getLatitude() {
		return fixedPrecisionToStr(latitudeInt);
	}
	
	@JsonGetter
	public String getLongitude() {
		return fixedPrecisionToStr(longitudeInt);
	}
	
	@JsonGetter
	public String getAddress() {
		return address;
	}
	
	public double distanceFrom(Location two) {
		double a = Double.parseDouble(fixedPrecisionToStr(this.latitudeInt - two.latitudeInt));
		double b = Double.parseDouble(fixedPrecisionToStr(this.longitudeInt - two.longitudeInt));
		// this is INCORRECT, as sphere topology is non-euclidean
		// BUT this should be close enough :^)
		return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
	}
	
	private String fixedPrecisionToStr(int input) {
		StringBuilder temp = new StringBuilder(Integer.toString(input));
		// left-pad integer to 8 places
		while (temp.length() < 8) {
			temp.insert(0, '0');
		}
		temp.insert(temp.length() - 6, '.');
		return temp.toString();
	}
	
	private int strToFixedPrecision(String input) {
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
		return this.address.equals(o2.address) &&
				this.latitudeInt == o2.latitudeInt &&
				this.longitudeInt == o2.longitudeInt;
	}
}

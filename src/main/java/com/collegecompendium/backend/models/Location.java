package com.collegecompendium.backend.models;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Embeddable
@ToString
public class Location {
	@Column(length = 200)
	@Size(min = 0, max = 200)
	private String address;
	
	@Getter
	private double latitude;
	@Getter
	private double longitude;
	
	// this constructor & its annotations define how
	// Jackson will (de)serialize JSON representations
	@JsonCreator
	public Location(
			@JsonProperty("address") String address,
			@JsonProperty("latitude") double latitude,
			@JsonProperty("longitude") double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Location(double latitude, double longitude) {
		this("", latitude, longitude);
	}
	
	public Location(
			String address,
			String latitude,
			String longitude) {
		this.latitude = Double.parseDouble(latitude);
		this.longitude = Double.parseDouble(longitude);
	}
//	
//	public Location(String latitude, String longitude) {
//		this("", latitude, longitude);
//	}
	
	// JPA needs a default constructor, even if
	// we really don't want one. Thankfully, using
	// a private default constructor works just fine.
	@SuppressWarnings("unused")
	private Location() {
		this.address = "";
		this.latitude = 0d;
		this.longitude = 0d;
	}
	
	@JsonGetter
	public String getAddress() {
		return address;
	}
	
	public double distanceFrom(Location two) {
		double a = this.latitude - two.latitude;
		double b = this.longitude - two.longitude;
		// this is INCORRECT, as sphere topology is non-euclidean
		// BUT this should be close enough :^)
		return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
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
	
	@JsonIgnore
	public String getLatitudeFixedPrecision() {
		long r = (long)Math.round(latitude * 1000000);
		StringBuilder ret = new StringBuilder(String.valueOf(r));
		ret.insert(ret.length() - 6, '.');
		return ret.toString();
	}
	
	@JsonIgnore
	public String getLongitudeFixedPrecision() {
		long r = (long)Math.round(longitude * 1000000);
		StringBuilder ret = new StringBuilder(String.valueOf(r));
		ret.insert(ret.length() - 6, '.');
		return ret.toString();
	}
}

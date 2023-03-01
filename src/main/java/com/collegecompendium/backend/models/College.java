package com.collegecompendium.backend.models;

import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

// Lombok's Data annotation - https://projectlombok.org/features/Data
@Data
// JPA's Entity annotation - denotes this class as storable on the database
@Entity
// Jackson JSON annotation - instructs Jackson to prevent circular reference 
// loops by replacing nested whole objects with their ID instead
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class College {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@ElementCollection
	private List<Degree> degrees;
	
	// JSR 380 - ensures that this value cannot be empty

	@NotEmpty
	@NotNull
	private String name;

	@NotEmpty
	@NotNull
	@Size(min = 10, max = 120)
	@NotNull
	@Column(length = 120)
	private String location;

	@NotNull
	@Size(min = 12, max = 1024)
	@Column(length = 1024)
	private String url;

	@Min(100)
	@NotNull
	private Integer cost;
	
	@NotNull
	private String website;
	
	@NotNull
	private String appURL;
	
	public College(@JsonProperty("id") String id, @JsonProperty("name") String name,
			@JsonProperty("location") String location, @JsonProperty("url") String url,
			@JsonProperty("cost") Integer cost, @JsonProperty("majors") List<String> majors) {
		this.id = id;
		this.name = name;
		this.location = location;
		this.url = url;
		this.cost = cost;
		this.majors = majors;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}

package com.collegecompendium.backend.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
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

// Lombok's Data annotation - https://projectlombok.org/features/Data
@Data
// JPA's Entity annotation - denotes this class as storable on the database
@Entity
// Jackson JSON annotation - instructs Jackson to prevent circular reference 
// loops by replacing nested whole objects with their ID instead
@JsonIdentityInfo( 
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id")
public class College {
	// JPA - denotes this field as a unique id
	@Id
	// JPA - denotes this field as auto-generated
	// the GenerationType is how the id is generated
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@ElementCollection
	private List<Degree> degrees;
	
	// JSR 380 - ensures that this value cannot be empty
	@NotEmpty
	// JSR 380 - ensures that this value cannot be null
	@NotNull
	private String name;
	@NotEmpty
	@NotNull
	// JSR 380 - ensures that this value cannot be fewer than 10 characters,
	// nor more than 120 characters
	@Size(min = 10, max = 120)
	@NotNull
	// JPA - defines the size of the column as stored in the DB
	@Column(length = 120)
	private String location;
	
	@NotNull
	@Size(min = 12, max = 1024)
	@Column(length = 1024)
	private String url;
	
	// JSR 380 - ensures that this value cannot be lower than 100
	@Min(100)
	@NotNull
	// this value is in whole dollars
	private Integer cost;
}

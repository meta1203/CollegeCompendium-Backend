package com.collegecompendium.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * This class represents a college degree. It contains the name of the degree and the number of credits required to earn it.
 * Colleges can have multiple degrees.
 *
 * @author Kor
 */
@Data
@Entity
@Builder
public class Degree {
	public enum DegreeType {
		ASSOCIATE,
		BACHELOR,
		MASTER,
		DOCTORATE
	}

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@NotNull
	@ManyToOne
	private Major major;

	// These actually can be empty/null, if the school doesn't provide enough data
	private DegreeType degreeType;
	private int creditsRequired;

	public Degree(){
		// Empty constructor for JPA
	}

	public Degree(String id, Major major, DegreeType degreeType, int creditsRequired) {
		this.id = id;
		this.major = major;
		this.degreeType = degreeType;
		this.creditsRequired = creditsRequired;
	}

}


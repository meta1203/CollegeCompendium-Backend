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
/**
 * Represents a college degree, wraps the Major class and DegreeType enum
 */
public class Degree {
	/**
	 * Represents the type of degree, such as Associate's or Bachelor's
	 */
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

	/**
	 * Creates a new Degree object
	 * @param id The id of the degree (Generated automatically by saving to the database)
	 * @param major The major of the degree
	 * @param degreeType The type of degree
	 * @param creditsRequired The number of credits required to earn the degree
	 */
	public Degree(String id, Major major, DegreeType degreeType, int creditsRequired) {
		this.id = id;
		this.major = major;
		this.degreeType = degreeType;
		this.creditsRequired = creditsRequired;
	}

}


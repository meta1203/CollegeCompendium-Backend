package com.collegecompendium.backend.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
/*
 * Not sure if needed with bi-directional link to User - User is "owner"
 * @JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class,
 * property = "id")
 */

public class Student {
	
	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.UUID) private String id;
	 */
	
	@NotEmpty
	@Size(min = 8, max = 120)
	@Column(length = 120)
	private String highschool;
	
	@NotEmpty
	@Size(min = 8, max = 120)
	@Column(length = 120)
	private String college;
	
	@Null
	private int satScore;
	
	@Null
	private int actScore;
	
	@Null
	private List<String> activities;
	
	@NotEmpty
	@NotNull
	@Size(min = 10, max = 120)
	@Column(length = 20)
	private String location;
	
	//TODO Majors?
	
	//Creates foreign key in student table in "user_id" which is linked to a primary key in User (owner) 
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}

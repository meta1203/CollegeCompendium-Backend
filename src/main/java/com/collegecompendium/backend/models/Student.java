package com.collegecompendium.backend.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity

public class Student extends User {
	
	@NotNull
	@Column(length = 128)
    private String firstName;
	
	@NotNull
	@Column(length = 128)
    private String lastName;
	
	@Column(length = 16)
	@Size(max = 16)
	private String middleInitial;
	
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
}

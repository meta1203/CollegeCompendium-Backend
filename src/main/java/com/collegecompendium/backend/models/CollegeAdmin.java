package com.collegecompendium.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollegeAdmin extends User {
	
	@ManyToOne
	@JsonBackReference
	private College college;
	
	//NotNull and Column(nullable = false) not needed, will always be t/f
	private boolean approved;
}

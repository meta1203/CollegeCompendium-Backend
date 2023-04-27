package com.collegecompendium.backend.models;

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
public class CollegeAdmin extends User {
	
	@ManyToOne
	private College college;
	
	//NotNull and Column(nullable = false) not needed, will always be t/f
	private boolean approved;
}

package com.collegecompendium.backend.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
public class Student extends User {

	@Size(min = 0, max = 120)
	@Column(length = 120)
	private String highschool;

	@Size(min = 0, max = 120)
	@Column(length = 120)
	private String college;

	@Nullable
	private Integer satScore;

	@Nullable
	private Integer actScore;

	@NotNull
	@Builder.Default
	private List<String> activities = new ArrayList<>();

	@Embedded
	private Location location;

	@ManyToMany
//	@JoinTable(
//	        name = "student_favorite_college",
//	        joinColumns = @JoinColumn(name = "student_id"),
//	        inverseJoinColumns = @JoinColumn(name = "college_id")
//	    )
	private Set<College> favoriteColleges;
	
	public Set<College> getFavoriteColleges() {
		if (this.favoriteColleges == null)
			return new HashSet<>();
		return this.favoriteColleges;
	}
}

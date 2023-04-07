package com.collegecompendium.backend.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
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
}

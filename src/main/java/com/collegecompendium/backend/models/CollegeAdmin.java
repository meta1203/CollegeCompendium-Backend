package com.collegecompendium.backend.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
	
	@ManyToMany
	private List<Degree> degrees;
	
	@NotEmpty
	@NotNull
	private String name;	
	
	@NotNull
	@Size(min = 12, max = 1024)
	@Column(length = 1024)
	private String url;
	
	@Min(100)
	@NotNull
	private Integer cost;
	
	// TODO: Check why @Data isn't picking this up - Erik
	public List<Degree> getDegrees() {
	    return degrees;
	}
}

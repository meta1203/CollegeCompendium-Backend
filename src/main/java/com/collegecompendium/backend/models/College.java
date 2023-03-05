package com.collegecompendium.backend.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity

public class College extends User {
	
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
}

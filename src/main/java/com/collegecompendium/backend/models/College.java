package com.collegecompendium.backend.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class College {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@OneToMany(mappedBy = "college")
	private List<CollegeAdmin> collegeAdmins;
	
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
	private Integer inStateCost;
	
	@Min(100)
	@NotNull
	private Integer outStateCost;

	public List<Degree> getDegrees() {
	    return degrees;
	}
		
	@Size(min = 0, max = 120)
	@NotNull
	@Column(length = 120)
	private String location;
}

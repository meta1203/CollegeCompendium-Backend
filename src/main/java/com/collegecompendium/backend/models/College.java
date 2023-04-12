package com.collegecompendium.backend.models;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.URL;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class College {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@OneToMany(mappedBy = "college")
	@Builder.Default
	private List<CollegeAdmin> collegeAdmins = new ArrayList<CollegeAdmin>();
	
	@ManyToMany
	@Builder.Default
	private List<Degree> degrees = new ArrayList<Degree>();
	
	@NotEmpty
	@NotNull
	private String name;	
	
	/**
	 * @URL will check that protocols: http, https, ftp, file, and jar exist. If we need different protocols
	 * we can change the constraint mapping.
	 * https://docs.jboss.org/hibernate/stable/validator/api/org/hibernate/validator/constraints/URL.html
	 */
	@NotNull
	@Size(min = 12, max = 1024)
	@Column(length = 1024)
	@URL
	private String url;
	
	@Min(100)
	@NotNull
	private Integer inStateCost;
	
	@Min(100)
	@NotNull
	private Integer outStateCost;

	@Embedded
	private Location location;
	
	@ElementCollection
	@URL
	@Builder.Default
	private List<String> photos = new ArrayList<String>();
	
	@Size(max = 4096)
	@Column(length = 4096)
	private String description;

	@Size(min = 12, max = 16)
	@Column(length = 15)
	private String phoneNumber;

	private Integer popularity;
}

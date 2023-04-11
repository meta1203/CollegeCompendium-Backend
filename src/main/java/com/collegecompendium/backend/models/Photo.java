package com.collegecompendium.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Photo {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@NotNull
	@Column(length = 1024)
	private String url;
	
	@ManyToOne
	private College college;
}

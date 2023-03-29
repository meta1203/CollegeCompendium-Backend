package com.collegecompendium.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
// Lombok constructors
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@SuperBuilder
@Data
public abstract class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
    private String id;
	
	@NotNull
	@Column(length = 256, unique = true)
	private String auth0Id;
	
	@NotNull
	@Email
	@Column(length = 1024)
    private String email;
	
	@NotNull
	@NotEmpty
	@Column(length = 128, unique = true)
	private String username;
	
	@NotNull
	@Column(length = 128)
    private String firstName;
	
	@NotNull
	@Column(length = 128)
    private String lastName;
	
	@Column(length = 16)
	@Size(max = 16)
	private String middleInitial;
	
}

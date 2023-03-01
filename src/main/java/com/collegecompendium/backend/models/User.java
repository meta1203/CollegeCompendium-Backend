package com.collegecompendium.backend.models;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Builder.Default;

@Entity
@Builder
// `user` is an H2 reserved word, so force this
// table to be called `users` instead
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Default
    private String id = UUID.randomUUID().toString();
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
	@NotNull
    private String location;
    
    public User() {
    	this.id = UUID.randomUUID().toString();
    	this.email = "";
    	this.firstName = "";
    	this.lastName = "";
    	this.location = "";
    }
    
    public User(String id, @NotNull @Email String email, @NotNull @NotEmpty String username, @NotNull String firstName,
			@NotNull String lastName, @Size(max = 16) String middleInitial, @NotNull String location) {
		this.id = id;
		this.email = email;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleInitial = middleInitial;
		this.location = location;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMiddleInitial() {
		return middleInitial;
	}

	public void setMiddleInitial(String middleInitial) {
		this.middleInitial = middleInitial;
	}
}

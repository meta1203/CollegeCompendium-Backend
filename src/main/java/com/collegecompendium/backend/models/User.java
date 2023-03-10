package com.collegecompendium.backend.models;

import java.net.URI;
import java.util.HashMap;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestTemplate;

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
	
	@Size(min = 0, max = 120)
	@NotNull
	@Column(length = 120)
	private String location;
	
	// gets everything auth0 knows about the given JWT token
	// returns a HashMap because there's no documentation I can find
	// that provides a fixed model 
	public static HashMap<String, String> getAuth0(Jwt token) {
		RestTemplate rt = new RestTemplate();
		
		RequestEntity<Void> request = RequestEntity
				.get(URI.create("https://dev-yrjc5x2ila2084mu.us.auth0.com/userinfo"))
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token.getTokenValue()).build();
		
		ResponseEntity<HashMap<String, String>> response = rt.exchange(request, new ParameterizedTypeReference<HashMap<String, String>>() {});
		
		if (response.getStatusCode().is2xxSuccessful()) return response.getBody();
		return null;
	}
}

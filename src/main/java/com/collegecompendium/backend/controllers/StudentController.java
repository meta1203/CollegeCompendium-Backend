package com.collegecompendium.backend.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.collegecompendium.backend.configurations.Auth0Provider;
import com.collegecompendium.backend.models.Student;
import com.collegecompendium.backend.repositories.StudentRepository;

import jakarta.servlet.http.HttpServletResponse;
@RestController
// Web browser visibility
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private Auth0Provider auth0Provider;
	
	@PostMapping("/student")
	public Student createNewStudent(@RequestBody Student input, @AuthenticationPrincipal Jwt token, HttpServletResponse response) {
		Student result = studentRepository.findDistinctByAuth0Id(token.getSubject());

		// Checking if it already exists, if so then do not let them create a new one.
		if(result != null) {
			response.setStatus(400);
			return null;
		}

		// Setting the student's auth0Id to the generated jwt token.
		input.setAuth0Id(token.getSubject());

		// Setting the ID to null will allow the User Model to generate the information.
		input.setId(null);

		// Saving the student to the db
		Student output = studentRepository.save(input);
		
		// add student permission to auth0
		auth0Provider.addPermissionToUser(output, "student");
		return output;
	}

	
	@GetMapping("/student/{id}")
	public Student getStudentById(@PathVariable String id, HttpServletResponse response) {
		Optional<Student> query = studentRepository.findById(id);
		
		if(query.isEmpty()) {
			response.setStatus(404);
			return null;
		}
		return query.get();
	}
	
	@GetMapping("/student")
	public Student getCallingStudent(@AuthenticationPrincipal Jwt token, HttpServletResponse response) {
		Student result = studentRepository.findDistinctByAuth0Id(token.getSubject());
		if(result == null) {
			response.setStatus(404);
			Student newUser = createJwtStudent(token);
			return newUser;
		} else {
			response.setStatus(200);
			return result;
		}
	}
	


	@PutMapping("/student")
	public Student modifyStudent(
		@RequestBody Student input, 
		@AuthenticationPrincipal Jwt token, 
		HttpServletResponse response) {
		
		Student result = studentRepository.findDistinctByAuth0Id(token.getSubject());
		
		// if the student does not exist, sending back 404.
		if(result == null) {
			response.setStatus(404);
			return null;
		}
		
		// if the token does not belong to the caller, return 403
		if (! token.getSubject().equals(input.getAuth0Id())) {
			response.setStatus(403);
			return null;
		}
		
		if (! input.getAuth0Id().equals(result.getAuth0Id())) {
			response.setStatus(403);
			return null;
		}
		
		// if the id is different, overwrite given id
		if (! input.getId().equals(result.getId())) {
			input.setId(result.getId());
		}
		
		// update the db with the new student object
		input = studentRepository.save(input);
		return input;
	}
	
	@DeleteMapping("/student/{id}")
	public boolean deleteStudent(@PathVariable String id, HttpServletResponse response) {
		Optional<Student> query = studentRepository.findById(id);
		if(query.isEmpty()) {
			response.setStatus(404);
			return false;
		}
		studentRepository.delete(query.get());
		return true;
	}
	
	//Possible "get students by degree(s)"?
	
	private Student createJwtStudent(Jwt token) {
		
	    Student student = new Student();
	    student.setEmail(token.getClaim("email"));
	    student.setUsername(token.getClaim("nickname"));
	    student.setLocation("");
	    student.setFirstName(token.getClaim("given_name"));
	    student.setLastName(token.getClaim("family_name"));
	    student.setMiddleInitial("");
	    student.setHighschool("");
	    student.setCollege("");
	    student.setSatScore(null);
	    student.setActScore(null);
	    return student;
	}
}

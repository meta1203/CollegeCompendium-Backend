package com.collegecompendium.backend.controllers;

import java.util.Optional;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.collegecompendium.backend.models.Student;
import com.collegecompendium.backend.repositories.StudentRepository;

import jakarta.servlet.http.HttpServletResponse;
@RestController
// Web browser visibility
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {
	
	@Autowired
	private StudentRepository studentRepository;
	
	@PostMapping("/student")
	public Student createNewStudent(@RequestAttribute Student input, HttpServletResponse respone) {
		input.setId(null);
		
		Student output = studentRepository.save(input);
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
		// If the student does not exist, sending back 404. 
		if(result == null) {
			response.setStatus(403);
			return null;
		}
		if (! token.getSubject().equals(input.getAuth0Id())) {
			reponse.setStatus(403);
			return null;
		}
		if (! input.getAuth0Id().equals(result.getAuth0Id())) {
			response.setStatus(403);
			return null;
		}
		if (! input.getId().equals(result.getId())) {
			response.setStatus(403);
			return null;
		}
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

package com.collegecompendium.backend.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@PutMapping("/college/{id}")
	public Student updateStudent(@RequestBody Student input, HttpServletResponse response) {
		Optional<Student> query = studentRepository.findById(input.getId());
		if(query.isEmpty()) {
			response.setStatus(404);
			return null;
		}
		Student student = query.get();
		return student;
	}
	
	@DeleteMapping("/college/{id}")
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
}

package com.collegecompendium.backend.controllers;

import java.util.Optional;
import java.util.Set;

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

import com.collegecompendium.backend.configurations.LocationProvider;
import com.collegecompendium.backend.configurations.UserProvider;
import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.Student;
import com.collegecompendium.backend.repositories.CollegeRepository;
import com.collegecompendium.backend.repositories.StudentRepository;

import jakarta.servlet.http.HttpServletResponse;
@RestController
// Web browser visibility
@CrossOrigin(origins = {"http://localhost:3000", "https://cse326.meta1203.com/"})
public class StudentController {
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private CollegeRepository collegeRepository;
	
	@Autowired
	private UserProvider userProvider;
	@Autowired
	private LocationProvider locationProvider;
	
	@PostMapping("/student")
	public Student createNewStudent(@RequestBody Student input, @AuthenticationPrincipal Jwt token, HttpServletResponse response) {
		// Checking if user already exists, if so then do not let them create a new one.
		if(userProvider.getUserForToken(token) != null) {
			response.setStatus(400);
			return null;
		}

		// Setting the student's auth0Id to the generated jwt token.
		input.setAuth0Id(token.getSubject());

		// Setting the ID to null will allow the User Model to generate the information.
		input.setId(null);
		
		// Look up student location
		input.setLocation(
				locationProvider.findLocation(
						input.getLocation().getAddress()
						)
				);

		// Saving the student to the db
		Student output = studentRepository.save(input);
		
		// add student permission to auth0
		userProvider.addPermissionToUser(output, "student");
		return output;
	}

	
	@GetMapping("/student/{id}")
	public Student getStudentById(
			@PathVariable String id,
			@AuthenticationPrincipal Jwt token,
			HttpServletResponse response
			) {
		Optional<Student> query = studentRepository.findById(id);
		
		if(query.isEmpty()) {
			response.setStatus(404);
			return null;
		}
		
		Student ret = query.get();
		if (! ret.getAuth0Id().equals(token.getSubject())) {
			response.setStatus(403);
			return null;
		}
		
		return ret;
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
		
		input.setLocation(locationProvider.findLocation(input.getLocation().getAddress()));
		
		// update the db with the new student object
		input = studentRepository.save(input);
		return input;
	}
	
	//Student Favorites a College by Id
	@PutMapping("/student/favorite/{collegeId}")
	public Student addFavoriteCollege(
	        @PathVariable String collegeId,
	        @AuthenticationPrincipal Jwt token,
	        HttpServletResponse response) {
		
		
		Student student = studentRepository.findDistinctByAuth0Id(token.getSubject());
		Optional<College> collegeQuery = collegeRepository.findById(collegeId);
		
		if (student == null) {
			response.setStatus(400);
			return null;
		}
		
		if (collegeQuery.isEmpty()) {
			response.setStatus(404);
			return null;
		}
		
		College college = collegeQuery.get();
		
		boolean added = student.getFavoriteColleges().add(college);
	
		if (!added) {
			// college already exists
			response.setStatus(200);
		} else {
			student = studentRepository.save(student);
			response.setStatus(200);
		}
		
		return student;
	}
	
	@DeleteMapping("/student/favorite/{collegeId}")
	public boolean removeFavoriteCollege(
	        @PathVariable String collegeId,
	        @AuthenticationPrincipal Jwt token,
	        HttpServletResponse response
	        ) {
	    Student student = studentRepository.findDistinctByAuth0Id(token.getSubject());
	    Optional<College> collegeQuery = collegeRepository.findById(collegeId);
	    
	    if (student == null) {
	    	response.setStatus(400);
	    	return false;
	    }

	    College college = collegeQuery.get();

	    boolean removed = student.getFavoriteColleges().remove(college);
	        studentRepository.save(student);
	        if(removed) {
	        response.setStatus(200);
	        return removed;
	        }
	        response.setStatus(200);
	        return removed;
	}
	
	@DeleteMapping("/student/{id}")
	public boolean deleteStudent(
			@PathVariable String id,
			@AuthenticationPrincipal Jwt token,
			HttpServletResponse response
			) {
		Optional<Student> query = studentRepository.findById(id);
		if(query.isEmpty()) {
			response.setStatus(404);
			return false;
		}
		
		Student ret = query.get();
		if (! ret.getAuth0Id().equals(token.getSubject())) {
			response.setStatus(403);
			return false;
		}
		
		studentRepository.delete(ret);
		return true;
	}
	
	//Possible "get students by degree(s)"?
	
	private Student createJwtStudent(Jwt token) {
		
	    Student student = new Student();
	    student.setEmail(token.getClaim("email"));
	    student.setUsername(token.getClaim("nickname"));
	    student.setLocation(null);
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

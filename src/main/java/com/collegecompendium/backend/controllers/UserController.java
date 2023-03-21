package com.collegecompendium.backend.controllers;

import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.Student;
import com.collegecompendium.backend.models.User;
import com.collegecompendium.backend.repositories.CollegeRepository;
import com.collegecompendium.backend.repositories.StudentRepository;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private CollegeRepository collegeRepository;
	@Autowired
	private Function<Jwt, Map<String,String>> tokenToAuth0Data;

    @PostMapping("/test/user")
    public User addUser(@RequestBody User user) {
    	// TODO: validate input
        // user = userRepository.save(user);
        return user;
    }
    
    // used to test endpoint security
    @GetMapping("/test")
    public Object pingAuth(@AuthenticationPrincipal Jwt token) {
    	//return token;
    	return tokenToAuth0Data.apply(token);
    }
    
    @GetMapping("/user")
    public User getUser(
    		@AuthenticationPrincipal Jwt token,
    		HttpServletResponse response
    		) {
    	
    	Student student = studentRepository.findDistinctByAuth0Id(token.getSubject());
    	if (student != null) {
    		return student;
    	}
    	
    	College college = collegeRepository.findDistinctByAuth0Id(token.getSubject());
    	if (college != null) {
    		return college;
    	}
    	
    	Map<String, String> auth0Data = tokenToAuth0Data.apply(token);
    	student = Student.builder()
    			.email(auth0Data.get("email"))
    			.firstName(auth0Data.get("given_name"))
    			.lastName(auth0Data.get("family_name"))
    			.username(auth0Data.get("nickname"))
    			.id(null)
    			.build();
    	
    	response.setStatus(404);
    	return student;
    }
}

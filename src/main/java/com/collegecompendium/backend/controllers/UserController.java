package com.collegecompendium.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.collegecompendium.backend.models.User;
import com.collegecompendium.backend.repositories.UserRepository;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {
	@Autowired
    private UserRepository userRepository;

    @PostMapping("/user")
    public User addUser(@RequestBody User user) {
    	// TODO: validate input
        user = userRepository.save(user);
        return user;
    }
    
    // used to test endpoint security
    @GetMapping("/student")
    public Object pingAuth(@AuthenticationPrincipal Jwt token) {
    	//return token;
    	return User.getAuth0(token);
    }
}

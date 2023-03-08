package com.collegecompendium.backend.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.collegecompendium.backend.models.User;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {

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
    	return User.getAuth0(token);
    }
}

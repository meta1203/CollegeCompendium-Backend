package com.collegecompendium.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.Major;
import com.collegecompendium.backend.repositories.CollegeRepository;
import com.collegecompendium.backend.repositories.MajorRepository;

@CrossOrigin(origins = {"http://localhost:3000", "https://cse326.meta1203.com/"})
@RestController
@RequestMapping(path = "/superAdmin")
public class SuperAdminController {
	@Autowired
	private MajorRepository majorRepository;
	@Autowired
	private CollegeRepository collegeRepository;
	
	@GetMapping("/test")
	public Jwt testSuperAdmin(@AuthenticationPrincipal Jwt token) {
		return token;
	}
	
	@PostMapping("/major")
	public Major addMajor(@RequestBody Major toAdd) {
		toAdd = majorRepository.save(toAdd);
		return toAdd;
	}
	
	@PutMapping("/major")
	public Major updateMajor(@RequestBody Major toAdd) {
		if (toAdd.getId() == null)
			return null;
		if (majorRepository.findById(toAdd.getId()).isEmpty())
			return null;
		toAdd = majorRepository.save(toAdd);
		return toAdd;
	}
	
	@PostMapping("/college")
	public College addCollege(@RequestBody College toAdd) {
		toAdd = collegeRepository.save(toAdd);
		return toAdd;
	}
	
	@PutMapping("/college")
	public College updateCollege(@RequestBody College toAdd) {
		if (toAdd.getId() == null)
			return null;
		if (collegeRepository.findById(toAdd.getId()).isEmpty())
			return null;
		toAdd = collegeRepository.save(toAdd);
		return toAdd;
	}
}

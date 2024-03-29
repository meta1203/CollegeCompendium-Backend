package com.collegecompendium.backend.controllers;

import java.util.LinkedList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collegecompendium.backend.configurations.LocationProvider;
import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.CollegeAdmin;
import com.collegecompendium.backend.models.Major;
import com.collegecompendium.backend.repositories.CollegeAdminRepository;
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
	@Autowired
	private CollegeAdminRepository collegeAdminRepository;
	@Autowired
	private LocationProvider locationProvider;
	
	@GetMapping("/test")
	public Jwt testSuperAdmin(@AuthenticationPrincipal Jwt token) {
		return token;
	}
	
	@GetMapping("/majors")
	public List<Major> getAllMajors() {
		LinkedList<Major> ret = new LinkedList<>();
		for (Major m : majorRepository.findAll()) {
			ret.addLast(m);
		}
		return ret;
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
	
	@GetMapping("/colleges")
	public List<College> getAllColleges() {
		LinkedList<College> ret = new LinkedList<>();
		for (College c : collegeRepository.findAll()) {
			ret.addLast(c);
		}
		return ret;
	}
	
	@PostMapping("/college")
	public College addCollege(@RequestBody College toAdd) {
		toAdd.setLocation(locationProvider.findLocation(toAdd.getLocation().getAddress()));
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
	
	@DeleteMapping("/college/{id}")
	public boolean deleteCollege(@PathVariable String id) {
		College c = collegeRepository.findById(id).orElse(null);
		if (c == null)
			return false;
		collegeRepository.delete(c);
		return true;
	}
	
	@PutMapping("/collegeAdmin/approve/{email}")
	public CollegeAdmin approveCollegeAdmin(@PathVariable String email) {
		CollegeAdmin ca = collegeAdminRepository.findDistinctByEmail(email);
		if (ca == null) return null;
		if (! ca.isApproved()) {
			ca.setApproved(true);
			collegeAdminRepository.save(ca);
		}
		return ca;
	}
}

package com.collegecompendium.backend.controllers;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.CollegeAdmin;
import com.collegecompendium.backend.models.Major;
import com.collegecompendium.backend.repositories.CollegeAdminRepository;
import com.collegecompendium.backend.repositories.CollegeRepository;
import com.collegecompendium.backend.repositories.MajorRepository;

@CrossOrigin(origins = {"http://localhost:3000", "https://cse326.meta1203.com/"})
@RestController
@RequestMapping(path = "/superAdmin")
/**
 * REST endpoint controller for super admin
 */
public class SuperAdminController {
	@Autowired
	private MajorRepository majorRepository;
	@Autowired
	private CollegeRepository collegeRepository;
	@Autowired
	private CollegeAdminRepository collegeAdminRepository;
	
	@GetMapping("/test")
	/**
	 * Test function for super admin
	 * @param token the JWT token of the super admin
	 * @return the JWT token of the super admin
	 */
	public Jwt testSuperAdmin(@AuthenticationPrincipal Jwt token) {
		return token;
	}
	
	@GetMapping("/majors")
	/**
	 * Gets a list of all majors
	 * @return a list of all majors
	 */
	public List<Major> getAllMajors() {
		LinkedList<Major> ret = new LinkedList<>();
		for (Major m : majorRepository.findAll()) {
			ret.addLast(m);
		}
		return ret;
	}
	
	@PostMapping("/major")
	/**
	 * Adds a major to the database
	 * @param toAdd the major to add
	 * @return the major that was added
	 */
	public Major addMajor(@RequestBody Major toAdd) {
		toAdd = majorRepository.save(toAdd);
		return toAdd;
	}
	
	@PutMapping("/major")
	/**
	 * Updates a major in the database
	 * @param toAdd the major to update
	 * @return the major that was updated
	 */
	public Major updateMajor(@RequestBody Major toAdd) {
		if (toAdd.getId() == null)
			return null;
		if (majorRepository.findById(toAdd.getId()).isEmpty())
			return null;
		toAdd = majorRepository.save(toAdd);
		return toAdd;
	}
	
	@GetMapping("/colleges")
	/**
	 * Gets a list of all colleges
	 * @return a list of all colleges
	 */
	public List<College> getAllColleges() {
		LinkedList<College> ret = new LinkedList<>();
		for (College c : collegeRepository.findAll()) {
			ret.addLast(c);
		}
		return ret;
	}
	
	@PostMapping("/college")
	/**
	 * Adds a college to the database
	 * @param toAdd the college to add
	 * @return the college that was added
	 */
	public College addCollege(@RequestBody College toAdd) {
		toAdd = collegeRepository.save(toAdd);
		return toAdd;
	}
	
	@PutMapping("/college")
	/**
	 * Updates a college in the database
	 * @param toAdd the college to update
	 * @return the college that was updated
	 */
	public College updateCollege(@RequestBody College toAdd) {
		if (toAdd.getId() == null)
			return null;
		if (collegeRepository.findById(toAdd.getId()).isEmpty())
			return null;
		toAdd = collegeRepository.save(toAdd);
		return toAdd;
	}
	
	@PutMapping("/collegeAdmin/approve/{email}")
	/**
	 * Approves a college admin
	 * @param email the email of the college admin to approve
	 * @return the college admin that was approved
	 */
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

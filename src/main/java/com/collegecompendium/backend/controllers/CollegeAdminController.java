package com.collegecompendium.backend.controllers;

import java.util.List;
import java.util.Optional;

import com.collegecompendium.backend.models.Degree;
import com.collegecompendium.backend.repositories.DegreeRepository;
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

import com.collegecompendium.backend.configurations.UserProvider;
import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.CollegeAdmin;
import com.collegecompendium.backend.repositories.CollegeAdminRepository;
import com.collegecompendium.backend.repositories.CollegeRepository;

import jakarta.servlet.http.HttpServletResponse;

// Spring annotation - marks this class as providing REST API endpoints
@RestController
// this has to be put on every controller we want to access from a
// web browser (CORS my beloathed)
@CrossOrigin(origins = {"http://localhost:3000", "https://cse326.meta1203.com/"})
public class CollegeAdminController {
	// Spring annotation - injects a defined Spring Bean here
	// These beans can be implicit (eg. @Component on a class/interface
	// that extends a special type of class/interface) or explicit
	// (@Bean on a function that returns a value)
	@Autowired
	private CollegeAdminRepository collegeAdminRepository;

	@Autowired
	private CollegeRepository collegeRepository;

	@Autowired
	private UserProvider userProvider;

	@Autowired
	private DegreeRepository degreeRepository;

	// Spring annotation - defines a REST endpoint to be handled by
	// the annotated function. function arguments can be annotated
	// for input (ex @RequestBody) or implicit (eg. HttpServletResponse)
	// TEST

	/*
	// This annotation is for the POST HTTP method
	@PostMapping("/collegeAdmin")
	public CollegeAdmin createNewCollegeAdmin(
			@RequestBody CollegeAdmin input, 
			HttpServletResponse response) {
		// set the ID to null so it can be auto-generated
		input.setId(null);
		// save the college to the database, saving the backing
		// object to a new `output` variable
		CollegeAdmin output = collegeAdminRepository.save(input);
		// send this new object back to the client via JSON
		// Spring automagically (un)marshals JSON to and from
		// classes for you
		return output;
	}
	 */

	/**
	 * Creates a new college admin object and adds to the database.
	 * @param input The college object to be created
	 * @param token Auth0 token (idk if this works)
	 * @param response Http response object.
	 * @return the College object saved to database.
	 */
	@PostMapping("/collegeAdmin")
	public CollegeAdmin createNewCollegeAdmin(
			@RequestBody CollegeAdmin input, 
			@AuthenticationPrincipal Jwt token, 
			HttpServletResponse response 
			) {
		// If Auth0 ID already exists, return.
		if(userProvider.getUserForToken(token) != null){
			response.setStatus(400);
			return null;
		}

		// Set Auth0 ID of the new college. Must be used for verification later!!!
		input.setAuth0Id(token.getSubject());
		// set approved to false for all new CollegeAdmins
		input.setApproved(false);
		// Set Database ID to null to be autogenerated
		input.setId(null);
		CollegeAdmin output = collegeAdminRepository.save(input);

		// add collegeAdmin permission to auth0
		userProvider.addPermissionToUser(input, "collegeAdmin");

		return output;
	}
	@PostMapping("/collegeAdmin/approve/{email}")
	public CollegeAdmin approveCollegeAdmin(
			@PathVariable String email,
			@AuthenticationPrincipal Jwt token,
			HttpServletResponse response) {

		CollegeAdmin callingCollegeAdmin = collegeAdminRepository.findDistinctByAuth0Id(token.getSubject());

		if (callingCollegeAdmin == null || !callingCollegeAdmin.isApproved()) {
			response.setStatus(403);
			return null;
		}

		CollegeAdmin targetCollegeAdmin = collegeAdminRepository.findDistinctByEmail(email);

		if (targetCollegeAdmin == null || !callingCollegeAdmin.getCollege().getId().equals(targetCollegeAdmin.getCollege().getId())) {
			response.setStatus(403);
			return null;
		}

		targetCollegeAdmin.setApproved(true);
		collegeAdminRepository.save(targetCollegeAdmin);
		return null;
	}
	/**
	 * Returns the college of the calling token
	 * @param token - the token of the calling user
	 * @param response - the response object to set the status code
	 * @return the college of the calling token
	 */
	@GetMapping("/collegeAdmin")
	public CollegeAdmin getCollege(
			@AuthenticationPrincipal Jwt token,
			HttpServletResponse response) {

		// This will have to change depending on how CollegeUser is implemented.
		CollegeAdmin result = collegeAdminRepository.findDistinctByAuth0Id(token.getSubject());

		// if the college doesn't exist then send back a 404
		if (result == null) {
			response.setStatus(404);
			return null;
		}

		// if the token doesn't belong to the result, then send back 403
		if (! token.getSubject().equals(result.getAuth0Id())) {
			response.setStatus(403);
			return null;
		}

		// if unapproved, yeet the attached College
		if (! result.isApproved()) { 
			result.setCollege(null);
			return result;
		}

		return result;
	}

	// This annotation is for the GET HTTP method
	// Notice the `{id}` in there. That is used by Spring's
	// @PathVariable annotation to get variables encoded in the path
	@GetMapping("/collegeAdmin/{id}")
	public CollegeAdmin getCollegeAdminById(
			// Spring annotation - takes a variable from the
			// REST path and bundles it so we can use it in our code
			@PathVariable String id,
			HttpServletResponse response
			) {
		Optional<CollegeAdmin> query = collegeAdminRepository.findById(id);
		if (query.isEmpty()) {
			// couldn't find it, so return a 404
			response.setStatus(404);
			return null;
		}

		// check if caller admin is approved
		CollegeAdmin result = query.get();
		if (!result.isApproved()) { 
			response.setStatus(403);
			return null;
		}
		// HttpServletResponse defaults to 200 okay,
		// so just return the object we got
		return result;
	}

	@PutMapping("/collegeAdmin")
	public CollegeAdmin modifyCollegeAdmin(
			@RequestBody CollegeAdmin input,
			@AuthenticationPrincipal Jwt token,
			HttpServletResponse response) {
		CollegeAdmin result = collegeAdminRepository.findDistinctByAuth0Id(token.getSubject());
		// if the college doesn't exist then send back a 403
		if (result == null) {
			response.setStatus(403);
			return null;
		}
		// if the token doesn't belong to the caller, then send back 403
		if (! token.getSubject().equals(input.getAuth0Id())) {
			response.setStatus(403);
			return null;
		}
		// if the given auth0Id doesn't match the auth0Id in our db
		if (! input.getAuth0Id().equals(result.getAuth0Id())) {
			response.setStatus(403);
			return null;
		}
		// if the id is different, then we overwrite given id
		if (! input.getId().equals(result.getId())) {
			input.setId(result.getId());
		}

		// update database w/ new collegeAdmin and return college
		input = collegeAdminRepository.save(input);
		return input;
	}

	@DeleteMapping("/collegeAdmin/{id}")
	public boolean deleteCollegeAdmin(
			@PathVariable String id,
			HttpServletResponse response
			) {
		Optional<CollegeAdmin> query = collegeAdminRepository.findById(id);
		if (query.isEmpty()) {
			// couldn't find it, so return a 404
			response.setStatus(404);
			return false;
		}

		// check if caller admin is approved
		CollegeAdmin result = query.get();
		if (!result.isApproved()) { 
			response.setStatus(403);
			return false;
		}
		collegeAdminRepository.delete(query.get());
		return true;
	}

	@PutMapping("/collegeAdmin/college")
	public College updateCollegeAdmin(
			@RequestBody College incoming,
			@AuthenticationPrincipal Jwt jwt,
			HttpServletResponse response
			) {
		
		CollegeAdmin caller = collegeAdminRepository.findDistinctByAuth0Id(jwt.getSubject());
		if (caller == null || !caller.isApproved()) {
			response.setStatus(403);
			return null;
		}
		
		College original = caller.getCollege();
		if (original == null || !incoming.getId().equals(original.getId())) {
			response.setStatus(400);
			return null;
		}
		
		// override values we do not want the user to be able to change
		incoming.setCollegeAdmins(original.getCollegeAdmins());
		incoming.setPopularity(original.getPopularity());
		incoming.setDegrees(original.getDegrees());
		
		incoming = collegeRepository.save(incoming);
		
		return incoming;
	}

	/**
	 * Gets colleges offering a certain degree name.
	 * TODO: With the splitting of College/CollegeAdmin, these will need to 
	 * be gone over w/ how we handle these. Remember that actual institutions
	 * will now be their own, unique objects.
	 */
	@GetMapping("/collegeAdmins/college/{collegeId}")
	public List<CollegeAdmin> getCollegeAdminsByCollegeId(@PathVariable String collegeId, HttpServletResponse response) {
		// TODO: modify this function to use the CollegeAdminRepository's
		// findByDegreeIn function

		Optional<College> collegeQuery = collegeRepository.findById(collegeId);
		if (collegeQuery.isEmpty()) {
			response.setStatus(404);
			return null;
		}
		List<CollegeAdmin> collegeAdmins = collegeAdminRepository.findByCollege(collegeQuery.get());
		if (collegeAdmins.isEmpty()) {
			response.setStatus(404);
			return null;
		}
		return collegeAdmins;
	}

	@PostMapping("/collegeAdmin/college/degree")
	public Degree addDegreeToCollege(
			@RequestBody Degree degree,
			@AuthenticationPrincipal Jwt jwt,
			HttpServletResponse response
			) {
		CollegeAdmin caller = collegeAdminRepository.findDistinctByAuth0Id(jwt.getSubject());
		if (caller == null || !caller.isApproved()) {
			response.setStatus(403);
			return null;
		}

		// Get the college
		College college = caller.getCollege();
		if (college == null) {
			response.setStatus(400);
			return null;
		}

		// check if degree already exists
		if(college.getDegrees().contains(degree)){
			response.setStatus(400);
			return null;
		}

		degree = degreeRepository.save(degree);
		college.addDegree(degree);
		college = collegeRepository.save(college);
		return degree;
	}

	@DeleteMapping("/collegeAdmin/college/degree/{degreeId}")
	public void removeDegreeFromCollege(
			@PathVariable String degreeId,
			@AuthenticationPrincipal Jwt jwt,
			HttpServletResponse response
			) {
		CollegeAdmin caller = collegeAdminRepository.findDistinctByAuth0Id(jwt.getSubject());
		if (caller == null || !caller.isApproved()) {
			response.setStatus(403);
			return;
		}

		// Get the college
		College college = caller.getCollege();
		if (college == null) {
			response.setStatus(400);
			return;
		}

		// check if degree exists
		Optional<Degree> result = degreeRepository.findById(degreeId);
		if(result.isEmpty()){
			response.setStatus(400);
			return;
		}

		// check if degree is in college
		Degree d = result.get();
		if(!college.getDegrees().contains(d)){
			response.setStatus(200);
			return;
		}

		college.getDegrees().remove(d);
		collegeRepository.save(college);
		degreeRepository.delete(d);
		return;
	}
}

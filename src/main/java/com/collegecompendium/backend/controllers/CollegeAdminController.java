package com.collegecompendium.backend.controllers;

import java.util.List;
import java.util.Optional;

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

import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.CollegeAdmin;
import com.collegecompendium.backend.repositories.CollegeAdminRepository;
import com.collegecompendium.backend.repositories.CollegeRepository;

import jakarta.servlet.http.HttpServletResponse;

// Spring annotation - marks this class as providing REST API endpoints
@RestController
// this has to be put on every controller we want to access from a
// web browser (CORS my beloathed)
@CrossOrigin(origins = "http://localhost:3000")
public class CollegeAdminController {
	// Spring annotation - injects a defined Spring Bean here
	// These beans can be implicit (eg. @Component on a class/interface
	// that extends a special type of class/interface) or explicit
	// (@Bean on a function that returns a value)
	@Autowired
	private CollegeAdminRepository collegeAdminRepository;
	@Autowired
	private CollegeRepository collegeRepository;
	// Spring annotation - defines a REST endpoint to be handled by
	// the annotated function. function arguments can be annotated
	// for input (ex @RequestBody) or implicit (eg. HttpServletResponse)
	// TEST

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
		// HttpServletResponse defaults to 200 okay,
		// so just return the object we got
		return query.get();
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
		collegeAdminRepository.delete(query.get());
		return true;
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
			response.setStatus(204);
			return null;
		}
		return collegeAdmins;
    }

}

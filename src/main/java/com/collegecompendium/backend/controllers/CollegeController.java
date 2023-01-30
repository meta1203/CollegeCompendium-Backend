package com.collegecompendium.backend.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.repositories.CollegeRepository;

import jakarta.servlet.http.HttpServletResponse;

// Spring annotation - marks this class as providing REST API endpoints
@RestController
public class CollegeController {
	// Spring annotation - injects a defined Spring Bean here
	// These beans can be implicit (eg. @Component on a class/interface
	// that extends a special type of class/interface) or explicit
	// (@Bean on a function that returns a value)
	@Autowired
	private CollegeRepository collegeRepository;

	// Spring annotation - defines a REST endpoint to be handled by
	// the annotated function. function arguments can be annotated
	// for input (ex @RequestBody) or implicit (eg. HttpServletResponse)

	// This annotation is for the POST HTTP method
	@PostMapping("/college")
	public College createNewCollege(
			@RequestBody College input, 
			HttpServletResponse response) {
		// set the ID to null so it can be auto-generated
		input.setId(null);
		// save the college to the database, saving the backing
		// object to a new `output` variable
		College output = collegeRepository.save(input);
		// send this new object back to the client via JSON
		// Spring automagically (un)marshals JSON to and from
		// classes for you
		return output;
	}

	// This annotation is for the GET HTTP method
	// Notice the `{id}` in there. That is used by Spring's
	// @PathVariable annotation to get variables encoded in the path
	@GetMapping("/college/{id}")
	public College getCollegeById(
			// Spring annotation - takes a variable from the
			// REST path and bundles it so we can use it in our code
			@PathVariable String id,
			HttpServletResponse response
			) {
		Optional<College> query = collegeRepository.findById(id);
		if (query.isEmpty()) {
			// couldn't find it, so return a 404
			response.setStatus(404);
			return null;
		}
		// HttpServletResponse defaults to 200 okay,
		// so just return the object we got
		return query.get();
	}

	@PutMapping("/college/{id}")
	public College updateCollege(
			@RequestBody College input,
			HttpServletResponse response
			) {
		Optional<College> query = collegeRepository.findById(input.getId());
		if (query.isEmpty()) {
			// couldn't find it, so return a 404
			response.setStatus(404);
			return null;
		}

		College college = query.get();
		
		// any object obtained from a repository is automatically wired
		// up to the database; simply make changes to the object and
		// they will be persisted into the DB
		
		// I really don't feel like implementing this right now, so this will be
		// left as an exercise for the reader ;)
		
		return college;
	}

	@DeleteMapping("/college/{id}")
	public boolean deleteCollege(
			@PathVariable String id,
			HttpServletResponse response
			) {
		Optional<College> query = collegeRepository.findById(id);
		if (query.isEmpty()) {
			// couldn't find it, so return a 404
			response.setStatus(404);
			return false;
		}
		collegeRepository.delete(query.get());
		return true;
	}
}

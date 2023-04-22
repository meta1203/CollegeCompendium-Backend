package com.collegecompendium.backend.controllers;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.collegecompendium.backend.models.Major;
import com.collegecompendium.backend.repositories.MajorRepository;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class MajorController {
    @Autowired
    private MajorRepository majorRepository;

    @GetMapping("/search/major")
    private Major getMajorById(@RequestParam String id, HttpServletResponse response) {
        Major result = majorRepository.findById(id).orElse(null);

        if (result == null) {
            response.setStatus(404);
            return null;
        }
        else {
            response.setStatus(200);
            return result;
        }
    }

    // GET /search/majors?name={partialName}
    @GetMapping("/search/majors")
    private List<Major> getMajorByPartialName(@RequestParam(required = false) String name, HttpServletResponse response) {
    	List<Major> result = null;
    	
    	// if no name is requested, get all majors
    	if (name == null) {
    		result = new LinkedList<>();
    		Iterable<Major> iter = majorRepository.findAll();
    		if (iter != null) {
    			for (Major m : iter) {
    				result.add(m);
    			}
    		}
    	} else {
    		result = majorRepository.findByMajorNameContains(name);
    	}

        if (result == null || result.isEmpty()) {
            response.setStatus(404);
            return null;
        } else {
            response.setStatus(200);
            return result;
        }
    }
}

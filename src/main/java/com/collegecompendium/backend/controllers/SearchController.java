package com.collegecompendium.backend.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.Location;
import com.collegecompendium.backend.models.Major;
import com.collegecompendium.backend.models.Student;
import com.collegecompendium.backend.repositories.CollegeRepository;
import com.collegecompendium.backend.repositories.MajorRepository;
import com.collegecompendium.backend.repositories.StudentRepository;

import jakarta.servlet.http.HttpServletResponse;

@RestController
// Web browser visibility
@CrossOrigin(origins = {"http://localhost:3000", "https://cse326.meta1203.com/"})
public class SearchController {

    @Autowired
    private CollegeRepository collegeRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    StudentRepository studentRepository;

    // /search/college/distances/{miles}
    @GetMapping("/search/colleges/distance/{miles}")
    public List<College> listWithinRadiusCollege(@PathVariable double miles, @AuthenticationPrincipal Jwt token, HttpServletResponse response) {
        Student student = studentRepository.findDistinctByAuth0Id(token.getSubject());
        // for test function
        // create three colleges
        // 1. is 3 miles away
        // 2. is 5 miles away
        // 3. is 10 miles away
        // when getColleges < 7  miles away.

        if (student == null) {
            response.setStatus(400);
            return null;
        }

        // Location object for student
        Location studentLocation = student.getLocation();

        List<College> result = collegeRepository.findAllCollegesNear(studentLocation, miles);

        // sort results by distance
        result.sort((a, b) ->
                (int) (studentLocation.distanceFrom(a.getLocation()) -
                        studentLocation.distanceFrom(b.getLocation())));

        // remove CollegeAdmins from list to preserve privacy 
        result.parallelStream().forEach(c -> {
            c.setCollegeAdmins(null);
        });

        return result;
    }

    @GetMapping("/search/college/{collegeID}")
    public College findCollegeByID(
            @PathVariable String collegeID,
            @AuthenticationPrincipal Jwt token,
            HttpServletResponse response
    ) {
        Optional<College> result = collegeRepository.findById(collegeID);
        if (result.isEmpty()) {
            response.setStatus(404);
            return null;
        }
        College output = result.get();

        if (!output.getId().equals(collegeID)) {
            // If for some reason our fetch doesn't have the right ID, send an error response + the output
            response.setStatus(500);
            return output;
        }

        return output;
    }
    
    @GetMapping("/search/colleges")
    public List<College> findCollegesByName(
    		@RequestParam(required = false) String name,
    		HttpServletResponse response
    		) {
    	List<College> ret = collegeRepository.findByNameContainsIgnoreCase(name);
    	
    	if (ret == null || ret.isEmpty()) {
    		response.setStatus(404);
    		return Collections.emptyList();
    	}
    	
    	// remove CollegeAdmins to preserve privacy
    	ret.parallelStream().forEach(c -> c.setCollegeAdmins(Collections.emptyList()));
    	return ret;
    }

    @GetMapping("/search/colleges/major")
    public List<College> findCollegesByMajor(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String id,
            @AuthenticationPrincipal Jwt token,
            HttpServletResponse response
    ) {
        List<Major> majors = new ArrayList<>();
        if(id != null) {
            Optional<Major> major;
            major = majorRepository.findDistinctById(id);
            if(major.isEmpty() && name == null) {
                response.setStatus(404);
                return Collections.emptyList();
            }
            major.ifPresent(majors::add);
        }
        if(name != null) {
            List<Major> majorList = majorRepository.findByNameContainsIgnoreCase(name);
            if(majorList.isEmpty()) {
                response.setStatus(404);
                return Collections.emptyList();
            }
            majors.addAll(majorList);
        }

        List<College> result = new ArrayList<>();

        for (Major major : majors) {
            List<College> colleges = collegeRepository.findByDegreesMajorId(major.getId());
            for (College college : colleges) {
                if (!result.contains(college)) {
                    result.add(college);
                }
            }
        }

        if (result.isEmpty()) {
            response.setStatus(404);
            return Collections.emptyList();
        }
        return result;

    }
}
package com.collegecompendium.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.collegecompendium.backend.configurations.UserProvider;
import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.Location;
import com.collegecompendium.backend.models.Student;
import com.collegecompendium.backend.repositories.CollegeRepository;
import com.collegecompendium.backend.repositories.StudentRepository;

import jakarta.servlet.http.HttpServletResponse;

@RestController
// Web browser visibility
@CrossOrigin(origins = "http://localhost:3000")
public class SearchController {

    @Autowired
    private CollegeRepository collegeRepository;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    StudentRepository studentRepository;

    // /search/college/distances/{miles}
    @GetMapping("/search/colleges/distance/{miles}")
    public List<College> listWithinRadiusCollege(@PathVariable int miles, @AuthenticationPrincipal Jwt token, HttpServletResponse response) {
        Student student = studentRepository.findDistinctByAuth0Id(token.getSubject());
        // for test function
        // create three colleges
            // 1. is 3 miles away
            // 2. is 5 miles away
            // 3. is 10 miles away
        // when getColleges < 7  miles away.

        if(student == null) {
            response.setStatus(400);
            return null;
        }

        // Location object for student
        Location studentLocation = student.getLocation();

        List<College> result = collegeRepository.findAllCollegesNear(studentLocation, miles);

        // sort results by distance
        result.sort((a, b) ->
                (int)(studentLocation.distanceFrom(a.getLocation()) -
                        studentLocation.distanceFrom(b.getLocation())));
        
        // remove CollegeAdmins from list to preserve privacy 
        result.parallelStream().forEach(c -> {
        	c.setCollegeAdmins(null);
        });
        
        return result;
    }
}
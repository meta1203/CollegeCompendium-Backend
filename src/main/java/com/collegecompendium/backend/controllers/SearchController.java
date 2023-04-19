package com.collegecompendium.backend.controllers;

import com.collegecompendium.backend.configurations.Auth0Provider;
import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.Location;
import com.collegecompendium.backend.models.Student;
import com.collegecompendium.backend.repositories.CollegeRepository;
import com.collegecompendium.backend.repositories.CollegeRepositoryCustom;
import com.collegecompendium.backend.repositories.CollegeRepositoryCustomImpl;
import com.collegecompendium.backend.repositories.StudentRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RestController
// Web browser visibility
@CrossOrigin(origins = "http://localhost:3000")
public class SearchController {

    @Autowired
    private CollegeRepository collegeRepository;

    @Autowired
    private Auth0Provider auth0Provider;

    @Autowired
    StudentRepository studentRepository;


    // /search/college/distance/{miles}
    @GetMapping("/search/college/distance/{miles}")
    public List<College> listWithinRadiusCollege(@PathVariable int miles, @AuthenticationPrincipal Jwt token, HttpServletResponse response) {
        Student student = studentRepository.findDistinctByAuth0Id(token.getSubject());
        // for test function
        // create three colleges
            // 1. is 3 miles away
            // 2. is 5 miles away
            // 3. is 10 miles away
        // when getColleges < 7  miles away.


        if(student == null) {
//            student newStudent =
            response.setStatus(404);
            List<College> collegeList = new LinkedList<College>();
            return collegeList;
        }


        // Location object for student
        Location studentLocation = student.getLocation();

        List<College> result = collegeRepository.findAllCollegesNear(studentLocation, miles);


        result.sort((a, b) ->
                (int)(studentLocation.distanceFrom(a.getLocation()) -
                        studentLocation.distanceFrom(b.getLocation())));
        return result;
    }

}
package com.collegecompendium.backend.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.collegecompendium.backend.repositories.DegreeRepository;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://cse326.meta1203.com/"})
public class DegreeController {
    @Autowired
    private DegreeRepository degreeRepository;

//    @Autowired
//    private MajorRepository majorRepository;
//
//    @Autowired
//    private CollegeAdminRepository collegeRepository;


}




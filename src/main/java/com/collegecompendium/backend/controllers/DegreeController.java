package com.collegecompendium.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.collegecompendium.backend.repositories.DegreeRepository;

@RestController
public class DegreeController {
    @Autowired
    private DegreeRepository degreeRepository;

//    @Autowired
//    private MajorRepository majorRepository;
//
//    @Autowired
//    private CollegeAdminRepository collegeRepository;


}




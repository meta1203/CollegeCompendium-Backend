package com.collegecompendium.backend.controllers;

import com.collegecompendium.backend.models.Major;
import com.collegecompendium.backend.repositories.DegreeRepository;
import com.collegecompendium.backend.repositories.MajorRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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




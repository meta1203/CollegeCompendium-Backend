package com.collegecompendium.backend.models;

import com.collegecompendium.backend.repositories.DegreeRepository;
import com.fasterxml.jackson.annotation.JsonGetter;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * This class represents a college degree. It contains the name of the degree and the number of credits required to earn it.
 * Colleges can have multiple degrees.
 *
 * @author Kor
 */
@Data
@Entity
public class Degree {
    private enum DegreeType {
        ASSOCIATE,
        BACHELOR,
        MASTER,
        DOCTORATE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotEmpty
    @NotNull
    @ManyToOne
    private Major major;

    // These actually can be empty/null, if the school doesn't provide enough data
    private DegreeType degreeType;
    private int creditsRequired;



    }


package com.collegecompendium.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
    private String name;

    // These actually can be empty/null, if the school doesn't provide enough data
    private DegreeType degreeType;
    private int creditsRequired;
    
    // TODO: Check why @Data isn't picking this up - Erik
    public String getName() {
        return name;
    }
}

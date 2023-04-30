package com.collegecompendium.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Builder
/**
 * Represents a college major
 */
public class Major {
    /**
     * Represents the type of major, such as Arts or Science
     */
    public enum MajorType {
        ARTS, SCIENCE, BUSINESS, PUBLIC,
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    @NotEmpty
    private String name;

    @Enumerated(EnumType.STRING)
    private MajorType majorType;

    public Major(){
        // Empty constructor for JPA
    }

    /**
     * Creates a new Major object
     * @param id The id of the major (Generated automatically by saving to the database)
     * @param name The name of the major
     * @param majorType The type of major
     */
    public Major(String id, String name, MajorType majorType) {
        this.id = id;
        this.name = name;
        this.majorType = majorType;
    }
}

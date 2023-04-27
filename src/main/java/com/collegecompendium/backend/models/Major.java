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
public class Major {
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

    public Major(String id, String name, MajorType majorType) {
        this.id = id;
        this.name = name;
        this.majorType = majorType;
    }
}

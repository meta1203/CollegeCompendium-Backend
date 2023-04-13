package com.collegecompendium.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class Major {
    private enum MajorType {
        ARTS, SCIENCE, BUSINESS, PUBLIC,
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    @NotEmpty
    private String majorName;

    @Enumerated(EnumType.STRING)
    private MajorType majorType;
}

package com.collegecompendium.backend.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class Major {

    private enum MajorType {
        ARTS, SCIENCE, BUSINESS, PUBLIC,
    }
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    @NotNull
    @NotEmpty
    private String MajorName;

    @OneToMany
    private MajorType majorType;



}

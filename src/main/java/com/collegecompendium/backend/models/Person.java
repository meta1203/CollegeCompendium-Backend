package com.collegecompendium.backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class Person {

    private final UUID id;
    private final String name;
    private final float GPA;
    private final String location;

    public Person(@JsonProperty("id") UUID id, @JsonProperty("name") String name, @JsonProperty("GPA") float GPA, @JsonProperty("location") String location) {
        this.id = id;
        this.name = name;
        this.GPA = GPA;
        this.location = location;
    }

    public float getGPA() {
        return GPA;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }
}

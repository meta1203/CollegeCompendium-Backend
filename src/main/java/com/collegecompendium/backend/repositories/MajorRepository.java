package com.collegecompendium.backend.repositories;

import com.collegecompendium.backend.models.Major;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MajorRepository extends CrudRepository<Major, String> {
    public Major findByMajorName(String name);

    public List<Major> findByNameContains(String name);
    public List<Major> findByNameContainsIgnoreCase(String name);
    public Optional<Major> findDistinctById(String id);
}

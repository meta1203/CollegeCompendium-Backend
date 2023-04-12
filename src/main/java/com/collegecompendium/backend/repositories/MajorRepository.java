package com.collegecompendium.backend.repositories;

import com.collegecompendium.backend.models.Major;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MajorRepository extends CrudRepository<Major, String> {
    public Major findByMajorName(String name);

    public List<Major> findByPartialMajorName(String name);
}

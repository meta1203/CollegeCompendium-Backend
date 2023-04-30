package com.collegecompendium.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import com.collegecompendium.backend.models.Student;
import jakarta.transaction.Transactional;


@Transactional
/**
 * Repository for Student, provides CRUD operations
 */
public interface StudentRepository extends CrudRepository<Student, String> {
	public Student findDistinctByUsername(String username);
	public Student findDistinctByAuth0Id(String auth0Id);
}

package com.collegecompendium.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import com.collegecompendium.backend.models.Student;
import jakarta.transaction.Transactional;


@Transactional
public interface StudentRepository extends CrudRepository<Student, String> {
	public Student findDistinctByUsername(String username);
	//TODO findByName() Student/User?
}

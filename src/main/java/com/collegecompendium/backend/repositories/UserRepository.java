package com.collegecompendium.backend.repositories;
import org.springframework.data.repository.CrudRepository;

import com.collegecompendium.backend.models.User;

@Repository
public interface UserRepository extends CrudRepository<User, String>{
	public User findDistinctByUsername(String username);
}

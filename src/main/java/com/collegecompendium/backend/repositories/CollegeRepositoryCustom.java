package com.collegecompendium.backend.repositories;

import java.util.List;

import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.Location;

public interface CollegeRepositoryCustom {
	public List<College> findAllCollegesNear(Location location, Integer range);
}

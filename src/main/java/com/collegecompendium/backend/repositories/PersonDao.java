package com.collegecompendium.backend.repositories;
import com.collegecompendium.backend.models.Person;

import java.util.UUID;

public interface PersonDao {

    int insertPerson(UUID id, Person Person);

    default int insertPerson(Person person) {
        UUID id = UUID.randomUUID();
        return insertPerson(id, person);
    }

}

package ru.alex.professionalslanguageapi.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alex.professionalslanguageapi.database.entity.Animal;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Integer> {

}

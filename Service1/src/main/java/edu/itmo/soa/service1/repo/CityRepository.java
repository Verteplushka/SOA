package edu.itmo.soa.service1.repo;

import edu.itmo.soa.service1.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {
    boolean existsByName(String name);
    List<City> findByMetersAboveSeaLevel(Integer metersAboveSeaLevel);
}


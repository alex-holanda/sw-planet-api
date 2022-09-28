package com.example.swplanetapi.domain.repository;

import com.example.swplanetapi.domain.model.Planet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanetRepository extends JpaRepository<Planet, Long> {

    Optional<Planet> findByName(String planetName);
}

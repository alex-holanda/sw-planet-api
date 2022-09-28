package com.example.swplanetapi.domain.service;

import com.example.swplanetapi.domain.model.Planet;
import com.example.swplanetapi.domain.repository.PlanetRepository;
import com.example.swplanetapi.domain.repository.QueryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanetService {

    private final PlanetRepository planetRepository;

    public List<Planet> list(String terrain, String climate) {
        Example<Planet> query = QueryBuilder.makeQuery(new Planet(null, null, climate, terrain));
        return planetRepository.findAll(query);
    }

    public Planet create(Planet planet) {
        return planetRepository.save(planet);
    }

    public Optional<Planet> get(Long planetId) {
        return planetRepository.findById(planetId);
    }

    public Optional<Planet> getByName(String planetName) {
        return planetRepository.findByName(planetName);
    }

    public void remove(Long planetId) {
        planetRepository.deleteById(planetId);
    }
}

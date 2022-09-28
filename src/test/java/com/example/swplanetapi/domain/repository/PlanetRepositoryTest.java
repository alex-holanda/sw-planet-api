package com.example.swplanetapi.domain.repository;

import com.example.swplanetapi.domain.model.Planet;
import org.h2.table.Plan;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static com.example.swplanetapi.PlanetConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;

@DataJpaTest
public class PlanetRepositoryTest {

    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @AfterEach
    void afterEach() {
        PLANET.setId(null);
    }

    @Test
    @DisplayName("Cria um planeta com dados válidos")
    void createPlanet_WithValidData_ReturnsPlanet() {
        Planet planet = planetRepository.save(PLANET);

        Planet sut = testEntityManager.find(Planet.class, planet.getId());

        assertThat(sut).isNotNull();
        assertThat(sut.getName()).isEqualTo(PLANET.getName());
        assertThat(sut.getClimate()).isEqualTo(PLANET.getClimate());
        assertThat(sut.getTerrain()).isEqualTo(PLANET.getTerrain());
        assertThat(sut).isEqualTo(planet);
    }

    @Test
    @DisplayName("Cria um planeta com dados inválidos")
    void createPlanet_WithInvalidData_ThrowsException() {
        assertThatThrownBy(() -> planetRepository.save(EMPTY_PLANET)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> planetRepository.save(INVALID_PLANET)).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Não deve criar um planeta com nome duplicado")
    void createPlanet_WithExistingName_ThrowsException() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);
        testEntityManager.detach(planet);
        planet.setId(null);

        assertThatThrownBy(() -> planetRepository.save(planet)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void getPlanet_ByExistingId_ReturnsPlanet() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        Optional<Planet> sut = planetRepository.findById(planet.getId());

        assertThat(sut).isPresent();
        assertThat(sut.get().getId()).isNotNull();
        assertThat(sut.get()).isEqualTo(planet);
    }

    @Test
    void getPlanet_ByUnexistingId_ReturnsEmpty() {
        Optional<Planet> sut = planetRepository.findById(1L);

        assertThat(sut).isEmpty();
    }

    @Test
    void getPlanet_ByExistingName_ReturnsPlanet() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        Optional<Planet> sut = planetRepository.findByName(planet.getName());

        assertThat(sut).isNotNull();
        assertThat(sut.get()).isEqualTo(planet);
    }

    @Test
    void getPlanet_ByUnexistingName_ReturnsEmpty() {
        Optional<Planet> sut = planetRepository.findByName("tatooine");

        assertThat(sut).isEmpty();
    }

    @Sql(scripts = "/import_planets.sql")
    @Test
    void listPlanets_ReturnsFilteredPlanets() {
        Example<Planet> queryWithoutFilters = QueryBuilder.makeQuery(Planet.builder()
                .build());

        Example<Planet> queryWithFilters = QueryBuilder.makeQuery(Planet.builder()
                .terrain(TATOOINE.getTerrain())
                .climate(TATOOINE.getClimate())
                .build());

        List<Planet> responseWithoutFilters = planetRepository.findAll(queryWithoutFilters);
        List<Planet> responseWithFilters = planetRepository.findAll(queryWithFilters);

        assertThat(responseWithoutFilters).isNotEmpty().hasSize(3);
        assertThat(responseWithFilters).isNotEmpty().hasSize(1);
        assertThat(responseWithFilters.get(0)).isEqualTo(TATOOINE);
    }

    @Test
    void listPlanets_ReturnsNoPlanets() {
        Example<Planet> query = QueryBuilder.makeQuery(Planet.builder().build());

        List<Planet> response = planetRepository.findAll(query);

        assertThat(response).isEmpty();
    }

    @Test
    void removePlanet_WithExistinId_RemovesPlanetFromDatabase() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        planetRepository.deleteById(planet.getId());

        Planet sut = testEntityManager.find(Planet.class, planet.getId());

        assertThat(sut).isNull();
    }

    @Test
    void removePlanet_WithUnexistingId_ThrowsExcetpion() {
        assertThatThrownBy(() -> planetRepository.deleteById(1L)).isInstanceOf(EmptyResultDataAccessException.class);
    }
}

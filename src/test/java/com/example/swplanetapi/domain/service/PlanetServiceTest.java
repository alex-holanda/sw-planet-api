package com.example.swplanetapi.domain.service;

import com.example.swplanetapi.domain.model.Planet;
import com.example.swplanetapi.domain.repository.PlanetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.swplanetapi.PlanetConstants.INVALID_PLANET;
import static com.example.swplanetapi.PlanetConstants.PLANET;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlanetServiceTest {

    @InjectMocks
    private PlanetService planetService;

    @Mock
    private PlanetRepository planetRepository;

    @Test
    @DisplayName("Create a planet with valid data")
    void createPlanet_WithValidData_ReturnsPlanet() {
        when(planetRepository.save(any(Planet.class))).thenReturn(PLANET);

        Planet sut = planetService.create(PLANET);

        assertThat(sut).isEqualTo(PLANET);

        verify(planetRepository, times(1)).save(any(Planet.class));
    }

    @Test
    @DisplayName("Create a invalid planet")
    void createPlanet_WithInvalidData_ThrowsException() {
        when(planetRepository.save(any(Planet.class))).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> planetService.create(INVALID_PLANET)).isInstanceOf(RuntimeException.class);

        verify(planetRepository, times(1)).save(any(Planet.class));
    }


    @Test
    @DisplayName("Get planet by existing id")
    void getPlanet_ByExistingId_ReturnsPlanet() {
        when(planetRepository.findById(anyLong())).thenReturn(Optional.of(PLANET));

        Optional<Planet> sut = planetService.get(anyLong());

        assertThat(sut).isNotEmpty();
        assertThat(sut.get()).isNotNull();
        assertThat(sut.get()).isEqualTo(PLANET);

        verify(planetRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Get planet by inexisting id")
    void getPlanet_ByInexistingId_ReturnsEmpty() {
        when(planetRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Planet> sut = planetService.get(anyLong());

        assertThat(sut).isEmpty();

        verify(planetRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Get planet by existing name")
    void getPlanet_ByExistingName_ReturnsPlanet() {
        when(planetRepository.findByName(anyString())).thenReturn(Optional.of(PLANET));

        Optional<Planet> sut = planetService.getByName(PLANET.getName());

        assertThat(sut).isPresent();
        assertThat(sut.get()).isNotNull();
        assertThat(sut.get()).isEqualTo(PLANET);

        verify(planetRepository, times(1)).findByName(anyString());
    }

    @Test
    @DisplayName("Get planet by inexisting name")
    void getPlanet_ByInexistingName_ReturnsEmpty() {
        when(planetRepository.findByName(anyString())).thenReturn(Optional.empty());

        Optional<Planet> sut = planetService.getByName(PLANET.getName());

        assertThat(sut).isEmpty();

        verify(planetRepository, times(1)).findByName(anyString());
    }

    @Test
    @DisplayName("Lista os planets")
    void listPlanets_ReturnsAllPlanets() {
        when(planetRepository.findAll(any(Example.class))).thenReturn(Arrays.asList(PLANET));

        List<Planet> sut = planetService.list(PLANET.getTerrain(), PLANET.getClimate());

        assertThat(sut).hasSize(1);
        assertThat(sut.get(0)).isEqualTo(PLANET);

        verify(planetRepository, times(1)).findAll(any(Example.class));
    }

    @Test
    @DisplayName("Lista os planetas e retorna vazio")
    void listPlanets_ReturnsNoPlanets() {
        when(planetRepository.findAll(any(Example.class))).thenReturn(Collections.emptyList());

        List<Planet> sut = planetService.list(PLANET.getTerrain(), PLANET.getClimate());

        assertThat(sut).isEmpty();

        verify(planetRepository, times(1)).findAll(any(Example.class));
    }

    @Test
    @DisplayName("Remover um planeta pelo ID")
    void removePlanet_WithExistingId_doesNotThrowAnyException() {
        assertThatCode(() -> planetService.remove(1L)).doesNotThrowAnyException();

        verify(planetRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Remover um planeta com ID inexistente")
    void removePlanet_WithInexistingId_ThrowsException() {
        doThrow(new RuntimeException()).when(planetRepository).deleteById(1L);

        assertThatThrownBy(() -> planetService.remove(1L)).isInstanceOf(RuntimeException.class);

        verify(planetRepository, times(1)).deleteById(anyLong());
    }
}

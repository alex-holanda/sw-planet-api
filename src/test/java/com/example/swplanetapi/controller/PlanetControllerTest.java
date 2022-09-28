package com.example.swplanetapi.controller;

import com.example.swplanetapi.domain.model.Planet;
import com.example.swplanetapi.domain.service.PlanetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.swplanetapi.PlanetConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlanetController.class)
public class PlanetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlanetService planetService;

    @Test
    @DisplayName("Deve criar um planeta")
    void createPlanet_WithValidData_ReturnsCreated() throws Exception {
        when(planetService.create(any(Planet.class))).thenReturn(PLANET);

        var request = post("/planets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(PLANET));

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(PLANET));

        verify(planetService, times(1)).create(any(Planet.class));
    }

    @Test
    void createPlanet_WithInvalidData_ReturnsBadRequest() throws Exception {
        var jsonInvalidPlanet = objectMapper.writeValueAsString(EMPTY_PLANET);

        var request = post("/planets").content(jsonInvalidPlanet)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity());

        verify(planetService, never()).create(any(Planet.class));
    }

    @Test
    void createPlanet_WithExistingName_ReturnsConflict() throws Exception {
        var json = objectMapper.writeValueAsString(PLANET);

        when(planetService.create(any(Planet.class))).thenThrow(DataIntegrityViolationException.class);

        var request = post("/planets").content(json)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());

        verify(planetService, times(1)).create(any(Planet.class));
    }

    @Test
    void getPlanet_ByExistingId_ReturnsPlanet() throws Exception {
        when(planetService.get(anyLong())).thenReturn(Optional.of(PLANET));

        var request = get("/planets/1").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    void getPlanet_ByUnexistingId_ReturnsNotFound() throws Exception {
        when(planetService.get(anyLong())).thenReturn(Optional.empty());

        var request = get("/planets/1").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void getPlanet_ByExistingName_ReturnsPlanet() throws Exception {
        when(planetService.getByName(anyString())).thenReturn(Optional.of(PLANET));

        var request = get("/planets/name/tattooine").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    void getPlanet_ByUnexistingName_ReturnsNotFound() throws Exception {
        when(planetService.getByName(anyString())).thenReturn(Optional.empty());

        var request = get("/planets/name/tattooine").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void listPlanets_ReturnsFilteredPlanets() throws Exception {
        when(planetService.list(null, null)).thenReturn(PLANETS);
        when(planetService.list(TATOOINE.getTerrain(), TATOOINE.getClimate())).thenReturn(List.of(TATOOINE));

        var request = get("/planets").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        request = get(String.format("/planets?terrain=%s&climate=%s", TATOOINE.getTerrain(), TATOOINE.getClimate()))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]").value(TATOOINE));
    }

    @Test
    void listPlanets_ReturnsNoPlanets() throws Exception {
        when(planetService.list(anyString(), anyString())).thenReturn(Collections.emptyList());

        var request = get("/planets").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void removePlanet_WithExistingId_ReturnsNoContent() throws Exception {
        var request = delete("/planets/1");

        mockMvc.perform(request).andExpect(status().isNoContent());

        verify(planetService, times(1)).remove(anyLong());
    }

    @Test
    void removePlanet_WithUnexistingId_ReturnsNotFound() throws Exception {
        doThrow(new EmptyResultDataAccessException(1)).when(planetService).remove(1L);

        var request = delete("/planets/1");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }
}

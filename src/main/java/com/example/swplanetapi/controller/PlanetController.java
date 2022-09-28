package com.example.swplanetapi.controller;

import com.example.swplanetapi.domain.model.Planet;
import com.example.swplanetapi.domain.service.PlanetService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/planets", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlanetController {

    private final PlanetService planetService;

    @GetMapping
    public ResponseEntity<List<Planet>> list(String terrain, String climate) {
        return ResponseEntity.ok(planetService.list(terrain, climate));
    }

    @PostMapping
    public ResponseEntity<Planet> create(@RequestBody @Valid Planet planet) {
        planet = planetService.create(planet);
        return ResponseEntity.status(HttpStatus.CREATED).body(planet);
    }

    @GetMapping("/{planetId}")
    public ResponseEntity<Planet> get(@PathVariable Long planetId) {
        return planetService.get(planetId).map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }
    
    @GetMapping("/name/{planetName}")
    public ResponseEntity<Planet> getByName(@PathVariable String planetName) {
        return planetService.getByName(planetName).map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @DeleteMapping("/{planetId}")
    public ResponseEntity<Planet> remove(@PathVariable Long planetId) {
        planetService.remove(planetId);
        return ResponseEntity.noContent().build();
    }
}

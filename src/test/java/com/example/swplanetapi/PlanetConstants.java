package com.example.swplanetapi;

import com.example.swplanetapi.domain.model.Planet;
import org.apache.tomcat.util.digester.ArrayStack;

import java.util.ArrayList;
import java.util.List;

public class PlanetConstants {

    public static final Planet PLANET = Planet.builder()
            .name("Name")
            .climate("Climate")
            .terrain("Terrain")
            .build();

    public static final Planet EMPTY_PLANET = Planet.builder().build();

    public static final Planet INVALID_PLANET = Planet.builder()
            .name("")
            .climate("")
            .terrain("")
            .build();

    public static final Planet TATOOINE = Planet.builder()
            .id(1L)
            .name("Tatooine")
            .climate("arid")
            .terrain("desert")
            .build();

    public static final Planet ALDERAAN = Planet.builder()
            .id(2L)
            .name("Alderaan")
            .climate("temperate")
            .terrain("grasslands, mountains")
            .build();

    public static final Planet YAVINIV = Planet.builder()
            .id(3L)
            .name("Yavin IV")
            .climate("temperate, tropical")
            .terrain("jungle, rainforests")
            .build();

    public static final List<Planet> PLANETS = new ArrayList<>() {{
        add(TATOOINE);
        add(ALDERAAN);
        add(YAVINIV);
    }};
}

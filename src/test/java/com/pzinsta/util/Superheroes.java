package com.pzinsta.util;

import com.google.common.collect.ImmutableList;
import com.pzinsta.model.Superhero;

import java.time.LocalDate;
import java.time.Month;

public class Superheroes {
    private Superheroes() {}

    public static Superhero superman() {
        Superhero superman = new Superhero();
        superman.setName("Clark Kent");
        superman.setPseudonym("Superman");
        superman.setPublisher("DC Comics");
        superman.setSkills(ImmutableList.of(
                "Superhuman strength, speed, and durability", "Flight", "Heat vision",
                "Freezing breath", "X-ray vision", "Telescopic & microscopic vision"));
        superman.setAllies(ImmutableList.of("Supergirl", "Superboy", "Superdog", "Power Girl"));
        superman.setFirstAppearance(LocalDate.of(1938, Month.APRIL, 18));
        return superman;
    }

    public static Superhero batman() {
        Superhero superman = new Superhero();
        superman.setName("Bruce Wayne");
        superman.setPseudonym("Batman");
        superman.setPublisher("DC Comics");
        superman.setSkills(ImmutableList.of("Genius-level intellect", "Expert detective"));
        superman.setAllies(ImmutableList.of("Robin", "Superman"));
        superman.setFirstAppearance(LocalDate.of(1939, Month.MARCH, 1));
        return superman;
    }
}

package com.pzinsta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SuperheroNotFoundException extends RuntimeException {
    public SuperheroNotFoundException(String message) {
        super(message);
    }

    public static SuperheroNotFoundException withId(Long id) {
        return new SuperheroNotFoundException(String.format("Superhero with ID %s not found.", id));
    }

    public static SuperheroNotFoundException withPseudonym(String pseudonym) {
        return new SuperheroNotFoundException(String.format("Superhero with pseudonym %s not found.", pseudonym));
    }
}

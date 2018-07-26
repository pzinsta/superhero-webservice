package com.pzinsta.exception;

public class SuperheroNotFoundException extends RuntimeException {
    public SuperheroNotFoundException(String message) {
        super(message);
    }

    public static SuperheroNotFoundException withId(Long id) {
        return new SuperheroNotFoundException(String.format("Superhero with ID %s not found.", id));
    }
}

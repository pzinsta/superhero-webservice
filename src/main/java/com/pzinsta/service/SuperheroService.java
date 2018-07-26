package com.pzinsta.service;

import com.pzinsta.model.Superhero;

public interface SuperheroService {
    Superhero create(Superhero superhero);
    Iterable<Superhero> findAll();
    Superhero findById(Long id);
}

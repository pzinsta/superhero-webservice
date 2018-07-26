package com.pzinsta.service.impl;

import com.pzinsta.exception.SuperheroNotFoundException;
import com.pzinsta.model.Superhero;
import com.pzinsta.repository.SuperheroRepository;
import com.pzinsta.service.SuperheroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultSuperheroService implements SuperheroService {

    private SuperheroRepository superheroRepository;

    @Autowired
    public DefaultSuperheroService(SuperheroRepository superheroRepository) {
        this.superheroRepository = superheroRepository;
    }

    @Override
    public Superhero create(Superhero superhero) {
        return superheroRepository.save(superhero);
    }

    @Override
    public Iterable<Superhero> findAll() {
        return superheroRepository.findAll();
    }

    @Override
    public Superhero findById(Long id) {
        return superheroRepository.findById(id)
                .orElseThrow(() -> SuperheroNotFoundException.withId(id));
    }
}

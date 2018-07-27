package com.pzinsta.service.impl;

import com.pzinsta.exception.SuperheroNotFoundException;
import com.pzinsta.model.Superhero;
import com.pzinsta.repository.SuperheroRepository;
import com.pzinsta.service.SuperheroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<Superhero> findAll(Pageable pageable) {
        return superheroRepository.findAll(pageable);
    }

    @Override
    public Iterable<Superhero> findAll(Sort sort) {
        return superheroRepository.findAll(sort);
    }

    @Override
    public Superhero findById(Long id) {
        return superheroRepository.findById(id)
                .orElseThrow(() -> SuperheroNotFoundException.withId(id));
    }

    @Override
    public Superhero findByPseudonym(String pseudonym) {
        return superheroRepository.findByPseudonym(pseudonym)
                .orElseThrow(() -> SuperheroNotFoundException.withPseudonym(pseudonym));
    }

    @Override
    public boolean existsByPseudonym(String pseudonym) {
        return superheroRepository.existsByPseudonym(pseudonym);
    }
}

package com.pzinsta.service;

import com.pzinsta.model.Superhero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface SuperheroService {
    Superhero create(Superhero superhero);

    Iterable<Superhero> findAll();

    Page<Superhero> findAll(Pageable pageable);

    Iterable<Superhero> findAll(Sort sort);

    Superhero findById(Long id);

    Superhero findByPseudonym(String pseudonym);

    boolean existsByPseudonym(String pseudonym);
}

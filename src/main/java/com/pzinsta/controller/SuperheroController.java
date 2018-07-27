package com.pzinsta.controller;

import com.pzinsta.model.Superhero;
import com.pzinsta.service.SuperheroService;
import com.pzinsta.validator.SuperheroValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/superheroes")
public class SuperheroController {

    private SuperheroService superheroService;
    private SuperheroValidator superheroValidator;

    @Autowired
    public SuperheroController(SuperheroService superheroService, SuperheroValidator superheroValidator) {
        this.superheroService = superheroService;
        this.superheroValidator = superheroValidator;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(superheroValidator);
    }

    @PostMapping
    public ResponseEntity<Superhero> create(@Valid @RequestBody Superhero superhero) {
        Superhero createdSuperhero = superheroService.create(superhero);
        URI resourceLocationUri = UriComponentsBuilder.fromPath("/superheroes/{id}")
                .buildAndExpand(createdSuperhero.getId()).toUri();
        return ResponseEntity.created(resourceLocationUri).body(createdSuperhero);
    }

    @GetMapping
    public Iterable<Superhero> findAll() {
        return superheroService.findAll();
    }

    @GetMapping(params = "page")
    public Page<Superhero> findAll(Pageable pageable) {
        return superheroService.findAll(pageable);
    }

    @GetMapping(params = "sort")
    public Iterable<Superhero> findAll(Sort sort) {
        return superheroService.findAll(sort);
    }

    @GetMapping("/{id}")
    public Superhero findById(@PathVariable("id") Long id) {
        return superheroService.findById(id);
    }

    @GetMapping(params = "pseudonym")
    public Superhero findByPseudonym(@RequestParam("pseudonym") String pseudonym) {
        return superheroService.findByPseudonym(pseudonym);
    }
}

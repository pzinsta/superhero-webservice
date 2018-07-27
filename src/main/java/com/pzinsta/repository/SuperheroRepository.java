package com.pzinsta.repository;

import com.pzinsta.model.Superhero;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuperheroRepository extends PagingAndSortingRepository<Superhero, Long> {
    Optional<Superhero> findByPseudonym(String pseudonym);
    boolean existsByPseudonym(String pseudonym);
}

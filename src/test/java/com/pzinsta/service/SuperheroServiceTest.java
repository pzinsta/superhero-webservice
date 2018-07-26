package com.pzinsta.service;

import com.pzinsta.exception.SuperheroNotFoundException;
import com.pzinsta.model.Superhero;
import com.pzinsta.repository.SuperheroRepository;
import com.pzinsta.service.impl.DefaultSuperheroService;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SuperheroServiceTest {

    private final static Long SUPERHERO_ID = 42L;

    private Superhero superman = createSuperman();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @InjectMocks
    private DefaultSuperheroService defaultSuperheroService;

    @Mock
    private SuperheroRepository superheroRepository;

    @Captor
    private ArgumentCaptor<Superhero> superheroArgumentCaptor;

    @Test
    public void shouldCreateSuperhero() throws Exception {
        // given
        assumeThatRepositorySavesAndAssignsIdFor(superman);

        // when
        Superhero result = defaultSuperheroService.create(superman);

        // then
        assertThatSuperheroPassedToRepositoryWasNotModified();
        assertThatServiceReturnedSuperheroFromRepositoryWithoutModifications(result);
    }

    @Test
    public void shouldFindAllSuperheroes() throws Exception {
        // given
        List<Superhero> superheroes = Collections.singletonList(superman);
        given(superheroRepository.findAll()).willReturn(superheroes);

        // when
        Iterable<Superhero> result = defaultSuperheroService.findAll();

        // then
        assertThat(result).containsAll(superheroes);
    }

    @Test
    public void shouldFindById() throws Exception {
        // given
        given(superheroRepository.findById(SUPERHERO_ID)).willReturn(Optional.of(superman));

        // when
        Superhero result = defaultSuperheroService.findById(SUPERHERO_ID);

        // then
        assertThat(result).isEqualTo(superman);
    }

    @Test
    public void shouldThrowExceptionWhenCannotFindById() throws Exception {
        // given
        given(superheroRepository.findById(SUPERHERO_ID)).willReturn(Optional.empty());

        // when
        Throwable result = catchThrowable(() -> defaultSuperheroService.findById(SUPERHERO_ID));

        // then
        assertThat(result).isInstanceOf(SuperheroNotFoundException.class);
    }

    private void assumeThatRepositorySavesAndAssignsIdFor(Superhero superman) {
        when(superheroRepository.save(superman)).thenReturn(createSupermanWithId());
    }

    private Superhero createSupermanWithId() {
        Superhero supermanWithId = createSuperman();
        supermanWithId.setId(SUPERHERO_ID);
        return supermanWithId;
    }

    private void assertThatSuperheroPassedToRepositoryWasNotModified() {
        verify(superheroRepository).save(superheroArgumentCaptor.capture());
        assertThat(superheroArgumentCaptor.getValue()).isEqualTo(superman);
    }

    private void assertThatServiceReturnedSuperheroFromRepositoryWithoutModifications(Superhero result) {
        assertThat(result.getId()).isEqualTo(SUPERHERO_ID);
        assertThat(result).isEqualTo(superman);
    }

    private static Superhero createSuperman() {
        Superhero superman = new Superhero();
        superman.setName("Clark Kent");
        superman.setPseudonym("Superman");
        superman.setPublisher("DC Comics");
        superman.setSkills(Arrays.asList(
                "Superhuman strength, speed, and durability", "Flight", "Heat vision",
                "Freezing breath", "X-ray vision", "Telescopic & microscopic vision"));
        superman.setAllies(Arrays.asList("Supergirl", "Superboy", "Superdog", "Power Girl"));
        superman.setFirstAppearance(LocalDate.of(1938, Month.APRIL, 18));
        return superman;
    }

}
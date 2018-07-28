package com.pzinsta.service;

import com.google.common.collect.ImmutableList;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static com.pzinsta.util.Superheroes.batman;
import static com.pzinsta.util.Superheroes.superman;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SuperheroServiceTest {

    private final static Long SUPERHERO_ID = 42L;
    private static final String PSEUDONYM = "Superman";

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
        Superhero superman = superman();
        givenThatRepositorySavesAndAssignsIdFor(superman);

        // when
        Superhero result = defaultSuperheroService.create(superman);

        // then
        assertThatSuperheroPassedToRepositoryWasNotModified();
        assertThatServiceReturnedSuperheroFromRepositoryWithoutModifications(result);
    }

    private void givenThatRepositorySavesAndAssignsIdFor(Superhero superhero) {
        when(superheroRepository.save(superhero)).thenReturn(supermanWithId());
    }

    private void assertThatSuperheroPassedToRepositoryWasNotModified() {
        verify(superheroRepository).save(superheroArgumentCaptor.capture());
        assertThat(superheroArgumentCaptor.getValue()).isEqualTo(superman());
    }

    private void assertThatServiceReturnedSuperheroFromRepositoryWithoutModifications(Superhero result) {
        assertThat(result).isEqualToComparingFieldByField(supermanWithId());
    }

    @Test
    public void shouldFindAllSuperheroes() throws Exception {
        // given
        List<Superhero> superheroes = ImmutableList.of(superman(), batman());
        given(superheroRepository.findAll()).willReturn(superheroes);

        // when
        Iterable<Superhero> result = defaultSuperheroService.findAll();

        // then
        assertThat(result).containsAll(ImmutableList.of(superman(), batman()));
    }

    @Test
    public void shouldFindAllSuperheroesPaged() throws Exception {
        // given
        int pageNumber = 7;
        int size = 50;
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Superhero> page = new PageImpl<>(ImmutableList.of(superman(), batman()));

        given(superheroRepository.findAll(pageable)).willReturn(page);

        // when
        Page<Superhero> result = defaultSuperheroService.findAll(pageable);

        // then
        assertThat(result).containsExactly(superman(), batman());
    }

    @Test
    public void shouldFindAllSuperheroesSorted() throws Exception {
        // given
        Sort sort = Sort.by("firstAppearance").descending();
        given(superheroRepository.findAll(sort)).willReturn(ImmutableList.of(batman(), superman()));

        // when
        Iterable<Superhero> result = defaultSuperheroService.findAll(sort);

        // then
        assertThat(result).containsExactly(batman(), superman());
    }

    @Test
    public void shouldFindById() throws Exception {
        // given
        given(superheroRepository.findById(SUPERHERO_ID)).willReturn(Optional.of(supermanWithId()));

        // when
        Superhero result = defaultSuperheroService.findById(SUPERHERO_ID);

        // then
        assertThat(result).isEqualToComparingFieldByField(supermanWithId());
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

    @Test
    public void shouldFindByPseudonym() throws Exception {
        // given
        given(superheroRepository.findByPseudonym(PSEUDONYM)).willReturn(Optional.ofNullable(supermanWithId()));

        // when
        Superhero result = defaultSuperheroService.findByPseudonym(PSEUDONYM);

        // then
        assertThat(result).isEqualToComparingFieldByField(supermanWithId());
    }

    @Test
    public void shouldThrowExceptionWhenCannotFindByPseudonym() throws Exception {
        // given
        given(superheroRepository.findByPseudonym(PSEUDONYM)).willReturn(Optional.empty());

        // when
        Throwable result = catchThrowable(() -> defaultSuperheroService.findByPseudonym(PSEUDONYM));

        // then
        assertThat(result).isInstanceOf(SuperheroNotFoundException.class);
    }

    @Test
    public void shouldReturnTrueIfSuperheroExistsByPseudonym() throws Exception {
        // given
        given(superheroRepository.existsByPseudonym(PSEUDONYM)).willReturn(true);

        // when
        boolean result = defaultSuperheroService.existsByPseudonym(PSEUDONYM);

        // then
        assertThat(result).isTrue();
    }

    @Test
    public void shouldReturnFalseIfSuperheroDoesNotExistByPseudonym() throws Exception {
        // given
        given(superheroRepository.existsByPseudonym(PSEUDONYM)).willReturn(false);

        // when
        boolean result = defaultSuperheroService.existsByPseudonym(PSEUDONYM);

        // then
        assertThat(result).isFalse();
    }

    private Superhero supermanWithId() {
        Superhero supermanWithId = superman();
        supermanWithId.setId(SUPERHERO_ID);
        return supermanWithId;
    }

}
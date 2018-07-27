package com.pzinsta.validator;

import com.pzinsta.model.Superhero;
import com.pzinsta.service.SuperheroService;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static com.pzinsta.util.Superheroes.superman;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class SuperheroValidatorTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @InjectMocks
    private SuperheroValidator superheroValidator;

    @Mock
    private SuperheroService superheroService;

    @Test
    public void shouldRejectSuperheroIfPseudonymAlreadyExists() throws Exception {
        // given
        Superhero superman = superman();
        given(superheroService.existsByPseudonym(superman.getPseudonym())).willReturn(true);
        Errors errors = new BeanPropertyBindingResult(superman, "superhero");
        
        // when
        superheroValidator.validate(superman, errors);
        
        // then
        assertThat(errors).matches(Errors::hasErrors);
    }

    @Test
    public void shouldNotRejectSuperheroIfPseudonymDoesNotExist() throws Exception {
        // given
        Superhero superman = superman();
        given(superheroService.existsByPseudonym(superman.getPseudonym())).willReturn(false);
        Errors errors = new BeanPropertyBindingResult(superman, "superhero");

        // when
        superheroValidator.validate(superman, errors);

        // then
        assertThat(errors.hasErrors()).isFalse();
    }

    @Test
    public void shouldSupportSuperheroClass() throws Exception {
        // given

        // when
        boolean result = superheroValidator.supports(Superhero.class);

        // then
        assertThat(result).isTrue();
    }
}
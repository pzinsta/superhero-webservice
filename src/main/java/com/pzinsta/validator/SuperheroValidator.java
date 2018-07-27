package com.pzinsta.validator;

import com.pzinsta.model.Superhero;
import com.pzinsta.service.SuperheroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SuperheroValidator implements Validator {

    private SuperheroService superheroService;

    @Autowired
    public SuperheroValidator(SuperheroService superheroService) {
        this.superheroService = superheroService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Superhero.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Superhero superhero = (Superhero) target;
        if (superheroService.existsByPseudonym(superhero.getPseudonym())) {
            errors.rejectValue("pseudonym", "not.unique", "must be unique");
        }
    }
}

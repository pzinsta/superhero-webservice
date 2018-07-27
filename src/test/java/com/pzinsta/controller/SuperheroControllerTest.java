package com.pzinsta.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.pzinsta.category.IntegrationTests;
import com.pzinsta.exception.SuperheroNotFoundException;
import com.pzinsta.model.Superhero;
import com.pzinsta.service.SuperheroService;
import com.pzinsta.validator.SuperheroValidator;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Errors;

import java.time.LocalDate;

import static com.pzinsta.util.Superheroes.batman;
import static com.pzinsta.util.Superheroes.superman;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

@Category(IntegrationTests.class)
@RunWith(SpringRunner.class)
@WebMvcTest(SuperheroController.class)
@EnableSpringDataWebSupport
public class SuperheroControllerTest {

    private final static Long SUPERHERO_ID = 42L;
    private static final String BLANK = "           ";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SuperheroService superheroService;

    @MockBean
    private SuperheroValidator superheroValidator;

    @Before
    public void setUp() throws Exception {
        when(superheroValidator.supports(Superhero.class)).thenReturn(true);
    }

    @Test
    public void shouldCreateSuperhero() throws Exception {
        // given
        given(superheroService.create(any(Superhero.class))).willReturn(supermanWithId());

        // when
        mockMvc.perform(post("/superheroes").accept(APPLICATION_JSON)
                .content(toJson(superman())).contentType(APPLICATION_JSON))

        // then
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", fromUriString("/superheroes/{id}").buildAndExpand(SUPERHERO_ID).toUriString()))
        .andExpect(content().json(toJson(supermanWithId())));
    }

    @Test
    public void shouldFindAllSuperheroes() throws Exception {
        // given
        given(superheroService.findAll()).willReturn(ImmutableList.of(superman(), batman()));

        // when
        mockMvc.perform(get("/superheroes").accept(APPLICATION_JSON))

        // then
        .andExpect(status().isOk()).andExpect(content().json(toJson(ImmutableList.of(superman(), batman()))));
    }

    @Test
    public void shouldFindAllSuperheroesPaged() throws Exception {
        // given
        Page<Superhero> page = new PageImpl<>(ImmutableList.of(superman(), batman()));
        given(superheroService.findAll(any(Pageable.class))).willReturn(page);

        // when
        mockMvc.perform(get("/superheroes").param("page", "0").accept(APPLICATION_JSON))

        // then
        .andExpect(status().isOk()).andExpect(content().json(toJson(page)));
    }

    @Test
    public void shouldFindAllSuperheroesSorted() throws Exception {
        // given
        given(superheroService.findAll(any(Sort.class))).willReturn(ImmutableList.of(batman(), superman()));

        // when
        mockMvc.perform(get("/superheroes").param("sort", "firstAppearance,desc")
                .accept(APPLICATION_JSON))

        // then
        .andExpect(status().isOk()).andExpect(content().json(toJson(ImmutableList.of(batman(), superman()))));
    }

    @Test
    public void shouldFindSuperheroById() throws Exception {
        // given
        given(superheroService.findById(SUPERHERO_ID)).willReturn(superman());

        // when
        mockMvc.perform(get("/superheroes/{id}", SUPERHERO_ID).accept(APPLICATION_JSON))

        // then
        .andExpect(status().isOk()).andExpect(content().json(toJson(superman())));
    }

    @Test
    public void shouldReturn404WhenCannotFindSuperheroById() throws Exception {
        // given
        given(superheroService.findById(SUPERHERO_ID)).willThrow(SuperheroNotFoundException.class);

        // when
        mockMvc.perform(get("/superheroes/{id}", SUPERHERO_ID).accept(APPLICATION_JSON))

        // then
        .andExpect(status().isNotFound());
    }

    @Test
    public void shouldFindSuperheroByPseudonym() throws Exception {
        // given
        String pseudonym = "Superman";
        given(superheroService.findByPseudonym(pseudonym)).willReturn(supermanWithId());

        // when
        mockMvc.perform(get("/superheroes").param("pseudonym", pseudonym).accept(APPLICATION_JSON))

        // then
        .andExpect(status().isOk()).andExpect(content().json(toJson(supermanWithId())));
    }

    @Test
    public void shouldReturn404WhenCannotFindSuperheroByPseudonym() throws Exception {
        // given
        String pseudonym = "Superman";
        given(superheroService.findByPseudonym(pseudonym)).willThrow(SuperheroNotFoundException.class);

        // when
        mockMvc.perform(get("/superheroes").param("pseudonym", pseudonym).accept(APPLICATION_JSON))

        // then
        .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotCreateSuperheroWithoutName() throws Exception {
        // given
        Superhero supermanWithoutName = superman();
        supermanWithoutName.setName(null);

        // when
        mockMvc.perform(post("/superheroes").content(toJson(supermanWithoutName))
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))

        // then
        .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateSuperheroWithBlankName() throws Exception {
        // given
        Superhero supermanWithBlankName = superman();
        supermanWithBlankName.setName(BLANK);

        // when
        mockMvc.perform(post("/superheroes").content(toJson(supermanWithBlankName))
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))

        // then
        .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateSuperheroWithoutPseudonym() throws Exception {
        // given
        Superhero supermanWithoutPseudonym = superman();
        supermanWithoutPseudonym.setPseudonym(null);

        // when
        mockMvc.perform(post("/superheroes").content(toJson(supermanWithoutPseudonym))
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))

        // then
        .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateSuperheroWithBlankPseudonym() throws Exception {
        // given
        Superhero supermanWithBlankPseudonym = superman();
        supermanWithBlankPseudonym.setPseudonym(BLANK);

        // when
        mockMvc.perform(post("/superheroes").content(toJson(supermanWithBlankPseudonym))
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))

        // then
        .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateSuperheroWithoutPublisher() throws Exception {
        // given
        Superhero supermanWithoutPublisher = superman();
        supermanWithoutPublisher.setPublisher(null);

        // when
        mockMvc.perform(post("/superheroes").content(toJson(supermanWithoutPublisher))
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))

        // then
        .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateSuperheroWithBlankPublisher() throws Exception {
        // given
        Superhero supermanWithBlankPublisher = superman();
        supermanWithBlankPublisher.setPublisher(BLANK);

        // when
        mockMvc.perform(post("/superheroes").content(toJson(supermanWithBlankPublisher))
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))

        // then
        .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateSuperheroWithoutSkills() throws Exception {
        // given
        Superhero supermanWithoutSkills = superman();
        supermanWithoutSkills.setSkills(ImmutableList.of());

        // when
        mockMvc.perform(post("/superheroes").content(toJson(supermanWithoutSkills))
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))

        // then
        .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateSuperheroWithNullSkills() throws Exception {
        // given
        Superhero supermanWithNullSkills = superman();
        supermanWithNullSkills.setSkills(Lists.newArrayList(null, null, null));

        // when
        mockMvc.perform(post("/superheroes").content(toJson(supermanWithNullSkills))
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))

        // then
        .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateSuperheroWithBlankSkills() throws Exception {
        // given
        Superhero supermanWithBlankSkills = superman();
        supermanWithBlankSkills.setSkills(ImmutableList.of(BLANK, BLANK));

        // when
        mockMvc.perform(post("/superheroes").content(toJson(supermanWithBlankSkills))
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))

        // then
        .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateSuperheroWithNullAllies() throws Exception {
        // given
        Superhero supermanWithNullAllies = superman();
        supermanWithNullAllies.setAllies(Lists.newArrayList(null, null));

        // when
        mockMvc.perform(post("/superheroes").content(toJson(supermanWithNullAllies))
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))

        // then
        .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateSuperheroWithBlankAllies() throws Exception {
        // given
        Superhero supermanWithBlankAllies = superman();
        supermanWithBlankAllies.setAllies(Lists.newArrayList(BLANK, BLANK));

        // when
        mockMvc.perform(post("/superheroes").content(toJson(supermanWithBlankAllies))
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))

        // then
        .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateSuperheroWithoutFirstAppearanceDate() throws Exception {
        // given
        Superhero supermanWithoutFirstAppearance = superman();
        supermanWithoutFirstAppearance.setFirstAppearance(null);

        // when
        mockMvc.perform(post("/superheroes").content(toJson(supermanWithoutFirstAppearance))
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))

        // then
        .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateSuperheroWithFirstAppearanceDateInFuture() throws Exception {
        // given
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Superhero supermanWithFirstAppearanceInFuture = superman();
        supermanWithFirstAppearanceInFuture.setFirstAppearance(tomorrow);

        // when
        mockMvc.perform(post("/superheroes").content(toJson(supermanWithFirstAppearanceInFuture))
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))

        // then
        .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDenyCreationWhenSuperheroValidatorRejectsSuperhero() throws Exception {
        // given
        givenSuperheroValidatorRejectsSuperhero();

        // when
        mockMvc.perform(post("/superheroes").content(toJson(superman()))
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))

        // then
        .andExpect(status().isBadRequest());
    }

    private void givenSuperheroValidatorRejectsSuperhero() {
        willAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("pseudonym", "not.unique");
            return null;
        }).given(superheroValidator).validate(any(Superhero.class), any(Errors.class));
    }

    private String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    private static Superhero supermanWithId() {
        Superhero superman = superman();
        superman.setId(SUPERHERO_ID);
        return superman;
    }

}
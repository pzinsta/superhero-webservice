package com.pzinsta;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.pzinsta.category.EndToEndTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import static com.google.common.base.Charsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@Category(EndToEndTests.class)
@RunWith(SpringRunner.class)
@AutoConfigureWebTestClient
@AutoConfigureJsonTesters
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class SuperheroApplicationTests {

    private static final String SUPERHEROES_URL = "/superheroes";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private BasicJsonTester jsonTester;

    @Test
    public void shouldDenyAccessForUnauthenticatedUsers() throws Exception {
        // given

        // when
        ResponseEntity<String> response = testRestTemplate.getForEntity(SUPERHEROES_URL, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void shouldAllowAccessForAuthenticatedUsers() throws Exception {
        // given

        // when
        ResponseEntity<String> response = testRestTemplateWithBasicAuth().getForEntity(SUPERHEROES_URL, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void shouldCreateSuperhero() throws Exception {
        // given
        HttpEntity<?> request = httpEntityFromFile("request-superman.json");

        // when
        ResponseEntity<String> response = testRestTemplateWithBasicAuth()
                .postForEntity(SUPERHEROES_URL, request, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getHeaders().getLocation()).hasPath("/superheroes/1000");
        assertThat(jsonTester.from(response.getBody())).isEqualToJson(jsonFromFile("shouldCreateSuperhero/response.json"));
    }

    @Test
    public void shouldGetListOfSuperheroes() throws Exception {
        // given
        postSuperheroesFromFiles("request-superman.json", "request-captain-america.json", "request-batman.json");

        // when
        ResponseEntity<String> response = testRestTemplateWithBasicAuth().getForEntity(SUPERHEROES_URL, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(jsonTester.from(response.getBody())).isEqualToJson(jsonFromFile("shouldGetListOfSuperheroes/response.json"));
    }

    @Test
    public void shouldGetPagedListOfSuperheroesWhenPageParameterIsPresent() throws Exception {
        // given
        postSuperheroesFromFiles("request-superman.json", "request-captain-america.json", "request-batman.json");
        URI uri = UriComponentsBuilder.fromPath(SUPERHEROES_URL)
                .queryParam("page", 0)
                .build().toUri();

        // when
        ResponseEntity<String> response = testRestTemplateWithBasicAuth().getForEntity(uri, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(jsonTester.from(response.getBody())).isEqualToJson(jsonFromFile("shouldGetPagedListOfSuperheroes/response.json"));
    }

    @Test
    public void shouldLimitNumberOfSuperheroesInPagedList() throws Exception {
        // given
        postSuperheroesFromFiles("request-superman.json", "request-captain-america.json", "request-batman.json");
        URI uri = UriComponentsBuilder.fromPath(SUPERHEROES_URL)
                .queryParam("page", 1)
                .queryParam("size", 1)
                .build().toUri();

        // when
        ResponseEntity<String> response = testRestTemplateWithBasicAuth().getForEntity(uri, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(jsonTester.from(response.getBody())).isEqualToJson(jsonFromFile("shouldLimitNumberOfSuperheroesInPagedList/response.json"));
    }

    @Test
    public void shouldApplySortingToPagedList() throws Exception {
        // given
        postSuperheroesFromFiles("request-superman.json", "request-captain-america.json", "request-batman.json");
        URI uri = UriComponentsBuilder.fromPath(SUPERHEROES_URL)
                .queryParam("page", 1)
                .queryParam("size", 1)
                .queryParam("sort", "firstAppearance,asc")
                .build().toUri();

        // when
        ResponseEntity<String> response = testRestTemplateWithBasicAuth().getForEntity(uri, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(jsonTester.from(response.getBody())).isEqualToJson(jsonFromFile("shouldApplySortingToPagedList/response.json"));
    }

    @Test
    public void shouldGetSortedListOfSuperheroes() throws Exception {
        // given
        postSuperheroesFromFiles("request-superman.json", "request-captain-america.json", "request-batman.json");
        URI uri = UriComponentsBuilder.fromPath(SUPERHEROES_URL)
                .queryParam("sort", "publisher,desc")
                .queryParam("sort", "pseudonym,asc")
                .build().toUri();

        // when
        ResponseEntity<String> response = testRestTemplateWithBasicAuth().getForEntity(uri, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(jsonTester.from(response.getBody())).isEqualToJson(jsonFromFile("shouldGetSortedListOfSuperheroes/response.json"));
    }

    @Test
    public void shouldGetSuperheroFromResourceLocation() throws Exception {
        // given
        URI location = testRestTemplateWithBasicAuth()
                .postForLocation(SUPERHEROES_URL, httpEntityFromFile("request-superman.json"));

        // when
        ResponseEntity<String> response = testRestTemplateWithBasicAuth().getForEntity(location, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(jsonTester.from(response.getBody())).isEqualToJson(jsonFromFile("shouldGetSuperheroFromResourceLocation/response.json"));
    }

    @Test
    public void shouldGetSuperheroById() throws Exception {
        // given
        testRestTemplateWithBasicAuth().postForLocation(SUPERHEROES_URL, httpEntityFromFile("request-superman.json"));

        // when
        ResponseEntity<String> response = testRestTemplateWithBasicAuth()
                .getForEntity("/superheroes/{id}", String.class, 1000);

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(jsonTester.from(response.getBody())).isEqualToJson(jsonFromFile("shouldGetSuperheroById/response.json"));
    }

    @Test
    public void shouldGetSuperheroByPseudonym() throws Exception {
        // given
        testRestTemplateWithBasicAuth().postForLocation(SUPERHEROES_URL, httpEntityFromFile("request-superman.json"));
        URI uri = UriComponentsBuilder.fromPath(SUPERHEROES_URL)
                .queryParam("pseudonym", "Superman")
                .build().toUri();

        // when
        ResponseEntity<String> response = testRestTemplateWithBasicAuth().getForEntity(uri, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(jsonTester.from(response.getBody())).isEqualToJson(jsonFromFile("shouldGetSuperheroByPseudonym/response.json"));
    }

    private HttpEntity<?> httpEntityFromFile(String resourceName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        headers.setAccept(ImmutableList.of(APPLICATION_JSON));
        return new HttpEntity<>(jsonFromFile(resourceName), headers);
    }

    private String jsonFromFile(String resourceName) {
        try {
            return Resources.toString(Resources.getResource(SuperheroApplicationTests.class, resourceName), UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TestRestTemplate testRestTemplateWithBasicAuth() {
        return testRestTemplate.withBasicAuth("user", "s3cr3t");
    }

    private void postSuperheroesFromFiles(String... files) throws IOException {
        Arrays.stream(files).forEach(this::postSuperheroFromFile);
    }

    private void postSuperheroFromFile(String file) {
        testRestTemplateWithBasicAuth().postForLocation(SUPERHEROES_URL, httpEntityFromFile(file));
    }

}

package ar.edu.utn.frc.tup.lciii.controllers;

import ar.edu.utn.frc.tup.lciii.exception.CountryNotFoundException;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.service.CountryService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc

public class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryService countryService;

    private List<Country> countries;

    @BeforeEach
    void init() {
        countries = new ArrayList<>();
        List<String> borders1 = new ArrayList<>();
        borders1.add("BRA");
        borders1.add("CHI");
        Map<String, String> lang1 = new HashMap<>();
        lang1.put("SPA", "Spanish");

        countries.add(
                Country.builder()
                        .name("Argentina")
                        .code("ARG")
                        .population(10)
                        .area(10)
                        .region("America")
                        .borders(borders1)
                        .languages(lang1)
                        .build()
        );
        List<String> borders2 = new ArrayList<>();
        borders2.add("PER");


        countries.add(
                Country.builder()
                        .name("Chile")
                        .code("CHI")
                        .population(10)
                        .area(10)
                        .region("America")
                        .borders(borders2)
                        .build()
        );

        countries.add(
                Country.builder()
                        .name("Bolivia")
                        .code("BOL")
                        .population(10)
                        .area(10)
                        .region("America")
                        .languages(lang1)
                        .build()
        );
    }

    @Test
    void getAllCountriesOKTest() throws Exception {
        given(countryService.getAllCountries()).willReturn(countries);

        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size()").value(3));
    }

    @Test
    void getCountriesByNameTest() throws Exception {
        List<Country> countriesResult = new ArrayList<>();
        countriesResult.add(Country.builder().name("Argentina").build());
        given(countryService.getCountriesByName("arg")).willReturn(countriesResult);

        mockMvc.perform(get("/api/countries?name=arg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size()").value(1))
                .andExpect(jsonPath("[0].name").value("Argentina"));
    }

    @Test
    void getCountriesByCodeTest() throws Exception {
        List<Country> countriesResult = new ArrayList<>();
        countriesResult.add(Country.builder().code("ARG").build());
        given(countryService.getCountriesByCode("arg")).willReturn(countriesResult);

        mockMvc.perform(get("/api/countries?code=arg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size()").value(1))
                .andExpect(jsonPath("[0].code").value("ARG"));
    }

    @Test
    void getCountriesByNameNotFoundTest() throws Exception {
        given(countryService.getCountriesByName("arg")).willThrow(new CountryNotFoundException("Not countries found with name provided."));

        mockMvc.perform(get("/api/countries?name=arg"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not countries found with name provided."));
    }
    @Test
    void getCountriesByCodeNotFoundTest() throws Exception {
        given(countryService.getCountriesByCode("arg")).willThrow(new CountryNotFoundException("Not countries found with code provided."));

        mockMvc.perform(get("/api/countries?code=arg"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not countries found with code provided."));
    }

    @Test
    void getCountriesByLanguageTest() throws Exception {
        List<Country> countriesResult = new ArrayList<>();
        countriesResult.add(Country.builder().code("ARG").build());
        given(countryService.getCountriesByLanguage("Spanish")).willReturn(countriesResult);

        mockMvc.perform(get("/api/countries/Spanish/language"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size()").value(1))
                .andExpect(jsonPath("[0].code").value("ARG"));
    }
    @Test
    void getCountriesByContinentTest() throws Exception {
        List<Country> countriesResult = new ArrayList<>();
        countriesResult.add(Country.builder().code("ARG").build());
        given(countryService.getCountriesByContinent("America")).willReturn(countriesResult);

        mockMvc.perform(get("/api/countries/America/continent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size()").value(1))
                .andExpect(jsonPath("[0].code").value("ARG"));
    }
    @Test
    void getCountriesMostBordersTest() throws Exception {
        given(countryService.getCountryMostBorders()).willReturn(Country.builder().name("Chile").area(10).build());

        mockMvc.perform(get("/api/countries/most-borders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size()").value(1))
                .andExpect(jsonPath("name").value("Chile"))
                .andExpect(jsonPath("area").value(10));
    }

}

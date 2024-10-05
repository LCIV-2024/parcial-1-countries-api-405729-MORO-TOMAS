package ar.edu.utn.frc.tup.lciii.services;

import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.repository.CountryRepository;
import ar.edu.utn.frc.tup.lciii.service.CountryService;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class CountryServiceTest {

    @MockBean
    private CountryRepository repository;
    @MockBean
    private ModelMapper modelMapper;
    @InjectMocks
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
    void getCountriesByNameTest() {
        when(countryService.getAllCountries()).thenReturn(countries);

        List<Country> countriesResult = countryService.getCountriesByName("arge");

        assertEquals(1, countriesResult.size());
        assertEquals("Argentina", countriesResult.get(0).getName());
    }

}

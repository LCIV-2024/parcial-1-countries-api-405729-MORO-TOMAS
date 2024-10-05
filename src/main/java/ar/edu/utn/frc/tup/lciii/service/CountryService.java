package ar.edu.utn.frc.tup.lciii.service;

import ar.edu.utn.frc.tup.lciii.dtos.CountrySaveDTO;
import ar.edu.utn.frc.tup.lciii.entities.CountryEntity;
import ar.edu.utn.frc.tup.lciii.exception.CountryNotFoundException;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {

    @Autowired
    private final CountryRepository countryRepository;

    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    private final ModelMapper modelMapper;
    private List<Country> CACHE_COUNTRY_LIST;

    public List<Country> getAllCountries() {
        if (CACHE_COUNTRY_LIST == null) {
            String url = "https://restcountries.com/v3.1/all";
            List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);
            CACHE_COUNTRY_LIST = response.stream().map(this::mapToCountry).collect(Collectors.toList());
        }
        return CACHE_COUNTRY_LIST;


    }

    /**
     * Agregar mapeo de campo cca3 (String)
     * Agregar mapeo campos borders ((List<String>))
     */
    private Country mapToCountry(Map<String, Object> countryData) {
        Map<String, Object> nameData = (Map<String, Object>) countryData.get("name");
        return Country.builder()
                .name((String) nameData.get("common"))
                .code((String) countryData.get("cca3"))
                .population(((Number) countryData.get("population")).longValue())
                .area(((Number) countryData.get("area")).doubleValue())
                .region((String) countryData.get("region"))
                .borders((List<String>) countryData.get("borders"))
                .languages((Map<String, String>) countryData.get("languages"))
                .build();
    }

    public List<Country> getCountriesByName(String countryName) {
        List<Country> allCountries = getAllCountries();
        List<Country> countriesResult = new ArrayList<>();
        for (Country c : allCountries) {
            if (c.getName().toLowerCase().contains(countryName.toLowerCase())) {
                countriesResult.add(c);
            }
        }
        if (countriesResult.isEmpty()) {
            throw new CountryNotFoundException("No se encontraron paises con ese nombre.");
        }
        return countriesResult;
    }

    public List<Country> getCountriesByCode(String countryCode) {
        List<Country> allCountries = getAllCountries();
        List<Country> countriesResult = new ArrayList<>();
        for (Country c : allCountries) {
            if (c.getCode().toLowerCase().contains(countryCode.toLowerCase())) {
                countriesResult.add(c);
            }
        }
        if (countriesResult.isEmpty()) {
            throw new CountryNotFoundException("No se encontraron paises con ese codigo.");
        }
        return countriesResult;
    }

    public List<Country> getCountriesByContinent(String continent) {
        List<Country> allCountries = getAllCountries();
        List<Country> countriesResult = new ArrayList<>();
        for (Country c : allCountries) {
            if (c.getRegion().equalsIgnoreCase(continent)) {
                countriesResult.add(c);
            }
        }

        if (countriesResult.isEmpty()) {
            throw new CountryNotFoundException("No se encontraron paises con ese continent.");
        }
        return countriesResult;
    }

    public List<Country> getCountriesByLanguage(String language) {
        List<Country> allCountries = getAllCountries();
        List<Country> countriesResult = new ArrayList<>();
        for (Country c : allCountries) {
            if ((c.getLanguages() != null && !c.getLanguages().isEmpty()) && c.getLanguages().containsValue(language)) {
                countriesResult.add(c);
            }
        }

        if (countriesResult.isEmpty()) {
            throw new CountryNotFoundException("No se encontraron paises con ese lenguage.");
        }
        return countriesResult;
    }

    public Country getCountryMostBorders() {
        List<Country> allCountries = getAllCountries();
        Country result = allCountries.get(0);
        int bordersQuantity = 0;

        for (int i = 0; i < allCountries.size() - 1; i++) {
            Country c = allCountries.get(i);
            if ((c.getBorders() != null && !c.getBorders().isEmpty()) && c.getBorders().size() > bordersQuantity) {
                bordersQuantity = c.getBorders().size();
                result = c;
            }
        }
        return result;
    }

    public List<Country> saveCountries(CountrySaveDTO dto) {
        List<Country> allCountries = getAllCountries();
        Collections.shuffle(allCountries);

        List<Country> countriesToSave = allCountries.subList(0, dto.getAmountOfCountryToSave() - 1);

        List<CountryEntity> entities = modelMapper.map(countriesToSave, new TypeToken<List<CountryEntity>>() {
        }.getType());

        List<CountryEntity> entitiesSaved = countryRepository.saveAll(entities);
        return modelMapper.map(entitiesSaved, new TypeToken<List<Country>>() {
        }.getType());
    }
}
package ar.edu.utn.frc.tup.lciii.controllers;
import ar.edu.utn.frc.tup.lciii.dtos.CountryDTO;
import ar.edu.utn.frc.tup.lciii.dtos.CountrySaveDTO;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.service.CountryService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/countries")

public class CountryController {

    @Autowired
    private final CountryService countryService;

    @Autowired
    private final ModelMapper modelMapper;

    @GetMapping("")
    public ResponseEntity<List<CountryDTO>> getAll(@RequestParam("name") Optional<String> countryName,
                                                   @RequestParam("code") Optional<String> countryCode) {
        List<Country> countries = new ArrayList<>();
        if (countryName.isEmpty() && countryCode.isEmpty()){

            countries = countryService.getAllCountries();

        }else{
            if (countryName.isPresent()){
                countries = countryService.getCountriesByName(countryName.get());
            } else {
                if (countryCode.isPresent()) {
                    countries = countryService.getCountriesByCode(countryCode.get());
                }
            }
        }

        List<CountryDTO> countriesDTO = modelMapper.map(countries, new TypeToken<List<CountryDTO>>() {
        }.getType());
        return ResponseEntity.ok(countriesDTO);
    }

    @GetMapping("{continent}/continent")
    public ResponseEntity<List<CountryDTO>> getByContinent(@PathVariable("continent") String continent) {
        List<Country> countries = countryService.getCountriesByContinent(continent);
        List<CountryDTO> countriesDTO = modelMapper.map(countries, new TypeToken<List<CountryDTO>>() {
        }.getType());
        return ResponseEntity.ok(countriesDTO);
    }

    @GetMapping("{language}/language")
    public ResponseEntity<List<CountryDTO>> getByLanguage(@PathVariable("language") String language) {
        List<Country> countries = countryService.getCountriesByLanguage(language);
        List<CountryDTO> countriesDTO = modelMapper.map(countries, new TypeToken<List<CountryDTO>>() {
        }.getType());
        return ResponseEntity.ok(countriesDTO);
    }
    @GetMapping("most-borders")
    public ResponseEntity<CountryDTO> getCountryMostBorders() {
        Country country = countryService.getCountryMostBorders();

        return ResponseEntity.ok(modelMapper.map(country, CountryDTO.class));
    }
    @PostMapping("")
    public ResponseEntity<CountryDTO> saveCountries(@Valid @RequestBody CountrySaveDTO dto) {
        if (dto.getAmountOfCountryToSave() >=10 ||dto.getAmountOfCountryToSave() < 1) {
            throw new IllegalArgumentException("El valor debe ser menor a 10 y mayor a 0");
        }

        List<Country> countries = countryService.saveCountries(dto);

        return ResponseEntity.ok(modelMapper.map(countries, CountryDTO.class));
    }
}

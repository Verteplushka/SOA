package edu.itmo.soa.service1.ctrl;

import edu.itmo.soa.service1.dto.request.CityInput;
import edu.itmo.soa.service1.dto.request.CitySearchRequest;
import edu.itmo.soa.service1.entity.City;
import edu.itmo.soa.service1.dto.error.ErrorResponse;
import edu.itmo.soa.service1.exception.CityAlreadyExistsException;
import edu.itmo.soa.service1.exception.CityNotFoundException;
import edu.itmo.soa.service1.exception.InvalidCityDataException;
import edu.itmo.soa.service1.service.CityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/cities", produces = MediaType.APPLICATION_XML_VALUE)
public class CityController {

    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCityById(@PathVariable("id") int id) {
        return ResponseEntity.ok().body(cityService.findById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> createCity(@Valid @RequestBody CityInput input) {
        City saved = cityService.createCity(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> updateCity(@PathVariable("id") int id, @Valid @RequestBody CityInput input) {
        City updated = cityService.updateCity(id, input);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCityById(@PathVariable("id") int id) {
        cityService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> searchCities(@RequestBody CitySearchRequest request) {
        try {
            return ResponseEntity.ok(cityService.searchCities(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("BAD_REQUEST", e.getMessage(), ZonedDateTime.now()));
        }
    }

    @DeleteMapping("/by-meters-above-sea-level")
    public ResponseEntity<?> deleteCityByMetersAboveSeaLevel(@RequestParam(value = "meters", required = false) Integer meters) {
        if(meters == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("BAD_REQUEST", "Параметр metersAboveSeaLevel не может быть пустой", ZonedDateTime.now()));
        }
        cityService.deleteByMetersAboveSeaLevel(meters);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-name-prefix")
    public ResponseEntity<?> getCitiesByNamePrefix(@RequestParam("prefix") String prefix) {
        List<City> cities = cityService.findByNamePrefix(prefix);
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/by-governor-age")
    public ResponseEntity<?> getCitiesByGovernorAge(@RequestParam(value = "age", required = false) Integer age) {
        if (age == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("BAD_REQUEST", "Параметр age не может быть пустой", ZonedDateTime.now()));
        }
        List<City> cities = cityService.getCitiesByGovernorAge(age);
        return ResponseEntity.ok(cities);
    }


    @ExceptionHandler(CityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCityNotFoundById(CityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_XML)
                .body(new ErrorResponse("NOT_FOUND", exception.getMessage(), ZonedDateTime.now()));
    }

    @ExceptionHandler(InvalidCityDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCityData(InvalidCityDataException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_XML)
                .body(new ErrorResponse("BAD_REQUEST", exception.getMessage(), ZonedDateTime.now()));
    }

    @ExceptionHandler(CityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCityAlreadyExists(CityAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_XML)
                .body(new ErrorResponse("CONFLICT", exception.getMessage(), ZonedDateTime.now()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_XML)
                .body(new ErrorResponse("NOT_FOUND", exception.getMessage(), ZonedDateTime.now()));
    }
}


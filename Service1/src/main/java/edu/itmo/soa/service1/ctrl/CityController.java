package edu.itmo.soa.service1.ctrl;

import edu.itmo.soa.service1.dto.request.CityInput;
import edu.itmo.soa.service1.dto.request.CitySearchRequest;
import edu.itmo.soa.service1.dto.response.CitiesResponse;
import edu.itmo.soa.service1.dto.response.CityPageResponse;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

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
        City city = cityService.findById(id);

        city.add(linkTo(methodOn(CityController.class).getCityById(id)).withSelfRel());
        city.add(linkTo(methodOn(CityController.class).deleteCityById(id)).withRel("delete"));
        city.add(linkTo(methodOn(CityController.class).updateCity(id, null)).withRel("update"));
        city.add(linkTo(methodOn(CityController.class).searchCities(null)).withRel("search"));

        return ResponseEntity.ok(city);
    }

    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> createCity(@Valid @RequestBody CityInput input) {
        City saved = cityService.createCity(input);

        saved.add(linkTo(methodOn(CityController.class).createCity(null)).withSelfRel());
        saved.add(linkTo(methodOn(CityController.class).getCityById(saved.getId())).withRel("get"));
        saved.add(linkTo(methodOn(CityController.class).updateCity(saved.getId(), null)).withRel("update"));
        saved.add(linkTo(methodOn(CityController.class).deleteCityById(saved.getId())).withRel("delete"));
        saved.add(linkTo(methodOn(CityController.class).searchCities(null)).withRel("search"));

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> updateCity(@PathVariable("id") int id, @Valid @RequestBody CityInput input) {
        City updated = cityService.updateCity(id, input);

        updated.add(linkTo(methodOn(CityController.class).updateCity(id, null)).withSelfRel());
        updated.add(linkTo(methodOn(CityController.class).getCityById(id)).withRel("get"));
        updated.add(linkTo(methodOn(CityController.class).deleteCityById(updated.getId())).withRel("delete"));
        updated.add(linkTo(methodOn(CityController.class).searchCities(null)).withRel("search"));

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
            CityPageResponse response = cityService.searchCities(request);

            addLinksToCities(response.getCities());
            response.add(linkTo(methodOn(CityController.class).searchCities(request)).withSelfRel());

            if(response.getPagination().getCurrentPage() > 0) {
                response.add(linkTo(methodOn(CityController.class).searchCities(null)).withRel("prevPage"));
            }

            if(response.getPagination().getCurrentPage() < response.getPagination().getTotalPages() - 1) {
                response.add(linkTo(methodOn(CityController.class).searchCities(null)).withRel("nextPage"));
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("BAD_REQUEST", e.getMessage(), ZonedDateTime.now()));
        }
    }

    @DeleteMapping("/by-meters-above-sea-level")
    public ResponseEntity<?> deleteCityByMetersAboveSeaLevel(@RequestParam(value = "meters", required = false) Integer meters) {
        if (meters == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("BAD_REQUEST", "Параметр metersAboveSeaLevel не может быть пустой", ZonedDateTime.now()));
        }
        cityService.deleteByMetersAboveSeaLevel(meters);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-name-prefix")
    public ResponseEntity<?> getCitiesByNamePrefix(@RequestParam("prefix") String prefix) {
        CitiesResponse response = cityService.findByNamePrefix(prefix);

        addLinksToCities(response.getCities());
        response.add(linkTo(methodOn(CityController.class).getCitiesByNamePrefix(prefix)).withSelfRel());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-governor-age")
    public ResponseEntity<?> getCitiesByGovernorAge(@RequestParam(value = "age", required = false) Integer age) {
        if (age == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("BAD_REQUEST", "Параметр age не может быть пустой", ZonedDateTime.now()));
        }

        CitiesResponse response = cityService.getCitiesByGovernorAge(age);

        addLinksToCities(response.getCities());
        response.add(linkTo(methodOn(CityController.class).getCitiesByGovernorAge(age)).withSelfRel());

        return ResponseEntity.ok(response);
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

    private void addLinksToCities(List<City> cities) {
        for (City city : cities) {
            city.add(linkTo(methodOn(CityController.class).getCityById(city.getId())).withRel("get"));
            city.add(linkTo(methodOn(CityController.class).updateCity(city.getId(), null)).withRel("update"));
            city.add(linkTo(methodOn(CityController.class).deleteCityById(city.getId())).withRel("delete"));
        }
    }
}


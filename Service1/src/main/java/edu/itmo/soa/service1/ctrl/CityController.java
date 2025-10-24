package edu.itmo.soa.service1.ctrl;

import edu.itmo.soa.service1.dto.request.CityInput;
import edu.itmo.soa.service1.entity.City;
import edu.itmo.soa.service1.entity.Coordinates;
import edu.itmo.soa.service1.entity.Human;
import edu.itmo.soa.service1.dto.error.ErrorResponse;
import edu.itmo.soa.service1.exception.CityAlreadyExistsException;
import edu.itmo.soa.service1.exception.InvalidCityDataException;
import edu.itmo.soa.service1.service.CityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping(value = "/cities", produces = MediaType.APPLICATION_XML_VALUE)
public class CityController {

    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService){
        this.cityService = cityService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCityById(@PathVariable int id) {
        return cityService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(
                                "NOT_FOUND",
                                "Город с ID " + id + " не найден",
                                ZonedDateTime.now()
                        )));
    }

    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> createCity(@Valid @RequestBody CityInput input) {
        try {
            City saved = cityService.createCity(input);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (CityAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("CONFLICT", e.getMessage(), ZonedDateTime.now()));
        } catch (InvalidCityDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("BAD_REQUEST", e.getMessage(), ZonedDateTime.now()));
        }
    }
}


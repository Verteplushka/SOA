package edu.itmo.soa.newservice2.eurekaclient.controller;

import edu.itmo.soa.newservice2.eurekaclient.exception.*;
import edu.itmo.soa.newservice2.eurekaclient.model.*;
import edu.itmo.soa.newservice2.eurekaclient.model.response.ErrorResponse;
import edu.itmo.soa.newservice2.eurekaclient.model.response.PopulationResponse;
import edu.itmo.soa.newservice2.eurekaclient.model.response.RelocationResponse;
import edu.itmo.soa.newservice2.eurekaclient.service.CityServiceClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping(value = "/genocide", produces = MediaType.APPLICATION_XML_VALUE)
public class GenocideController {

    private final CityServiceClient cityServiceClient;

    public GenocideController(CityServiceClient cityServiceClient) {
        this.cityServiceClient = cityServiceClient;
    }

    @GetMapping("/city/{id}")
    public ResponseEntity<?> getCityById(@PathVariable int id) {
        try {
            City city = cityServiceClient.getCity(id);
            return ResponseEntity.ok(city);
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("NOT_FOUND", "Город с id " + id + " не найден"));
        } catch (ApiErrorException e) {
            return ResponseEntity.status(500).body(new ErrorResponse("SERVICE1_API_ERROR", e.getMessage()));
        } catch (NetworkErrorException e) {
            return ResponseEntity.status(500).body(new ErrorResponse("SERVICE1_CONNECTION_ERROR", e.getMessage()));
        }
    }

    @PostMapping("/count/{id1}/{id2}/{id3}")
    public ResponseEntity<?> countPopulation(@PathVariable int id1, @PathVariable int id2, @PathVariable int id3) {
        try {
            City c1 = cityServiceClient.getCity(id1);
            City c2 = cityServiceClient.getCity(id2);
            City c3 = cityServiceClient.getCity(id3);

            long totalPopulation = c1.getPopulation() + c2.getPopulation() + c3.getPopulation();
            PopulationResponse result = new PopulationResponse();
            result.setTotalPopulation(totalPopulation);
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("NOT_FOUND", "Один или несколько городов не найдены"));
        } catch (ApiErrorException e) {
            return ResponseEntity.status(500).body(new ErrorResponse("SERVICE1_API_ERROR", e.getMessage()));
        } catch (NetworkErrorException e) {
            return ResponseEntity.status(500).body(new ErrorResponse("SERVICE1_CONNECTION_ERROR", e.getMessage()));
        }
    }

    @PostMapping("/move-to-poorest/{id}")
    public ResponseEntity<?> moveToPoorest(@PathVariable int id) {
        try {
            List<City> cities = cityServiceClient.getAllCities();
            City source = cityServiceClient.getCity(id);

            City poorest = cities.stream()
                    .filter(c -> c.getId() != source.getId())
                    .min(Comparator.comparingDouble(City::getPopulationDensity))
                    .orElse(null);

            if (poorest == null) {
                return ResponseEntity.badRequest().body(new ErrorResponse("INVALID_OPERATION", "Нет подходящего города для переселения"));
            }

            poorest.setPopulation(poorest.getPopulation() + source.getPopulation());
            source.setPopulation(0);
            cityServiceClient.updateCity(poorest);
            cityServiceClient.updateCity(source);

            RelocationResponse response = new RelocationResponse();
            response.setSourceCity(source);
            response.setTargetCity(poorest);
            return ResponseEntity.ok(response);
        } catch (EmptyCitiesListException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("NOT_FOUND", "Нет доступных городов"));
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("NOT_FOUND", "Исходный город не найден"));
        } catch (ApiErrorException e) {
            return ResponseEntity.status(500).body(new ErrorResponse("SERVICE1_API_ERROR", e.getMessage()));
        } catch (NetworkErrorException e) {
            return ResponseEntity.status(500).body(new ErrorResponse("SERVICE1_CONNECTION_ERROR", e.getMessage()));
        }
    }
}


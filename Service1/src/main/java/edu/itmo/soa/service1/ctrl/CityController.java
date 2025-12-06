package edu.itmo.soa.service1.ctrl;

import edu.itmo.soa.service1.consul.ServiceDiscoveryClient;
import edu.itmo.soa.service1.dto.request.CityInput;
import edu.itmo.soa.service1.dto.request.CitySearchRequest;
import edu.itmo.soa.service1.dto.response.CitiesResponse;
import edu.itmo.soa.service1.dto.response.CityPageResponse;
import edu.itmo.soa.service1.entity.City;
import edu.itmo.soa.service1.dto.error.ErrorResponse;
import edu.itmo.soa.service1.entity.CityDto;
import edu.itmo.soa.service1.exception.CityAlreadyExistsException;
import edu.itmo.soa.service1.exception.CityNotFoundException;
import edu.itmo.soa.service1.exception.InvalidCityDataException;
import edu.itmo.soa.service1.CityServiceRemote;
import edu.itmo.soa.service1.util.CityMapper;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJBException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.Context;
import javax.naming.InitialContext;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/cities", produces = MediaType.APPLICATION_XML_VALUE)
public class CityController {
    private CityServiceRemote cityService;
    private final ServiceDiscoveryClient discoveryClient;
    private static final Logger log = Logger.getLogger("CityController");


    @Autowired
    public CityController(ServiceDiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @PostConstruct
    public void init() {
        try {
//            props.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
//            props.put(Context.SECURITY_PRINCIPAL, "ejb-user");
//            props.put(Context.SECURITY_CREDENTIALS, "12345678");

            log.info("info ale");
            log.warning("warn ale");
//            String providerUrl = discoveryClient.getService1ProviderUrl();
//            Properties props = new Properties();
//            props.put(Context.PROVIDER_URL, providerUrl);
//            props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");

            InitialContext ctx = new InitialContext();
            Object obj = ctx.lookup("java:jboss/exported/service1-ejb-1.0-SNAPSHOT/CityServiceBean!edu.itmo.soa.service1.CityServiceRemote");
            this.cityService = (CityServiceRemote) obj;
        } catch (Exception e) {
            throw new RuntimeException("Failed to lookup EJB", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCityById(@PathVariable("id") int id) {
        City city = cityService.findById(id);
        CityDto cityDto = CityMapper.toCityDto(city);

        cityDto.add(linkTo(methodOn(CityController.class).getCityById(id)).withSelfRel());
        cityDto.add(linkTo(methodOn(CityController.class).deleteCityById(id)).withRel("delete"));
        cityDto.add(linkTo(methodOn(CityController.class).updateCity(id, null)).withRel("update"));
        cityDto.add(linkTo(methodOn(CityController.class).searchCities(null)).withRel("search"));

        return ResponseEntity.ok(cityDto);
    }

    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> createCity(@Valid @RequestBody CityInput input) {
        City saved = cityService.createCity(input);
        CityDto cityDto = CityMapper.toCityDto(saved);

        cityDto.add(linkTo(methodOn(CityController.class).createCity(null)).withSelfRel());
        cityDto.add(linkTo(methodOn(CityController.class).getCityById(cityDto.getId())).withRel("get"));
        cityDto.add(linkTo(methodOn(CityController.class).updateCity(cityDto.getId(), null)).withRel("update"));
        cityDto.add(linkTo(methodOn(CityController.class).deleteCityById(cityDto.getId())).withRel("delete"));
        cityDto.add(linkTo(methodOn(CityController.class).searchCities(null)).withRel("search"));

        return ResponseEntity.status(HttpStatus.CREATED).body(cityDto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> updateCity(@PathVariable("id") int id, @Valid @RequestBody CityInput input) {
        City updated = cityService.updateCity(id, input);
        CityDto cityDto = CityMapper.toCityDto(updated);

        cityDto.add(linkTo(methodOn(CityController.class).updateCity(id, null)).withSelfRel());
        cityDto.add(linkTo(methodOn(CityController.class).getCityById(id)).withRel("get"));
        cityDto.add(linkTo(methodOn(CityController.class).deleteCityById(cityDto.getId())).withRel("delete"));
        cityDto.add(linkTo(methodOn(CityController.class).searchCities(null)).withRel("search"));

        return ResponseEntity.ok(cityDto);
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

            addLinksToCitiesDto(response.getCities());
            response.add(linkTo(methodOn(CityController.class).searchCities(request)).withSelfRel());

            if (response.getPagination().getCurrentPage() > 0) {
                response.add(linkTo(methodOn(CityController.class).searchCities(null)).withRel("prevPage"));
            }

            if (response.getPagination().getCurrentPage() < response.getPagination().getTotalPages() - 1) {
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

        addLinksToCitiesDto(response.getCities());
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

        addLinksToCitiesDto(response.getCities());
        response.add(linkTo(methodOn(CityController.class).getCitiesByGovernorAge(age)).withSelfRel());

        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(EJBException.class)
    public ResponseEntity<ErrorResponse> handleEJBException(EJBException exception) {
        Throwable cause = exception.getCause();
        if (cause instanceof CityNotFoundException) {
            CityNotFoundException ex = (CityNotFoundException) cause;
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_XML)
                    .body(new ErrorResponse("CITY_NOT_FOUND", ex.getMessage(), ZonedDateTime.now()));
        } else if (cause instanceof CityAlreadyExistsException) {
            CityAlreadyExistsException ex = (CityAlreadyExistsException) cause;
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_XML)
                    .body(new ErrorResponse("CITY_ALREADY_EXISTS", ex.getMessage(), ZonedDateTime.now()));
        } else if (cause instanceof InvalidCityDataException) {
            InvalidCityDataException ex = (InvalidCityDataException) cause;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_XML)
                    .body(new ErrorResponse("BAD_REQUEST", ex.getMessage(), ZonedDateTime.now()));
        } else if (cause instanceof IllegalArgumentException) {
            IllegalArgumentException ex = (IllegalArgumentException) cause;
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_XML)
                    .body(new ErrorResponse("NOT_FOUND", ex.getMessage(), ZonedDateTime.now()));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_XML)
                .body(new ErrorResponse("EJB_EXCEPTION", exception.getMessage(), ZonedDateTime.now()));
    }


    private void addLinksToCitiesDto(List<CityDto> citiesDto) {
        for (CityDto city : citiesDto) {
            city.add(linkTo(methodOn(CityController.class).getCityById(city.getId())).withRel("get"));
            city.add(linkTo(methodOn(CityController.class).updateCity(city.getId(), null)).withRel("update"));
            city.add(linkTo(methodOn(CityController.class).deleteCityById(city.getId())).withRel("delete"));
        }
    }
}


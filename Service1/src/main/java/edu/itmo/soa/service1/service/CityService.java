package edu.itmo.soa.service1.service;

import edu.itmo.soa.service1.dto.request.CityInput;
import edu.itmo.soa.service1.dto.request.CitySearchRequest;
import edu.itmo.soa.service1.dto.response.CityPageResponse;
import edu.itmo.soa.service1.entity.City;
import edu.itmo.soa.service1.entity.Coordinates;
import edu.itmo.soa.service1.entity.Human;
import edu.itmo.soa.service1.exception.CityAlreadyExistsException;
import edu.itmo.soa.service1.exception.CityNotFoundException;
import edu.itmo.soa.service1.exception.InvalidCityDataException;
import edu.itmo.soa.service1.repo.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public City findById(int id) {
        return cityRepository.findById(id).orElseThrow(() -> new CityNotFoundException(id));
    }

    public void deleteById(int id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException(id));
        cityRepository.delete(city);
    }

    public City createCity(CityInput input) {
        if (cityRepository.existsByName(input.getName())) {
            throw new CityAlreadyExistsException("Город с таким именем уже существует");
        }

        validateCityInput(input);

        City city = City.builder()
                .name(input.getName())
                .coordinates(new Coordinates(input.getCoordinates().getX(),
                        input.getCoordinates().getY()))
                .area(input.getArea())
                .population(input.getPopulation())
                .metersAboveSeaLevel(input.getMetersAboveSeaLevel())
                .establishmentDate(input.getEstablishmentDate())
                .populationDensity(input.getPopulationDensity())
                .government(input.getGovernment())
                .governor(new Human(input.getGovernor().getAge()))
                .build();

        return cityRepository.save(city);
    }

    public City updateCity(int id, CityInput input) {
        City existing = cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException(id));

        validateCityInput(input);

        City city = City.builder()
                .id(existing.getId())
                .name(input.getName())
                .coordinates(new Coordinates(input.getCoordinates().getX(),
                        input.getCoordinates().getY()))
                .area(input.getArea())
                .population(input.getPopulation())
                .metersAboveSeaLevel(input.getMetersAboveSeaLevel())
                .establishmentDate(input.getEstablishmentDate())
                .populationDensity(input.getPopulationDensity())
                .government(input.getGovernment())
                .governor(new Human(input.getGovernor().getAge()))
                .creationDate(existing.getCreationDate())
                .build();

        return cityRepository.save(city);
    }

    public CityPageResponse searchCities(CitySearchRequest request) {
        int page = request.getPagination().getPage();
        int size = request.getPagination().getSize();

        Sort sort = Sort.by(Sort.Direction.fromString(request.getSort().getDirection().name()),
                request.getSort().getField());

        List<City> allCities = cityRepository.findAll();
        List<City> filtered = new ArrayList<>();

        for (City city : allCities) {
            if (request.getFilter() != null) {
                boolean matches = true;

                if (request.getFilter().getName() != null &&
                        !city.getName().contains(request.getFilter().getName())) {
                    matches = false;
                }

                if (request.getFilter().getArea() != null) {
                    Integer min = request.getFilter().getArea().getMin();
                    Integer max = request.getFilter().getArea().getMax();
                    if ((min != null && city.getArea() < min) || (max != null && city.getArea() > max)) {
                        matches = false;
                    }
                }

                if (!matches) continue;
            }
            filtered.add(city);
        }

        int start = Math.min(page * size, filtered.size());
        int end = Math.min(start + size, filtered.size());
        List<City> pageContent = filtered.subList(start, end);

        CityPageResponse response = new CityPageResponse();
        CityPageResponse.Pagination pagination = new CityPageResponse.Pagination();
        pagination.setCurrentPage(page);
        pagination.setTotalPages((int) Math.ceil((double) filtered.size() / size));
        pagination.setPageSize(size);

        response.setPagination(pagination);
        response.setCities(pageContent);

        return response;
    }

    private void validateCityInput(CityInput input) {
        if (input.getName() == null || input.getName().trim().isEmpty()) {
            throw new InvalidCityDataException("Поле name не может быть null или пустым");
        }

        CityInput.CoordinatesInput coords = input.getCoordinates();
        if (coords == null) {
            throw new InvalidCityDataException("Поле coordinates не может быть null");
        }
        if (coords.getX() == null || coords.getX() > 220) {
            throw new InvalidCityDataException("coordinates.x не может быть null и должно быть <= 220");
        }
        if (coords.getY() == null) {
            throw new InvalidCityDataException("coordinates.y не может быть null");
        }

        if (input.getArea() == null || input.getArea() <= 0) {
            throw new InvalidCityDataException("Поле area должно быть > 0 и не null");
        }

        if (input.getPopulation() < 0) {
            throw new InvalidCityDataException("Поле population должно быть > 0");
        }

        if (input.getPopulationDensity() != null && input.getPopulationDensity() <= 0) {
            throw new InvalidCityDataException("populationDensity должно быть > 0");
        }

        if (input.getGovernment() == null) {
            throw new InvalidCityDataException("Поле government не может быть null");
        }

        CityInput.HumanInput governor = input.getGovernor();
        if (governor == null || governor.getAge() <= 0) {
            throw new InvalidCityDataException("Поле governor не может быть null, возраст должен быть > 0");
        }
    }
}


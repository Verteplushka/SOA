package edu.itmo.soa.service1.service;

import edu.itmo.soa.service1.dto.request.CityInput;
import edu.itmo.soa.service1.dto.request.CitySearchRequest;
import edu.itmo.soa.service1.dto.response.CityPageResponse;
import edu.itmo.soa.service1.entity.City;
import edu.itmo.soa.service1.entity.Coordinates;
import edu.itmo.soa.service1.entity.Government;
import edu.itmo.soa.service1.entity.Human;
import edu.itmo.soa.service1.exception.CityAlreadyExistsException;
import edu.itmo.soa.service1.exception.CityNotFoundException;
import edu.itmo.soa.service1.exception.InvalidCityDataException;
import edu.itmo.soa.service1.repo.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public City findById(int id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException("ID", id));
    }

    public void deleteById(int id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException("ID", id));
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
                .orElseThrow(() -> new CityNotFoundException("ID", id));

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

                if (request.getFilter().getPopulation() != null) {
                    Long min = request.getFilter().getPopulation().getMin();
                    Long max = request.getFilter().getPopulation().getMax();
                    if ((min != null && city.getPopulation() < min) || (max != null && city.getPopulation() > max)) {
                        matches = false;
                    }
                }

                if (request.getFilter().getMetersAboveSeaLevel() != null) {
                    Integer min = request.getFilter().getMetersAboveSeaLevel().getMin();
                    Integer max = request.getFilter().getMetersAboveSeaLevel().getMax();
                    if ((min != null && city.getMetersAboveSeaLevel() < min) ||
                            (max != null && city.getMetersAboveSeaLevel() > max)) {
                        matches = false;
                    }
                }

                if (request.getFilter().getPopulationDensity() != null) {
                    Float min = request.getFilter().getPopulationDensity().getMin();
                    Float max = request.getFilter().getPopulationDensity().getMax();
                    if ((min != null && city.getPopulationDensity() < min) ||
                            (max != null && city.getPopulationDensity() > max)) {
                        matches = false;
                    }
                }

                if (request.getFilter().getGovernor() != null && request.getFilter().getGovernor().getAge() != null) {
                    Integer min = request.getFilter().getGovernor().getAge().getMin();
                    Integer max = request.getFilter().getGovernor().getAge().getMax();
                    Integer age = city.getGovernor() != null ? city.getGovernor().getAge() : null;
                    if (age != null) {
                        if ((min != null && age < min) || (max != null && age > max)) {
                            matches = false;
                        }
                    } else if (min != null || max != null) {
                        matches = false;
                    }
                }

                if (request.getFilter().getGovernment() != null && !request.getFilter().getGovernment().isEmpty()) {
                    try {
                        Government filterGov = Government.valueOf(request.getFilter().getGovernment());
                        if (!filterGov.equals(city.getGovernment())) {
                            matches = false;
                        }
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("There is no such government " + request.getFilter().getGovernment());
                    }
                }

                if (request.getFilter().getCoordinates() != null) {
                    if (city.getCoordinates() != null) {
                        if (request.getFilter().getCoordinates().getX() != null) {
                            Double min = request.getFilter().getCoordinates().getX().getMin();
                            Double max = request.getFilter().getCoordinates().getX().getMax();
                            if ((min != null && city.getCoordinates().getX() < min) ||
                                    (max != null && city.getCoordinates().getX() > max)) {
                                matches = false;
                            }
                        }
                        if (request.getFilter().getCoordinates().getY() != null) {
                            Double min = request.getFilter().getCoordinates().getY().getMin();
                            Double max = request.getFilter().getCoordinates().getY().getMax();
                            if ((min != null && city.getCoordinates().getY() < min) ||
                                    (max != null && city.getCoordinates().getY() > max)) {
                                matches = false;
                            }
                        }
                    } else {
                        matches = false;
                    }
                }

                if (request.getFilter().getEstablishmentDate() != null) {
                    LocalDateTime min = request.getFilter().getEstablishmentDate().getMin();
                    LocalDateTime max = request.getFilter().getEstablishmentDate().getMax();
                    LocalDateTime est = city.getEstablishmentDate();
                    if (est != null) {
                        if ((min != null && est.isBefore(min)) || (max != null && est.isAfter(max))) {
                            matches = false;
                        }
                    } else if (min != null || max != null) {
                        matches = false;
                    }
                }

                if (!matches) continue;
            }
            filtered.add(city);
        }


        if (request.getSort() != null && request.getSort().getField() != null) {
            Comparator<City> comparator = Comparator.comparing(city -> {
                Object value;
                switch (request.getSort().getField()) {
                    case "id": value = city.getId(); break;
                    case "name": value = city.getName(); break;
                    case "area": value = city.getArea(); break;
                    case "population": value = city.getPopulation(); break;
                    case "metersAboveSeaLevel": value = city.getMetersAboveSeaLevel(); break;
                    case "populationDensity": value = city.getPopulationDensity(); break;
                    case "government": value = city.getGovernment(); break;
                    case "governor.age": value = city.getGovernor() != null ? city.getGovernor().getAge() : null; break;
                    case "coordinates.x": value = city.getCoordinates() != null ? city.getCoordinates().getX() : null; break;
                    case "coordinates.y": value = city.getCoordinates() != null ? city.getCoordinates().getY() : null; break;
                    case "establishmentDate": value = city.getEstablishmentDate(); break;
                    default: value = null; break;
                }
                return (Comparable) value;
            }, Comparator.nullsLast(Comparator.naturalOrder()));

            if (request.getSort().getDirection() == CitySearchRequest.Sort.Direction.DESC) {
                comparator = comparator.reversed();
            }

            filtered.sort(comparator);
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

    public void deleteByMetersAboveSeaLevel(int meters) {
        List<City> cityList = cityRepository.findByMetersAboveSeaLevel(meters);
        if (cityList.isEmpty()) {
            throw new CityNotFoundException("metersAboveSeaLevel", meters);
        }
        cityRepository.delete(cityList.get(0));
    }

    public List<City> findByNamePrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            throw new IllegalArgumentException("Параметр prefix не должен быть пустой");
        }
        return cityRepository.findByNameStartingWith(prefix);
    }

    public List<City> getCitiesByGovernorAge(int age) {
        if (age <= 0) {
            throw new IllegalArgumentException("Параметр age должен быть больше 0");
        }
        return cityRepository.findByGovernorAgeGreaterThan(age);
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


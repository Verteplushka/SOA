package edu.itmo.soa.service1.service;

import edu.itmo.soa.service1.dto.request.CityInput;
import edu.itmo.soa.service1.entity.City;
import edu.itmo.soa.service1.entity.Coordinates;
import edu.itmo.soa.service1.entity.Human;
import edu.itmo.soa.service1.exception.CityAlreadyExistsException;
import edu.itmo.soa.service1.exception.CityNotFoundException;
import edu.itmo.soa.service1.exception.InvalidCityDataException;
import edu.itmo.soa.service1.repo.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        if (input.getArea() <= 0) {
            throw new InvalidCityDataException("Параметр area должен быть больше 0");
        }

        City city = City.builder()
                .name(input.getName())
                .coordinates(new Coordinates(input.getCoordinates().getX(), input.getCoordinates().getY()))
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
                .orElseThrow(() -> new IllegalArgumentException("Город с ID " + id + " не найден"));

        if (input.getName() == null || input.getArea() == null || input.getGovernment() == null) {
            throw new InvalidCityDataException("Некорректные параметры запроса");
        }

        existing.setName(input.getName());
        existing.setCoordinates(new Coordinates(input.getCoordinates().getX(), input.getCoordinates().getY()));
        existing.setArea(input.getArea());
        existing.setPopulation(input.getPopulation());
        existing.setMetersAboveSeaLevel(input.getMetersAboveSeaLevel());
        existing.setEstablishmentDate(input.getEstablishmentDate());
        existing.setPopulationDensity(input.getPopulationDensity());
        existing.setGovernment(input.getGovernment());
        existing.setGovernor(new Human(input.getGovernor().getAge()));

        return cityRepository.save(existing);
    }

}


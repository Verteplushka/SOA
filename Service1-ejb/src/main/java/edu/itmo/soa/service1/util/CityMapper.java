package edu.itmo.soa.service1.util;

import edu.itmo.soa.service1.dto.request.CityInput;
import edu.itmo.soa.service1.entity.City;
import edu.itmo.soa.service1.entity.CityDto;
import edu.itmo.soa.service1.entity.Coordinates;
import edu.itmo.soa.service1.entity.Human;

import java.util.ArrayList;
import java.util.List;

public class CityMapper {
    public static CityDto toCityDto(City city) {
        return new CityDto(
                city.getId(),
                city.getName(),
                city.getCoordinates(),
                city.getCreationDate(),
                city.getArea(),
                city.getPopulation(),
                city.getMetersAboveSeaLevel(),
                city.getEstablishmentDate(),
                city.getPopulationDensity(),
                city.getGovernment(),
                city.getGovernor()
        );
    }

    public static List<CityDto> tocityDtoList(List<City> cities) {
        List<CityDto> cityDtoList = new ArrayList<>();
        for(City city : cities) {
            cityDtoList.add(toCityDto(city));
        }
        return cityDtoList;
    }
    
    public static void copyFields(CityInput cityInput, City output) {
        output.setName(cityInput.getName());
        output.setCoordinates(new Coordinates(
                cityInput.getCoordinates().getX(),
                cityInput.getCoordinates().getY()
        ));
        output.setArea(cityInput.getArea());
        output.setPopulation(cityInput.getPopulation());
        output.setMetersAboveSeaLevel(cityInput.getMetersAboveSeaLevel());
        output.setEstablishmentDate(cityInput.getEstablishmentDate());
        output.setPopulationDensity(cityInput.getPopulationDensity());
        output.setGovernment(cityInput.getGovernment());
        output.setGovernor(new Human(cityInput.getGovernor().getAge()));
    }
}

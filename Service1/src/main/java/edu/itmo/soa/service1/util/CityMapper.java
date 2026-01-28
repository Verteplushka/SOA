package edu.itmo.soa.service1.util;

import edu.itmo.soa.service1.entity.City;
import edu.itmo.soa.service1.entity.CityDto;

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
}

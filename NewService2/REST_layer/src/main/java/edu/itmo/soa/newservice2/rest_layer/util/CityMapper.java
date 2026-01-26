package edu.itmo.soa.newservice2.rest_layer.util;

import edu.itmo.soa.newservice2.rest_layer.dto.CityDto;
import edu.itmo.soa.newservice2.rest_layer.dto.CoordinatesDto;
import edu.itmo.soa.newservice2.rest_layer.dto.GovernorDto;

public class CityMapper {

    public static CityDto fromSoap(com.example.soap.client.City city) {
        CityDto dto = new CityDto();
        dto.setId(city.getId());
        dto.setName(city.getName());

        if (city.getCoordinates() != null) {
            CoordinatesDto c = new CoordinatesDto();
            c.setId(city.getCoordinates().getId());
            c.setX(city.getCoordinates().getX());
            c.setY(city.getCoordinates().getY());
            dto.setCoordinates(c);
        }

        dto.setCreationDate(
                city.getCreationDate() != null
                        ? city.getCreationDate().toXMLFormat()
                        : null
        );

        dto.setEstablishmentDate(
                city.getEstablishmentDate() != null
                        ? city.getEstablishmentDate().toXMLFormat()
                        : null
        );

        dto.setPopulation(city.getPopulation());
        dto.setArea(city.getArea());
        dto.setMetersAboveSeaLevel(city.getMetersAboveSeaLevel());
        dto.setPopulationDensity(city.getPopulationDensity());

        if (city.getGovernment() != null) {
            dto.setGovernment(city.getGovernment().value());
        }

        if (city.getGovernor() != null) {
            GovernorDto g = new GovernorDto();
            g.setId(city.getGovernor().getId());
            g.setAge(city.getGovernor().getAge());
            dto.setGovernor(g);
        }

        return dto;
    }
}

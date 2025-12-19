package edu.itmo.soa.newservice2.eurekaclient.util;


import edu.itmo.soa.newservice2.eurekaclient.model.City;
import edu.itmo.soa.newservice2.eurekaclient.model.CityInput;
import edu.itmo.soa.newservice2.eurekaclient.model.CoordinatesInput;
import edu.itmo.soa.newservice2.eurekaclient.model.HumanInput;

public class CityMapper {
    public static CityInput toCityInput(City city) {
        CityInput input = new CityInput();
        input.setName(city.getName());
        if (city.getCoordinates() != null) {
            CoordinatesInput coords = new CoordinatesInput();
            coords.setX(city.getCoordinates().getX());
            coords.setY(city.getCoordinates().getY());
            input.setCoordinates(coords);
        }
        input.setArea(city.getArea());
        input.setPopulation(city.getPopulation());
        input.setMetersAboveSeaLevel(city.getMetersAboveSeaLevel());
        input.setEstablishmentDate(city.getEstablishmentDate());
        input.setPopulationDensity(city.getPopulationDensity());
        input.setGovernment(city.getGovernment());
        if (city.getGovernor() != null) {
            HumanInput gov = new HumanInput();
            gov.setAge(city.getGovernor().getAge());
            input.setGovernor(gov);
        }
        return input;
    }
}

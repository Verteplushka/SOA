package edu.itmo.soa.newservice2.eurekaclient.mapper;

import java.util.List;
import java.util.stream.Collectors;

public final class CityMapper {
    public static edu.itmo.soa.newservice2.eurekaclient.model.City toModel(edu.itmo.soa.newservice2.eurekaclient.client.City clientCity) {
        if (clientCity == null) {
            return null;
        }

        edu.itmo.soa.newservice2.eurekaclient.model.City modelCity =
                new edu.itmo.soa.newservice2.eurekaclient.model.City();

        modelCity.setId(clientCity.getId());
        modelCity.setName(clientCity.getName());
        modelCity.setCoordinates(CoordinatesMapper.toModel(clientCity.getCoordinates()));
        modelCity.setCreationDate(clientCity.getCreationDate());
        modelCity.setPopulation(clientCity.getPopulation());
        modelCity.setEstablishmentDate(clientCity.getEstablishmentDate());
        modelCity.setPopulationDensity(clientCity.getPopulationDensity());
        modelCity.setGovernment(GovernmentMapper.toModel(clientCity.getGovernment()));
        modelCity.setGovernor(HumanMapper.toModel(clientCity.getGovernor()));

        if (clientCity.getArea() != null) {
            modelCity.setArea(clientCity.getArea().intValue());
        }

        if (clientCity.getMetersAboveSeaLevel() != null) {
            modelCity.setMetersAboveSeaLevel(
                    clientCity.getMetersAboveSeaLevel().intValue()
            );
        }

        return modelCity;
    }

    public static edu.itmo.soa.newservice2.eurekaclient.client.City toClient(edu.itmo.soa.newservice2.eurekaclient.model.City modelCity) {
        if (modelCity == null) {
            return null;
        }

        edu.itmo.soa.newservice2.eurekaclient.client.City clientCity =
                new edu.itmo.soa.newservice2.eurekaclient.client.City();

        clientCity.setId(modelCity.getId());
        clientCity.setName(modelCity.getName());
        clientCity.setCoordinates(CoordinatesMapper.toClient(modelCity.getCoordinates()));
        clientCity.setCreationDate(modelCity.getCreationDate());
        clientCity.setPopulation(modelCity.getPopulation());
        clientCity.setEstablishmentDate(modelCity.getEstablishmentDate());
        clientCity.setPopulationDensity(modelCity.getPopulationDensity());
        clientCity.setGovernment(GovernmentMapper.toClient(modelCity.getGovernment()));
        clientCity.setGovernor(HumanMapper.toClient(modelCity.getGovernor()));

        if (modelCity.getArea() != null) {
            clientCity.setArea(modelCity.getArea().longValue());
        }

        if (modelCity.getMetersAboveSeaLevel() != null) {
            clientCity.setMetersAboveSeaLevel(
                    modelCity.getMetersAboveSeaLevel().longValue()
            );
        }

        return clientCity;
    }

    public static List<edu.itmo.soa.newservice2.eurekaclient.model.City> toModelList(List<edu.itmo.soa.newservice2.eurekaclient.client.City> clientList) {
        return clientList.stream().map(CityMapper::toModel).collect(Collectors.toList());
    }
}

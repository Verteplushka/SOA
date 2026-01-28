package edu.itmo.soa.newservice2.eurekaclient.model;

import jakarta.xml.bind.annotation.*;

import java.util.List;

@XmlRootElement(name = "cityPageResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CityPageResponse {

    @XmlElementWrapper(name = "cities")
    @XmlElement(name = "city")
    private List<City> cities;

    public CityPageResponse() {
    }

    public CityPageResponse(List<City> cities) {
        this.cities = cities;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }
}


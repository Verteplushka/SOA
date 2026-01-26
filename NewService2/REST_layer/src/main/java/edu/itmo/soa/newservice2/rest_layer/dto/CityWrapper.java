package edu.itmo.soa.newservice2.rest_layer.dto;

import com.example.soap.client.City;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class CityWrapper {

    @XmlElement(name = "city")
    private City city;

    public CityWrapper() {}

    public CityWrapper(City city) {
        this.city = city;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}

package edu.itmo.soa.newservice2.rest_layer.dto;

import com.example.soap.client.City;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class CityWrapper {

    @XmlElement(name = "city")
    private CityDto city;

    public CityWrapper() {}

    public CityWrapper(CityDto city) {
        this.city = city;
    }

    public CityDto getCity() {
        return city;
    }

    public void setCity(CityDto city) {
        this.city = city;
    }
}

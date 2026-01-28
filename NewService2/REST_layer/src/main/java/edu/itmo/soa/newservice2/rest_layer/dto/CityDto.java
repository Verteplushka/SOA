package edu.itmo.soa.newservice2.rest_layer.dto;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@XmlRootElement(name = "city")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CityDto {

    private int id;
    private String name;

    private CoordinatesDto coordinates;

    private String creationDate;
    private Integer area;
    private Long population;
    private Integer metersAboveSeaLevel;
    private String establishmentDate;
    private Float populationDensity;
    private String government;

    private GovernorDto governor;
}

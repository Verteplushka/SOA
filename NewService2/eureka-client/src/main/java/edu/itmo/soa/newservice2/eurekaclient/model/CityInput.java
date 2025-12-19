package edu.itmo.soa.newservice2.eurekaclient.model;

import edu.itmo.soa.newservice2.eurekaclient.model.adapter.LocalDateTimeAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@XmlRootElement(name = "CityInput")
@XmlAccessorType(XmlAccessType.FIELD)
public class CityInput {
    private String name;
    private CoordinatesInput coordinates;
    private Integer area;
    private long population;
    private Integer metersAboveSeaLevel;
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime establishmentDate;
    private Float populationDensity;
    private Government government;
    private HumanInput governor;
}

package edu.itmo.soa.newservice2.eurekaclient.model;

import edu.itmo.soa.newservice2.eurekaclient.model.adapter.LocalDateTimeAdapter;
import edu.itmo.soa.newservice2.eurekaclient.model.adapter.ZonedDateTimeAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;


import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@XmlRootElement(name = "City")
@XmlAccessorType(XmlAccessType.FIELD)
public class City {
    private int id;
    private String name;
    private Coordinates coordinates;
    @XmlJavaTypeAdapter(ZonedDateTimeAdapter.class)
    private ZonedDateTime creationDate;
    private Integer area;
    private long population;
    private Integer metersAboveSeaLevel;
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime establishmentDate;
    private Float populationDensity;
    private Government government;
    private Human governor;
}

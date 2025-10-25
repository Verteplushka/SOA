package model;

import jakarta.xml.bind.annotation.*;
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

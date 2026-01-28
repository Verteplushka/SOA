package edu.itmo.soa.newservice2.eurekaclient.model;

import edu.itmo.soa.newservice2.eurekaclient.model.adapter.LocalDateTimeAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;

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

    public CityInput() {
    }

    public CityInput(String name, CoordinatesInput coordinates, Integer area, long population,
                     Integer metersAboveSeaLevel, LocalDateTime establishmentDate, Float populationDensity,
                     Government government, HumanInput governor) {
        this.name = name;
        this.coordinates = coordinates;
        this.area = area;
        this.population = population;
        this.metersAboveSeaLevel = metersAboveSeaLevel;
        this.establishmentDate = establishmentDate;
        this.populationDensity = populationDensity;
        this.government = government;
        this.governor = governor;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CoordinatesInput getCoordinates() { return coordinates; }
    public void setCoordinates(CoordinatesInput coordinates) { this.coordinates = coordinates; }

    public Integer getArea() { return area; }
    public void setArea(Integer area) { this.area = area; }

    public long getPopulation() { return population; }
    public void setPopulation(long population) { this.population = population; }

    public Integer getMetersAboveSeaLevel() { return metersAboveSeaLevel; }
    public void setMetersAboveSeaLevel(Integer metersAboveSeaLevel) { this.metersAboveSeaLevel = metersAboveSeaLevel; }

    public LocalDateTime getEstablishmentDate() { return establishmentDate; }
    public void setEstablishmentDate(LocalDateTime establishmentDate) { this.establishmentDate = establishmentDate; }

    public Float getPopulationDensity() { return populationDensity; }
    public void setPopulationDensity(Float populationDensity) { this.populationDensity = populationDensity; }

    public Government getGovernment() { return government; }
    public void setGovernment(Government government) { this.government = government; }

    public HumanInput getGovernor() { return governor; }
    public void setGovernor(HumanInput governor) { this.governor = governor; }
}

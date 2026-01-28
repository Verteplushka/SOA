package edu.itmo.soa.newservice2.rest_layer.dto;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class MoveResultDto {

    @XmlElement
    private CityWrapper sourceCity;

    @XmlElement
    private CityWrapper targetCity;

    public CityWrapper getSourceCity() {
        return sourceCity;
    }

    public void setSourceCity(CityWrapper sourceCity) {
        this.sourceCity = sourceCity;
    }

    public CityWrapper getTargetCity() {
        return targetCity;
    }

    public void setTargetCity(CityWrapper targetCity) {
        this.targetCity = targetCity;
    }
}

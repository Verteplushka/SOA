package edu.itmo.soa.newservice2.rest_layer.dto;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class CountResultDto {

    @XmlElement
    private long totalPopulation;

    public CountResultDto() {}

    public CountResultDto(long totalPopulation) {
        this.totalPopulation = totalPopulation;
    }

    public long getTotalPopulation() {
        return totalPopulation;
    }

    public void setTotalPopulation(long totalPopulation) {
        this.totalPopulation = totalPopulation;
    }
}

package edu.itmo.soa.newservice2.eurekaclient.model.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "population")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class PopulationResponse {
    private long totalPopulation;
}

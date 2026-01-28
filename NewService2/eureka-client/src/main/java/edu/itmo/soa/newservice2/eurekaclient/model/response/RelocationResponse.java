package edu.itmo.soa.newservice2.eurekaclient.model.response;


import edu.itmo.soa.newservice2.eurekaclient.model.City;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;


@XmlRootElement(name = "relocationResult")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class RelocationResponse {
    private City sourceCity;
    private City targetCity;
}


package edu.itmo.soa.newservice2.rest_layer.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class GovernorDto {
    private long id;
    private int age;
}


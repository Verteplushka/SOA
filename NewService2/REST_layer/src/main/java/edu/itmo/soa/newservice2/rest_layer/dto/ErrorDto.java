package edu.itmo.soa.newservice2.rest_layer.dto;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorDto {

    @XmlValue
    private String message;

    public ErrorDto() {}

    public ErrorDto(String message) {
        this.message = message;
    }
}


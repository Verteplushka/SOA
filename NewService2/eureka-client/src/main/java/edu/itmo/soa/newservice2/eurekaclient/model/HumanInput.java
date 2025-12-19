package edu.itmo.soa.newservice2.eurekaclient.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "governor")
@XmlAccessorType(XmlAccessType.FIELD)
public class HumanInput {
    private int age;
}

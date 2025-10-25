package model;


import jakarta.xml.bind.annotation.*;
import lombok.Data;

@XmlRootElement(name = "relocationResult")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class RelocationResponse {
    private City sourceCity;
    private City targetCity;
}


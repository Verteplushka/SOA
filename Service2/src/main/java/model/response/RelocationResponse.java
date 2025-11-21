package model.response;


import jakarta.xml.bind.annotation.*;
import lombok.Data;
import model.City;

@XmlRootElement(name = "relocationResult")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class RelocationResponse {
    private City sourceCity;
    private City targetCity;
}


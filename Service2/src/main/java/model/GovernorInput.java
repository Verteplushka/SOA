package model;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@Data
@XmlRootElement(name = "governor")
@XmlAccessorType(XmlAccessType.FIELD)
public class GovernorInput {
    private int age;
}

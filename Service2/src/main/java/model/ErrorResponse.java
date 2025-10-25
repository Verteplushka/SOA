package model;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.time.Instant;
@Data
@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorResponse {
    private String error;
    private String message;
    private String timestamp;

    public ErrorResponse() {}

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
        this.timestamp = Instant.now().toString();
    }


}


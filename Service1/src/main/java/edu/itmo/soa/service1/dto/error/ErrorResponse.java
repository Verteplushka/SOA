package edu.itmo.soa.service1.dto.error;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "error")
public class ErrorResponse {
    private String error;
    private String message;
    private ZonedDateTime timestamp;
}


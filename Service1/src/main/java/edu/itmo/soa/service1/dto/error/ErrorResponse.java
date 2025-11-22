package edu.itmo.soa.service1.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
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
    @JacksonXmlProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd'T'hh:mm:ss.SSSX")
    private ZonedDateTime timestamp;
}


package edu.itmo.soa.service1.exception;

public class InvalidCityDataException extends RuntimeException{
    public InvalidCityDataException(String message) {
        super(message);
    }
}

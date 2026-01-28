package edu.itmo.soa.service1.exception;

public class CityAlreadyExistsException extends RuntimeException{
    public CityAlreadyExistsException(String message) {
        super(message);
    }
}

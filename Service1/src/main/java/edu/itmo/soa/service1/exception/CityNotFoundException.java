package edu.itmo.soa.service1.exception;

public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(String fieldName, Object value) {
        super("Город с " + fieldName + " = " + value + " не найден");
    }
}


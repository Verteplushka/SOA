package edu.itmo.soa.service1.exception;

public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(int id) {
        super("Город с id " + id + " не найден");
    }
}


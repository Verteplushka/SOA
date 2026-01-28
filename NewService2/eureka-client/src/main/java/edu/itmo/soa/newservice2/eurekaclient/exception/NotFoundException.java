package edu.itmo.soa.newservice2.eurekaclient.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

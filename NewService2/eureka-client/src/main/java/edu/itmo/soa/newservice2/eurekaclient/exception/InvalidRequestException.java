package edu.itmo.soa.newservice2.eurekaclient.exception;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}

package edu.itmo.soa.newservice2.eurekaclient.exception;

public class ApiErrorException extends RuntimeException {
    public ApiErrorException(String message) {
        super(message);
    }
}

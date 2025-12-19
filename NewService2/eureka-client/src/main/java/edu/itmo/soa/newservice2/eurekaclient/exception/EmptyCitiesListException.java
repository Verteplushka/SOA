package edu.itmo.soa.newservice2.eurekaclient.exception;

public class EmptyCitiesListException extends RuntimeException {
    public EmptyCitiesListException (String message) {
        super(message);
    }
}

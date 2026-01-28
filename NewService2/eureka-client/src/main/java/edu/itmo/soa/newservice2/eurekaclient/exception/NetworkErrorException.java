package edu.itmo.soa.newservice2.eurekaclient.exception;

public class NetworkErrorException extends RuntimeException {
    public NetworkErrorException(String message) {
        super(message);
    }
}

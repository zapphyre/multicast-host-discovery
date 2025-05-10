package org.zapphyre.discovery.exception;

public class NoLocalIpException extends RuntimeException {
    public NoLocalIpException(String message) {
        super(message);
    }
}

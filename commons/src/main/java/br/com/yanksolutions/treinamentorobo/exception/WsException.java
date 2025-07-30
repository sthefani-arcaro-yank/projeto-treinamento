package br.com.yanksolutions.treinamentorobo.exception;

public class WsException extends RuntimeException{

    public WsException() {
    }

    public WsException(String message) {
        super(message);
    }

    public WsException(String message, Throwable cause) {
        super(message, cause);
    }
}

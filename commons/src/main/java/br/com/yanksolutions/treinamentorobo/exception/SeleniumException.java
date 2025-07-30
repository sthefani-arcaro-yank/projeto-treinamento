package br.com.yanksolutions.treinamentorobo.exception;


public class SeleniumException extends RuntimeException{

    public SeleniumException() {
    }

    public SeleniumException(String message) {
        super(message);
    }

    public SeleniumException(String message, Throwable cause) {
        super(message, cause);
    }
}

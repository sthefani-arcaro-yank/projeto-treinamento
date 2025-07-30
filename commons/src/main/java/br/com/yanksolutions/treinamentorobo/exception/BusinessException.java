package br.com.yanksolutions.treinamentorobo.exception;

public class BusinessException extends RuntimeException{

    public BusinessException () {
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
